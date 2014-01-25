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
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Ip4;
import ph.edu.dlsu.chimera.core.Statistics;
import ph.edu.dlsu.chimera.core.TrafficDirection;
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
import weka.core.Instances;

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
    public final Instances connDataInstances;
    public final HashMap<Criteria, Instances> criteriaDataInstances;
    public final boolean active;
    private IpTables iptable;

    public ComponentDecision(IntermodulePipe<PduAtomic> inQueue,
            ModelLive model,
            List<Object> rulesMap,
            InetAddress syslogServer,
            boolean active) {
        super(inQueue, null);
        this.model = model;
        this.rulesMap = rulesMap;
        this.syslogServer = syslogServer;
        this.connDataInstances = new Instances("connection", this.model.connectionSubModel.attributes, 0);
        this.criteriaDataInstances = new HashMap<Criteria, Instances>();
        for (Criteria crt : this.model.criteriaSubModels.keySet()) {
            this.criteriaDataInstances.put(crt, new Instances(crt.expression, this.model.criteriaSubModels.get(crt).attributes, 0));
        }
        this.active = active;
    }

    @Override
    protected void preLoop() throws Exception {
        if (this.active) {
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
    }

    @Override
    protected PduAtomic process(PduAtomic input) throws Exception {
        if (input.direction == TrafficDirection.Ingress) {
            //integrity check
            if (this.active) {
                if (this.rulesMap != null) {
                    if (this.iptable.getAllRules(ComponentDecision.CHIMERA_CHAIN).size() != this.rulesMap.size()) {
                        throw new Exception("Error: [Decision] The CHIMERA iptables chain has been tampered with.");
                    }
                }
            }
            //connection evaluation
            if (this.evaluateAgainstConnection(input)) {
                //attack
                this.logConnectionViolation(input);
                if (this.active) {
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
                }
            } else {
                //normal
                if (this.active) {
                    if (this.rulesMap != null) {
                        //remove rules
                        if (this.rulesMap.contains(input.getConnection().sockets)) {
                            if (this.active) {
                                int idx = this.rulesMap.indexOf(input.getConnection().sockets);
                                this.iptable.deleteNumEntry(ComponentDecision.FORWARD_CHAIN, idx);
                                this.iptable.commit();
                                this.rulesMap.remove(input.getConnection().sockets);
                            }
                        }
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
                    if (this.active) {
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
                    }
                } else {
                    //normal
                    if (this.active) {
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
            }
        } else {
            throw new Exception("Error: [Decision] Encountered egress packet.");
        }
        return null;
    }

    @Override
    protected void postLoop() throws Exception {
        //clean up
        if (this.active) {
            for (String ch : this.iptable.getAllChains()) {
                if (ch.startsWith(ComponentDecision.CHIMERA_CHAIN)) {
                    this.iptable.deleteChain(ch);
                }
            }
            this.iptable.commit();
        }
    }

    protected boolean evaluateAgainstConnection(PduAtomic pkt) {
        boolean allow = true;
        Object[] connInst = UtilsTraining.getConnectionInstance(pkt);
        if (!UtilsTraining.instanceIsNull(connInst)) {
            Object[] coreInst = UtilsTraining.getCoreInstance(pkt);
            Object[] inst = UtilsArray.concat(coreInst, connInst);
            Instance _inst = new Instance(inst.length);
            _inst.setDataset(this.connDataInstances);
            for (int i = 0; i < inst.length; i++) {
                if (inst[i] instanceof Number) {
                    _inst.setValue(i, ((Number) inst[i]).doubleValue());
                } else {
                    _inst.setValue(i, (String) inst[i]);
                }
            }
            double evalResult = 0.0;
            try {
                evalResult = this.model.connectionSubModel.tree.classifyInstance(_inst);
            } catch (Exception ex) {
            }
            allow = evalResult == 0.0;
        }
        return allow;
    }

    protected HashMap<Criteria, Boolean> evaluateAgainstCriterias(PduAtomic pkt) {
        HashMap<Criteria, Boolean> report = new HashMap<Criteria, Boolean>();
        Object[] coreInst = UtilsTraining.getCoreInstance(pkt);
        for (Criteria crt : this.model.criteriaSubModels.keySet()) {
            boolean allow = true;
            Object[] crtInst = UtilsTraining.getCriteriaInstance(crt, pkt);
            if (!UtilsTraining.instanceIsNull(crtInst)) {
                Object[] inst = UtilsArray.concat(coreInst, crtInst);
                Instance _inst = new Instance(inst.length);
                _inst.setDataset(this.criteriaDataInstances.get(crt));
                for (int i = 0; i < inst.length; i++) {
                    if (inst[i] instanceof Number) {
                        _inst.setValue(i, ((Number) inst[i]).doubleValue());
                    } else {
                        _inst.setValue(i, (String) inst[i]);
                    }
                }
                double evalResult = 0.0;
                try {
                    evalResult = this.model.criteriaSubModels.get(crt).tree.classifyInstance(_inst);
                } catch (Exception ex) {
                }
                allow = evalResult == 0.0;
            }
            report.put(crt, allow);
        }
        return report;
    }

    protected void logConnectionViolation(PduAtomic pkt) {
        System.out.println("Attack Logged (Connection Tree)");
        if (pkt.packet.hasHeader(new Ethernet())) {
            Ethernet eth = pkt.packet.getHeader(new Ethernet());
            try {
                StringBuilder sb = new StringBuilder();
                for (byte b : eth.source()) {
                    sb = sb.append(String.format("%02x:", b));
                }
                System.out.println("    " + sb.toString());
                sb = new StringBuilder();
                for (byte b : eth.destination()) {
                    sb = sb.append(String.format("%02x:", b));
                }
                System.out.println("    " + sb.toString());
            } catch (Exception ex) {
            }
        }
        if (pkt.packet.hasHeader(new Ip4())) {
            Ip4 ip = pkt.packet.getHeader(new Ip4());
            try {
                System.out.println("    " + InetAddress.getByAddress(ip.source()));
            } catch (Exception ex) {
            }
        }
        if (this.syslogServer != null) {
            LogAttackConnection log = new LogAttackConnection(new Date(), pkt.getConnection());
            Syslog.warning(this.syslogServer, "CHIMERA:AttackLogged", JsonWriter.toJson(log));
        }
    }

    protected void logCriteriaViolation(PduAtomic pkt, Criteria criteria, Statistics statistics) {
        System.out.println("Attack Logged (Criteria Tree)");
        if (pkt.packet.hasHeader(new Ethernet())) {
            Ethernet eth = pkt.packet.getHeader(new Ethernet());
            try {
                StringBuilder sb = new StringBuilder();
                for (byte b : eth.source()) {
                    sb = sb.append(String.format("%02x:", b));
                }
                System.out.println("    Eth Source....... " + sb.toString());
                sb = new StringBuilder();
                for (byte b : eth.destination()) {
                    sb = sb.append(String.format("%02x:", b));
                }
                System.out.println("    Eth Destination.. " + sb.toString());
            } catch (Exception ex) {
            }
        }
        if (pkt.packet.hasHeader(new Ip4())) {
            Ip4 ip = pkt.packet.getHeader(new Ip4());
            try {
                System.out.println("    Ip Source........ " + InetAddress.getByAddress(ip.source()));
                System.out.println("    Ip Destination... " + InetAddress.getByAddress(ip.destination()));
            } catch (Exception ex) {
            }
        }
        if (this.syslogServer != null) {
            LogAttackCriteria log = new LogAttackCriteria(new Date(), criteria, pkt.packet, statistics);
            Syslog.warning(this.syslogServer, "CHIMERA:AttackLogged", JsonWriter.toJson(log));
        }
    }
}
