/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.components;

import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.core.model.ModelLive;
import ph.edu.dlsu.chimera.pdu.PduAtomic;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ComponentDecision extends ComponentActive {

    public final ModelLive model;
    public final ConcurrentLinkedQueue<PduAtomic> inQueue;
    public final ConcurrentLinkedQueue<PduAtomic> outQueue;
    private long processed;

    public ComponentDecision(ModelLive model,
            ConcurrentLinkedQueue<PduAtomic> inQueue,
            ConcurrentLinkedQueue<PduAtomic> outQueue) {
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
                        
                    } else {
                        throw new Exception("Error: [Decision] Encountered outbound packet.");
                    }
                }
            } else {
                throw new Exception("Error: [Decision] inQueue is null.");
            }
        }
    }

    

}