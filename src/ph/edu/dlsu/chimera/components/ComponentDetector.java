/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.components;

import com.cedarsoftware.util.io.JsonWriter;
import de.tbsol.iptablesjava.rules.IpRule;
import java.net.InetAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogIF;
import ph.edu.dlsu.chimera.core.Statistics;
import ph.edu.dlsu.chimera.core.TrafficDirection;
import ph.edu.dlsu.chimera.core.criteria.Criteria;
import ph.edu.dlsu.chimera.core.criteria.CriteriaInstance;
import ph.edu.dlsu.chimera.core.logs.Log;
import ph.edu.dlsu.chimera.core.logs.LogAttackConnection;
import ph.edu.dlsu.chimera.core.logs.LogAttackCriteria;
import ph.edu.dlsu.chimera.core.model.ModelLive;
import ph.edu.dlsu.chimera.core.tools.IntermodulePipe;
import ph.edu.dlsu.chimera.core.PduAtomic;
import ph.edu.dlsu.chimera.rules.RulesManager;
import ph.edu.dlsu.chimera.util.UtilsArray;
import ph.edu.dlsu.chimera.util.UtilsTraining;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ComponentDetector extends ComponentActiveProcessor<PduAtomic, PduAtomic> {

    public final ModelLive model;
    public final RulesManager rulesManager;
    public final SyslogIF syslogLogger;
    public final Instances connDataInstances;
    public final HashMap<Criteria, Instances> criteriaDataInstances;
    public final ConcurrentLinkedQueue<Log> logs;

    public ComponentDetector(IntermodulePipe<PduAtomic> inQueue,
            ModelLive model,
            RulesManager rulesManager,
            InetAddress syslogServer,
            int syslogPort) {
        super(inQueue, null);
        this.model = model;
        this.rulesManager = rulesManager;
        if (syslogServer != null) {
            this.syslogLogger = Syslog.getInstance("udp");
            this.syslogLogger.getConfig().setHost(syslogServer.getHostAddress());
            this.syslogLogger.getConfig().setPort(syslogPort);
        } else {
            this.syslogLogger = null;
        }
        this.connDataInstances = new Instances("connection", this.model.connectionSubModel.attributes, 0);
        this.connDataInstances.setClassIndex(this.connDataInstances.numAttributes() - 1);
        this.criteriaDataInstances = new HashMap<Criteria, Instances>();
        for (Criteria crt : this.model.criteriaSubModels.keySet()) {
            this.criteriaDataInstances.put(crt, new Instances(crt.expression, this.model.criteriaSubModels.get(crt).attributes, 0));
            this.criteriaDataInstances.get(crt).setClassIndex(this.criteriaDataInstances.get(crt).numAttributes() - 1);
        }
        this.logs = new ConcurrentLinkedQueue<Log>();
    }

    private void evaluate(PduAtomic input) throws Exception {
        //connection evaluation
        i++;
        boolean atk = false;
        if (!this.evaluateAgainstConnection(input)) {
            //attack
            atk = true;
            this.logConnectionViolation(input);
            if (this.rulesManager != null) {
                //add rules
                if (input.getConnection() != null && !this.rulesManager.contains(input.getConnection().sockets)) {
                    IpRule rule = input.getConnection().sockets.createRule();
                    if (rule != null) {
                        rule.setJump(RulesManager.DROP_JUMP);
                        this.rulesManager.append(input.getConnection(), rule);
                    }
                }
            }
        }
        //criteria evaluation
        HashMap<Criteria, Boolean> crtEval = this.evaluateAgainstCriterias(input);
        for (Criteria crt : crtEval.keySet()) {
            CriteriaInstance inst = crt.createInstance(input.packet);
            if (!crtEval.get(crt)) {
                //attack
                atk = true;
                this.logCriteriaViolation(input, crt, input.getConnection());
                if (this.rulesManager != null) {
                    //create rules
                    if (!this.rulesManager.contains(inst)) {
                        IpRule rule = inst.criteria.createRule(input.packet);
                        if (rule != null) {
                            rule.setJump(RulesManager.DROP_JUMP);
                            this.rulesManager.append(inst, rule);
                        }
                    }
                }
            }
        }
        System.out.println(i + "," + input.getProtocolName() + "," + ((atk) ? "Attack" : "Normal"));
    }

    @Override
    protected PduAtomic process(PduAtomic input) throws Exception {
        if (input.direction == TrafficDirection.Ingress) {
            if (this.rulesManager != null) {
                synchronized (this.rulesManager) {
                    //integrity check
                    if (this.rulesManager != null && this.rulesManager.isTampered()) {
                        throw new Exception("Error: [Detector] The CHIMERA iptables chain has been tampered with.");
                    }
                    this.evaluate(input);
                    if (this.rulesManager.hasUncommitedChanges()) {
                        this.rulesManager.commit();
                    }
                }
            } else {
                this.evaluate(input);
            }
        } else {
            throw new Exception("Error: [Detector] Encountered egress packet.");
        }
        return null;
    }

    @Override
    protected void postLoop() throws Exception {
        //clean up
        if (this.rulesManager != null) {
            this.rulesManager.free();
        }
    }

    protected boolean evaluateAgainstConnection(PduAtomic pkt) {
        boolean allow = true;
        Object[] connInst = UtilsTraining.getConnectionInstance(pkt, this.connDataInstances);
        try {
            if (!UtilsTraining.instanceIsNull(connInst)) {
                Object[] coreInst = UtilsTraining.getCoreInstance(pkt, this.connDataInstances);
                Object[] inst = UtilsArray.concat(coreInst, connInst);
                Instance _inst = new Instance(inst.length + 1);
                _inst.setDataset(this.connDataInstances);
                for (int i = 0; i < inst.length - 1; i++) {
                    if (inst[i] instanceof Number) {
                        _inst.setValue(i, ((Number) inst[i]).doubleValue());
                    } else {
                        _inst.setValue(i, (String) inst[i]);
                    }
                }
                _inst.setMissing(_inst.numAttributes() - 1);
                double evalResult = (this.model.connectionSubModel.attackClass == 1.0) ? 0.0 : 1.0;
                try {
                    evalResult = this.model.connectionSubModel.tree.classifyInstance(_inst);
                } catch (Exception ex) {
                }
                allow = evalResult != this.model.connectionSubModel.attackClass;
            }
        } catch (Exception ex) {
        }
        return allow;
    }

    protected HashMap<Criteria, Boolean> evaluateAgainstCriterias(PduAtomic pkt) {
        HashMap<Criteria, Boolean> report = new HashMap<Criteria, Boolean>();
        for (Criteria crt : this.model.criteriaSubModels.keySet()) {
            boolean allow = true;
            Object[] coreInst = UtilsTraining.getCoreInstance(pkt, this.criteriaDataInstances.get(crt));
            Object[] crtInst = UtilsTraining.getCriteriaInstance(crt, pkt, this.criteriaDataInstances.get(crt));
            try {
                if (!UtilsTraining.instanceIsNull(crtInst)) {
                    Object[] inst = UtilsArray.concat(coreInst, crtInst);
                    Instance _inst = new Instance(inst.length + 1);
                    _inst.setDataset(this.criteriaDataInstances.get(crt));
                    for (int i = 0; i < inst.length; i++) {
                        if (inst[i] instanceof Number) {
                            _inst.setValue(i, ((Number) inst[i]).doubleValue());
                        } else {
                            _inst.setValue(i, (String) inst[i]);
                        }
                    }
                    _inst.setMissing(_inst.numAttributes() - 1);
                    double evalResult = (this.model.criteriaSubModels.get(crt).attackClass == 1.0) ? 0.0 : 1.0;
                    try {
                        StringBuilder attb = new StringBuilder();
                        StringBuilder valb = new StringBuilder();
                        for (int i = 0; i < _inst.numValues() - 1; i++) {
                            attb = attb.append("|").append(_inst.attribute(i).name()).append("|");
                            if (_inst.attribute(i).isNominal()) {
                                valb = valb.append("|").append(_inst.stringValue(i)).append("|");
                            } else {
                                valb = valb.append("|").append(_inst.value(i)).append("|");
                            }
                        }
                        evalResult = this.model.criteriaSubModels.get(crt).tree.classifyInstance(_inst);
                    } catch (Exception ex) {
                    }
                    allow = evalResult != this.model.criteriaSubModels.get(crt).attackClass;
                }
            } catch (Exception ex) {
            }
            report.put(crt, allow);
        }
        return report;
    }

    int i = 0;

    protected void logConnectionViolation(PduAtomic pkt) {
        LogAttackConnection log = new LogAttackConnection(new Date(), pkt.getConnection());
        this.logs.add(log);
        if (this.syslogLogger != null) {
            this.syslogLogger.alert(JsonWriter.toJson(log));
        }
    }

    protected void logCriteriaViolation(PduAtomic pkt, Criteria criteria, Statistics statistics) {
        LogAttackCriteria log = new LogAttackCriteria(new Date(), criteria, pkt.packet, statistics);
        this.logs.add(log);
        if (this.syslogLogger != null) {
            this.syslogLogger.alert(JsonWriter.toJson(log));
        }
    }
}
