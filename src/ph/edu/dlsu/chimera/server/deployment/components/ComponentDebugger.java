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
public class ComponentDebugger<TPdu extends Pdu> extends ComponentActive {

    public final ConcurrentLinkedQueue<TPdu> inQueue;
    public final ConcurrentLinkedQueue<TPdu> outQueue;

    public ComponentDebugger(Assembly assembly,
            ConcurrentLinkedQueue<TPdu> inQueue,
            ConcurrentLinkedQueue<TPdu> outQueue) {
        super(assembly);
        this.setPriority(Thread.NORM_PRIORITY);
        this.inQueue = inQueue;
        this.outQueue = outQueue;
    }

    @Override
    protected void componentRun() throws Exception {
        while (super.running) {
            if (this.inQueue != null) {
                while (!this.inQueue.isEmpty()) {
                    TPdu pkt = this.inQueue.poll();
                    System.out.println(pkt.printDebug());
                    if (this.outQueue != null) {
                        this.outQueue.add(pkt);
                    }
                }
            }
        }
    }
}
