/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.components;

import de.tbsol.iptablesjava.IpTables;
import de.tbsol.iptablesjava.rules.IpRule;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.core.PolicyChain;
import ph.edu.dlsu.chimera.core.criteria.Criteria;
import ph.edu.dlsu.chimera.core.criteria.CriteriaInstance;
import ph.edu.dlsu.chimera.core.model.ModelLive;
import ph.edu.dlsu.chimera.pdu.PduAtomic;
import ph.edu.dlsu.chimera.util.UtilsArray;
import ph.edu.dlsu.chimera.util.UtilsTraining;
import weka.core.Instance;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ComponentDecision extends ComponentActive {

    public static final String CHIMERA_CHAIN = "CHIMERA_CHAIN";
    public final ModelLive model;
    public final ConcurrentLinkedQueue<PduAtomic> inQueue;
    public final ConcurrentLinkedQueue<PduAtomic> outQueue;
    public final ConcurrentHashMap<CriteriaInstance, PolicyChain> blockedList;
    public final IpTables iptable;
    public final String chain;
    private long processed;

    public ComponentDecision(ModelLive model,
            ConcurrentLinkedQueue<PduAtomic> inQueue,
            ConcurrentLinkedQueue<PduAtomic> outQueue,
            ConcurrentHashMap<CriteriaInstance, PolicyChain> blockedList,
            IpTables iptable,
            String chain) {
        this.model = model;
        this.inQueue = inQueue;
        this.outQueue = outQueue;
        this.blockedList = blockedList;
        this.iptable = iptable;
        this.chain = chain;
        this.processed = 0;
    }

    @Override
    protected void componentRun() throws Exception {
        //clean up
        for (String ch : this.iptable.getAllChains()) {
            if (ch.startsWith(ComponentDecision.CHIMERA_CHAIN)) {
                this.iptable.deleteChain(ch);
            }
        }
        //create master chain
        this.iptable.createChain(ComponentDecision.CHIMERA_CHAIN);
        while (super.running) {
            if (this.inQueue != null) {
                while (!this.inQueue.isEmpty()) {
                    PduAtomic pkt = this.inQueue.poll();
                    if (pkt.ingress) {
                        HashMap<Criteria, Boolean> evaluation = this.evaluate(pkt);
                        for (Criteria crt : evaluation.keySet()) {
                            CriteriaInstance inst = crt.createInstance(pkt.packet);
                            if (evaluation.get(crt)) {
                                //attack - try to create rules
                                if (!this.blockedList.contains(inst)) {
                                    int newPos = this.iptable.getAllRules(ComponentDecision.CHIMERA_CHAIN).size();
                                    IpRule[] rules = inst.criteria.getRules(pkt.packet);
                                    if (rules.length > 0) {
                                        PolicyChain ruleChains = new PolicyChain(newPos, rules.length);
                                        //create rule chains
                                        for (int i = 0; i < ruleChains.size(); i++) {
                                            String ruleChainName = ComponentDecision.CHIMERA_CHAIN + "(" + inst.getChainName() + "[" + i + "])";
                                            ruleChains.setSubChain(i, ruleChainName);
                                            this.iptable.createChain(ruleChainName);
                                        }
                                        //connect rule chains
                                        for (int i = 0; i < ruleChains.size(); i++) {
                                            if (i + 1 < ruleChains.size()) {
                                                rules[i].setJump(ruleChains.getSubChain(i + 1));
                                            } else {
                                                rules[i].setJump("DROP");
                                            }
                                            IpRule returnRule = new IpRule();
                                            returnRule.setJump("RETURN");
                                            this.iptable.appendEntry(ruleChains.getSubChain(i), rules[i]);
                                            this.iptable.appendEntry(ruleChains.getSubChain(i), returnRule);
                                        }
                                        //attach rule chains to master chain
                                        IpRule jumpRule = new IpRule();
                                        jumpRule.setJump(ruleChains.getSubChain(0));
                                        this.iptable.appendEntry(ComponentDecision.CHIMERA_CHAIN, jumpRule);
                                        //commit
                                        this.blockedList.put(inst, ruleChains);
                                        this.iptable.commit();
                                    }
                                }
                            } else {
                                //normal - try to remove rules
                                if (this.blockedList.contains(inst)) {
                                    PolicyChain ruleChains = this.blockedList.get(inst);
                                    //detach rule chains from master list
                                    this.iptable.deleteNumEntry(ComponentDecision.CHIMERA_CHAIN, ruleChains.getPosition());
                                    for (CriteriaInstance i : this.blockedList.keySet()) {
                                        int curPos = this.blockedList.get(i).getPosition();
                                        if (curPos > ruleChains.getPosition()) {
                                            this.blockedList.get(i).setPosition(curPos - 1);
                                        }
                                    }
                                    //delete rule chains
                                    for (int i = 0; i < ruleChains.size(); i++) {
                                        this.iptable.deleteChain(ruleChains.getSubChain(i));
                                    }
                                    //commit
                                    this.blockedList.remove(inst);
                                    this.iptable.commit();
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
        for (String ch : this.iptable.getAllChains()) {
            if (ch.startsWith(ComponentDecision.CHIMERA_CHAIN)) {
                this.iptable.deleteChain(ch);
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
