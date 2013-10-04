/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components.handler;

import java.util.concurrent.ConcurrentLinkedQueue;
import org.jnetpcap.packet.PcapPacket;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PDU;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class Assembler {

    private ConcurrentLinkedQueue<PDU> queue;

    public Assembler() {
        this.queue = new ConcurrentLinkedQueue<PDU>();
    }

    public PDU poll() {
        return this.queue.poll();
    }

    public int size() {
        return this.queue.size();
    }

    public abstract boolean append(PcapPacket segment);

    public abstract Assembler copyHandlerType();

    protected void outputPDU(PDU pdu) {
        this.queue.add(pdu);
    }
    
}
