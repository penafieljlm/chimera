/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components.handler;

import java.util.concurrent.ConcurrentLinkedQueue;
import org.jnetpcap.packet.PcapPacket;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PDUAtomic;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PDUComposite;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class Assembler {

    private ConcurrentLinkedQueue<PDUComposite> queue;

    public Assembler() {
        this.queue = new ConcurrentLinkedQueue<PDUComposite>();
    }

    public PDUComposite poll() {
        return this.queue.poll();
    }

    public int size() {
        return this.queue.size();
    }

    public abstract boolean append(PDUAtomic segment);

    public abstract Assembler copyAssemblerType();

    protected void outputPDU(PDUComposite pdu) {
        this.queue.add(pdu);
    }
    
}
