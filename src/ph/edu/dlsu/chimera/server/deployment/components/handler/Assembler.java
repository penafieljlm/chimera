/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components.handler;

import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduComposite;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class Assembler {

    private ConcurrentLinkedQueue<PduComposite> queue;

    public Assembler() {
        this.queue = new ConcurrentLinkedQueue<PduComposite>();
    }

    public PduComposite poll() {
        return this.queue.poll();
    }

    public int size() {
        return this.queue.size();
    }

    public abstract boolean append(PduAtomic segment);

    public abstract Assembler copyAssemblerType();

    protected void outputPDU(PduComposite pdu) {
        this.queue.add(pdu);
    }
    
}
