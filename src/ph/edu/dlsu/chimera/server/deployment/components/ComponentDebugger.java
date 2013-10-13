/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.Pdu;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduEnd;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ComponentDebugger extends ComponentActive {

    public final ConcurrentLinkedQueue<Pdu> inQueue;
    public final ConcurrentLinkedQueue<Pdu> outQueue;

    public ComponentDebugger(Assembly assembly, 
            ConcurrentLinkedQueue<Pdu> inQueue,
            ConcurrentLinkedQueue<Pdu> outQueue) {
        super(assembly);
        this.setPriority(Thread.NORM_PRIORITY);
        this.inQueue = inQueue;
        this.outQueue = outQueue;
    }

    @Override
    protected void componentRun() throws Exception {
        while(super.running) {
            if(this.inQueue != null) {
                while(!this.inQueue.isEmpty()) {
                    Pdu pkt = this.inQueue.poll();
                    if (pkt instanceof PduEnd) {
                        //signal end
                        if (this.outQueue != null) {
                            this.outQueue.add(pkt);
                        }
                        return;
                    }
                    System.out.println(pkt.printDebug());
                    if(this.outQueue != null) {
                        this.outQueue.add(pkt);
                    }
                }
            }
        }
    }
}
