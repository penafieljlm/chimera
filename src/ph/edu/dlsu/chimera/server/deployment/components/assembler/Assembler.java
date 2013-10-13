/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components.assembler;

import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduComposite;
import ph.edu.dlsu.chimera.server.deployment.components.data.Statistics;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class Assembler extends Statistics {

    private ConcurrentLinkedQueue<PduComposite> queue;

    public Assembler(long timeCreatedNanos) {
        super(timeCreatedNanos);
        this.queue = new ConcurrentLinkedQueue<PduComposite>();
    }

    public PduComposite poll() {
        return this.queue.poll();
    }

    public int size() {
        return this.queue.size();
    }

    public boolean append(PduAtomic segment) {
        super.commitEncounter(segment);
        return true;
    }

    public abstract Assembler createAssemblerInstance(PduAtomic firstPacket);

    protected void outputPDU(PduComposite pdu) {
        this.queue.add(pdu);
    }
    
}
