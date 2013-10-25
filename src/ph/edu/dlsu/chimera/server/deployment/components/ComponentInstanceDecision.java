/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Instance;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ComponentInstanceDecision extends ComponentActive {

    public final Classifier model;
    public final ConcurrentLinkedQueue<PduAtomic> inQueue;
    public final ConcurrentLinkedQueue<PduAtomic> outQueue;
    private long processed;

    public ComponentInstanceDecision(Assembly assembly,
            Classifier model,
            ConcurrentLinkedQueue<PduAtomic> inQueue,
            ConcurrentLinkedQueue<PduAtomic> outQueue) {
        super(assembly);
        this.model = model;
        this.inQueue = inQueue;
        this.outQueue = outQueue;
        this.processed = 0;
    }

    @Override
    protected void componentRun() throws Exception {
        while (super.running) {
            if (this.inQueue != null) {
                while (!this.inQueue.isEmpty()) {
                    PduAtomic pkt = this.inQueue.poll();
                    if (pkt.inbound) {
                        if (pkt.getInstanceHeaders() != null || pkt.getInstanceData() != null) {
                            if (pkt.getInstanceHeaders().length == pkt.getInstanceData().length) {
                                //forward packet
                                if (this.model != null) {
                                    if (this.evaluatePacket(pkt)) {
                                        if (this.outQueue != null) {
                                            this.processed++;
                                            this.outQueue.add(pkt);
                                        } else {
                                            throw new Exception("Error: [Instance Decision] outQueue is null.");
                                        }
                                    } else {
                                        //TODO : logging mechanism
                                    }
                                } else {
                                    throw new Exception("Error: [Instance Decision] model is null.");
                                }
                            } else {
                                throw new Exception("Error: [Instance Decision] Encountered packet with mimatched instance headers and data.");
                            }
                        } else {
                            throw new Exception("Error: [Instance Decision] Encounter packet with null instances.");
                        }
                    } else {
                        throw new Exception("Error: [Instance Decision] Encountered outbound packet.");
                    }
                }
            } else {
                throw new Exception("Error: [Instance Decision] inQueue is null.");
            }
        }
    }

    private boolean evaluatePacket(PduAtomic pkt) {
        Instance inst = new Instance(pkt.getInstanceHeaders().length);
        for (int i = 0; i < pkt.getInstanceData().length; i++) {
            inst.setValue(i, pkt.getInstanceData()[i]);
        }
        try {
            if (this.model.classifyInstance(inst) == 0.0) {
                return true;
            }
        } catch (Exception ex) {
        }
        return false;
    }
}
