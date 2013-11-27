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
public class ComponentDecision extends ComponentActive {

    public static final String CHIMERA_CHAIN = "CHIMERA";
    public static final String FORWARD_CHAIN = "FORWARD";
    public final ModelLive model;
    public final IntermodulePipe<PduAtomic> inQueue;
    public final List<Object> rulesMap;
    public final InetAddress syslogServer;
    private long processed;

    public ComponentDecision(IntermodulePipe<PduAtomic> inQueue,
            ModelLive model,
            List<Object> rulesMap,
            InetAddress syslogServer) {
        this.model = model;
        this.inQueue = inQueue;
        this.rulesMap = rulesMap;
        this.syslogServer = syslogServer;
        this.processed = 0;
    }

    @Override
    protected void componentRun() throws Exception {
        IpTables iptable = new IpTables("filter");
        //clean up
        for (String ch : iptable.getAllChains()) {
            if (ch.startsWith(ComponentDecision.CHIMERA_CHAIN)) {
                iptable.deleteChain(ch);
            }
        }
        iptable.commit();
        //create master chain
        iptable.createChain(ComponentDecision.CHIMERA_CHAIN);
        iptable.commit();
        while (super.running) {
            if (this.inQueue != null) {
                while (!this.inQueue.isEmpty()) {
                    PduAtomic pkt = this.inQueue.poll();
                    if (pkt.ingress) {
                        //integrity check
                        if (this.rulesMap != null) {
                            if (iptable.getAllRules(ComponentDecision.CHIMERA_CHAIN).size() != this.rulesMap.size()) {
                                throw new Exception("Error: [Decision] The CHIMERA iptables chain has been tampered with.");
                            }
                        }
                        //connection evaluation
                        if (this.evaluateAgainstConnection(pkt)) {
                            //attack
                            this.logConnectionViolation(pkt);
                            if (this.rulesMap != null) {
                                //add rules
                                if (!this.rulesMap.contains(pkt.getConnection().sockets)) {
                                    IpRule rule = pkt.getConnection().sockets.createRule();
                                    if (rule != null) {
                                        iptable.appendEntry(ComponentDecision.FORWARD_CHAIN, rule);
                                        iptable.commit();
                                    }
                                    this.rulesMap.add(pkt.getConnection().sockets);
                                }
                            }
                        } else {
                            //normal
                            if (this.rulesMap != null) {
                                //remove rules
                                if (this.rulesMap.contains(pkt.getConnection().sockets)) {
                                    int idx = this.rulesMap.indexOf(pkt.getConnection().sockets);
                                    iptable.deleteNumEntry(ComponentDecision.FORWARD_CHAIN, idx);
                                    iptable.commit();
                                    this.rulesMap.remove(pkt.getConnection().sockets);
                                }
                            }
                        }
                        //criteria evaluation
                        HashMap<Criteria, Boolean> crtEval = this.evaluateAgainstCriterias(pkt);
                        for (Criteria crt : crtEval.keySet()) {
                            CriteriaInstance inst = crt.createInstance(pkt.packet);
                            if (crtEval.get(crt)) {
                                //attack
                                this.logCriteriaViolation(pkt, crt, pkt.getConnection());
                                if (this.rulesMap != null) {
                                    //create rules
                                    if (!this.rulesMap.contains(inst)) {
                                        IpRule rule = inst.criteria.createRule(pkt.packet);
                                        if (rule != null) {
                                            iptable.appendEntry(ComponentDecision.FORWARD_CHAIN, rule);
                                            iptable.commit();
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
                                        iptable.deleteNumEntry(ComponentDecision.FORWARD_CHAIN, idx);
                                        iptable.commit();
                                        this.rulesMap.remove(inst);
                                    }
                                }
                            }
                        }
                        this.processed++;
                    } else {
                        throw new Exception("Error: [Decision] Encountered egress packet.");
                    }
                }
            } else {
                throw new Exception("Error: [Decision] inQueue is null.");
            }
        }
        //clean up
        for (String ch : iptable.getAllChains()) {
            if (ch.startsWith(ComponentDecision.CHIMERA_CHAIN)) {
                iptable.deleteChain(ch);
            }
        }
        iptable.commit();
    }

    protected boolean evaluateAgainstConnection(PduAtomic pkt) {
        String[] coreInst = UtilsTraining.getCoreInstance(pkt);
        String[] connInst = UtilsTraining.getConnectionInstance(pkt);
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
        return evalResult == 1.0;
    }

    protected HashMap<Criteria, Boolean> evaluateAgainstCriterias(PduAtomic pkt) {
        HashMap<Criteria, Boolean> report = new HashMap<>();
        String[] coreInst = UtilsTraining.getCoreInstance(pkt);
        for (Criteria crt : this.model.criteriaTrees.keySet()) {
            String[] crtInst = UtilsTraining.getCriteriaInstance(crt, pkt);
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
            report.put(crt, evalResult == 1.0);
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
