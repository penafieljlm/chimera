/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.deployment.components.data.IntermodulePipe;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.Pdu;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ComponentDebugger<TPdu extends Pdu> extends ComponentActive {

    public final IntermodulePipe<TPdu> inQueue;
    public final IntermodulePipe<TPdu> outQueue;

    public ComponentDebugger(Assembly assembly,
            IntermodulePipe<TPdu> inQueue,
            IntermodulePipe<TPdu> outQueue) {
        super(assembly);
        this.setPriority(Thread.NORM_PRIORITY);
        this.inQueue = inQueue;
        this.outQueue = outQueue;
        if (this.inQueue != null) {
            this.inQueue.setReader(this);
        }
        if (this.outQueue != null) {
            this.outQueue.setWriter(this);
        }
    }

    @Override
    protected void componentRun() throws Exception {
        while (super.running) {
            if (this.inQueue != null) {
                if (this.inQueue.isEmpty()) {
                    synchronized (this) {
                        this.wait();
                    }
                }
                while (!this.inQueue.isEmpty()) {
                    TPdu pkt = this.inQueue.poll();
                    synchronized (pkt) {
                        System.out.println(pkt.printDebug());
                        if (this.outQueue != null) {
                            this.outQueue.add(pkt);
                        }
                    }
                }
            } else {
                throw new Exception("Error: [Debugger] inQueue is null.");
            }
        }
    }
}
