/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PDU;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PDUAtomic;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ComponentDebugger extends ComponentActive {

    public final ConcurrentLinkedQueue<PDU> inQueue;
    public final ConcurrentLinkedQueue<PDU> outQueue;

    public ComponentDebugger(Assembly assembly, ConcurrentLinkedQueue<PDU> inQueue, ConcurrentLinkedQueue<PDU> outQueue) {
        super(assembly);
        this.inQueue = inQueue;
        this.outQueue = outQueue;
    }

    @Override
    protected void componentRun() {
        while(super.running) {
            if(this.inQueue != null) {
                while(!this.inQueue.isEmpty()) {
                    PDU pkt = this.inQueue.poll();
                    System.out.println(pkt.printDebug());
                    if(this.outQueue != null) {
                        this.outQueue.add(pkt);
                    }
                }
            }
        }
    }
}
