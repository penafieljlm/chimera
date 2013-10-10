/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.Pdu;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ComponentStatistics extends ComponentActive {

    public final ConcurrentLinkedQueue<Pdu> inQueue;
    public final ConcurrentLinkedQueue<Pdu> outQueue;

    public ComponentStatistics(Assembly assembly,
            ConcurrentLinkedQueue<Pdu> inQueue,
            ConcurrentLinkedQueue<Pdu> outQueue) {
        super(assembly);
        this.inQueue = inQueue;
        this.outQueue = outQueue;
    }

    @Override
    protected void componentRun() throws Exception {
        while (super.running) {
            if (this.inQueue != null) {
                while (!this.inQueue.isEmpty()) {
                    Pdu pkt = this.inQueue.poll();
                    //processing here
                    
                    if(this.outQueue != null) {
                        this.outQueue.add(pkt);
                    }
                }
            }
        }
    }
}
