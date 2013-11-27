/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.components;

import de.tbsol.iptablesjava.IpTables;
import de.tbsol.iptablesjava.rules.IpRule;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.core.criteria.Criteria;
import ph.edu.dlsu.chimera.core.criteria.CriteriaInstance;
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
    public final List<CriteriaInstance> rulesMap;
    private long processed;

    public ComponentDecision(IntermodulePipe<PduAtomic> inQueue,
            ModelLive model,
            List<CriteriaInstance> rulesMap) {
        this.model = model;
        this.inQueue = inQueue;
        this.rulesMap = rulesMap;
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
        //create master chain
        iptable.createChain(ComponentDecision.CHIMERA_CHAIN);
        while (super.running) {
            if (this.inQueue != null) {
                while (!this.inQueue.isEmpty()) {
                    PduAtomic pkt = this.inQueue.poll();
                    if (pkt.ingress) {
                        //integrity check
                        if (iptable.getAllRules(ComponentDecision.CHIMERA_CHAIN).size() != this.rulesMap.size()) {
                            throw new Exception("Error: [Decision] The CHIMERA iptables chain has been tampered with.");
                        }
                        HashMap<Criteria, Boolean> evaluation = this.evaluate(pkt);
                        for (Criteria crt : evaluation.keySet()) {
                            CriteriaInstance inst = crt.createInstance(pkt.packet);
                            if (evaluation.get(crt)) {
                                //attack - try to create rules
                                if (!this.rulesMap.contains(inst)) {
                                    IpRule rule = inst.criteria.createRule(pkt.packet);
                                    if (rule != null) {
                                        iptable.appendEntry(ComponentDecision.FORWARD_CHAIN, rule);
                                    }
                                    this.rulesMap.add(inst);
                                }
                            } else {
                                //normal - try to remove rules
                                if (this.rulesMap.contains(inst)) {
                                    int idx = this.rulesMap.indexOf(inst);
                                    iptable.deleteNumEntry(ComponentDecision.FORWARD_CHAIN, idx);
                                    this.rulesMap.remove(inst);
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
    }

    protected HashMap<Criteria, Boolean> evaluate(PduAtomic pkt) {
        HashMap<Criteria, Boolean> report = new HashMap<>();
        String[] coreInst = UtilsTraining.getCoreInstance(pkt);
        for (Criteria crt : this.model.criteriaTrees.keySet()) {
            String[] crtInst = UtilsTraining.getCriteriaInstance(crt, pkt);
            String[] _crtInst = UtilsArray.concat(coreInst, crtInst);
            Instance inst = new Instance(_crtInst.length);
            for (int i = 0; i < _crtInst.length; i++) {
                inst.setValue(i, _crtInst[i]);
            }
            double evalResult = 0.0;
            try {
                evalResult = this.model.criteriaTrees.get(crt).classifyInstance(inst);
            } catch (Exception ex) {
            }
            report.put(crt, evalResult == 1.0);
        }
        return report;
    }
}
