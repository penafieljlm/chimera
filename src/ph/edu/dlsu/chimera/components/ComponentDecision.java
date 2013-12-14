/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.components;

import com.cedarsoftware.util.io.JsonWriter;
import com.protomatter.syslog.Syslog;
import de.tbsol.iptablesjava.IpTables;
import de.tbsol.iptablesjava.rules.IpRule;
import java.net.InetAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import ph.edu.dlsu.chimera.core.Statistics;
import ph.edu.dlsu.chimera.core.criteria.Criteria;
import ph.edu.dlsu.chimera.core.criteria.CriteriaInstance;
import ph.edu.dlsu.chimera.core.logs.LogAttackConnection;
import ph.edu.dlsu.chimera.core.logs.LogAttackCriteria;
import ph.edu.dlsu.chimera.core.model.ModelLive;
import ph.edu.dlsu.chimera.core.tools.IntermodulePipe;
import ph.edu.dlsu.chimera.pdu.PduAtomic;
import ph.edu.dlsu.chimera.util.UtilsArray;
import ph.edu.dlsu.chimera.util.UtilsTraining;
import weka.core.Instance;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ComponentDecision extends ComponentActiveProcessor<PduAtomic, PduAtomic> {

    public static final String CHIMERA_CHAIN = "CHIMERA";
    public static final String FORWARD_CHAIN = "FORWARD";
    public final ModelLive model;
    public final List<Object> rulesMap;
    public final InetAddress syslogServer;
    private IpTables iptable;

    public ComponentDecision(IntermodulePipe<PduAtomic> inQueue,
            ModelLive model,
            List<Object> rulesMap,
            InetAddress syslogServer) {
        super(inQueue, null);
        this.model = model;
        this.rulesMap = rulesMap;
        this.syslogServer = syslogServer;
    }

    @Override
    protected void preLoop() throws Exception {
        this.iptable = new IpTables("filter");
        //clean up
        for (String ch : this.iptable.getAllChains()) {
            if (ch.startsWith(ComponentDecision.CHIMERA_CHAIN)) {
                this.iptable.deleteChain(ch);
            }
        }
        this.iptable.commit();
        //create master chain
        this.iptable.createChain(ComponentDecision.CHIMERA_CHAIN);
        this.iptable.commit();
    }

    @Override
    protected PduAtomic process(PduAtomic input) throws Exception {
        if (input.ingress) {
            //integrity check
            if (this.rulesMap != null) {
                if (this.iptable.getAllRules(ComponentDecision.CHIMERA_CHAIN).size() != this.rulesMap.size()) {
                    throw new Exception("Error: [Decision] The CHIMERA iptables chain has been tampered with.");
                }
            }
            //connection evaluation
            if (this.evaluateAgainstConnection(input)) {
                //attack
                this.logConnectionViolation(input);
                if (this.rulesMap != null) {
                    //add rules
                    if (!this.rulesMap.contains(input.getConnection().sockets)) {
                        IpRule rule = input.getConnection().sockets.createRule();
                        if (rule != null) {
                            this.iptable.appendEntry(ComponentDecision.FORWARD_CHAIN, rule);
                            this.iptable.commit();
                        }
                        this.rulesMap.add(input.getConnection().sockets);
                    }
                }
            } else {
                //normal
                if (this.rulesMap != null) {
                    //remove rules
                    if (this.rulesMap.contains(input.getConnection().sockets)) {
                        int idx = this.rulesMap.indexOf(input.getConnection().sockets);
                        this.iptable.deleteNumEntry(ComponentDecision.FORWARD_CHAIN, idx);
                        this.iptable.commit();
                        this.rulesMap.remove(input.getConnection().sockets);
                    }
                }
            }
            //criteria evaluation
            HashMap<Criteria, Boolean> crtEval = this.evaluateAgainstCriterias(input);
            for (Criteria crt : crtEval.keySet()) {
                CriteriaInstance inst = crt.createInstance(input.packet);
                if (crtEval.get(crt)) {
                    //attack
                    this.logCriteriaViolation(input, crt, input.getConnection());
                    if (this.rulesMap != null) {
                        //create rules
                        if (!this.rulesMap.contains(inst)) {
                            IpRule rule = inst.criteria.createRule(input.packet);
                            if (rule != null) {
                                this.iptable.appendEntry(ComponentDecision.FORWARD_CHAIN, rule);
                                this.iptable.commit();
                            }
                            this.rulesMap.add(inst);
                        }
                    }
                } else {
                    //normal
                    if (this.rulesMap != null) {
                        //remove rules
                        if (this.rulesMap.contains(inst)) {
                            int idx = this.rulesMap.indexOf(inst);
                            this.iptable.deleteNumEntry(ComponentDecision.FORWARD_CHAIN, idx);
                            this.iptable.commit();
                            this.rulesMap.remove(inst);
                        }
                    }
                }
            }
        } else {
            throw new Exception("Error: [Decision] Encountered egress packet.");
        }
        return null;
    }

    @Override
    protected void postLoop() throws Exception {
        //clean up
        for (String ch : this.iptable.getAllChains()) {
            if (ch.startsWith(ComponentDecision.CHIMERA_CHAIN)) {
                this.iptable.deleteChain(ch);
            }
        }
        this.iptable.commit();
    }

    protected boolean evaluateAgainstConnection(PduAtomic pkt) {
        boolean allow = true;
        String[] connInst = UtilsTraining.getConnectionInstance(pkt);
        if (!UtilsTraining.instanceIsNull(connInst)) {
            String[] coreInst = UtilsTraining.getCoreInstance(pkt);
            String[] inst = UtilsArray.concat(coreInst, connInst);
            Instance _inst = new Instance(inst.length);
            for (int i = 0; i < inst.length; i++) {
                _inst.setValue(i, inst[i]);
            }
            double evalResult = 0.0;
            try {
                evalResult = this.model.connectionTree.classifyInstance(_inst);
            } catch (Exception ex) {
            }
            allow = evalResult == 1.0;
        }
        return allow;
    }

    protected HashMap<Criteria, Boolean> evaluateAgainstCriterias(PduAtomic pkt) {
        HashMap<Criteria, Boolean> report = new HashMap<>();
        String[] coreInst = UtilsTraining.getCoreInstance(pkt);
        for (Criteria crt : this.model.criteriaTrees.keySet()) {
            boolean allow = true;
            String[] crtInst = UtilsTraining.getCriteriaInstance(crt, pkt);
            if (!UtilsTraining.instanceIsNull(crtInst)) {
                String[] inst = UtilsArray.concat(coreInst, crtInst);
                Instance _inst = new Instance(inst.length);
                for (int i = 0; i < inst.length; i++) {
                    _inst.setValue(i, inst[i]);
                }
                double evalResult = 0.0;
                try {
                    evalResult = this.model.criteriaTrees.get(crt).classifyInstance(_inst);
                } catch (Exception ex) {
                }
                allow = evalResult == 1.0;
            }
            report.put(crt, allow);
        }
        return report;
    }

    protected void logConnectionViolation(PduAtomic pkt) {
        if (this.syslogServer != null) {
            LogAttackConnection log = new LogAttackConnection(new Date(), pkt.getConnection());
            Syslog.warning(this.syslogServer, "CHIMERA:AttackLogged", JsonWriter.toJson(log));
        }
    }

    protected void logCriteriaViolation(PduAtomic pkt, Criteria criteria, Statistics statistics) {
        if (this.syslogServer != null) {
            LogAttackCriteria log = new LogAttackCriteria(new Date(), criteria, pkt.packet, statistics);
            Syslog.warning(this.syslogServer, "CHIMERA:AttackLogged", JsonWriter.toJson(log));
        }
    }
}
