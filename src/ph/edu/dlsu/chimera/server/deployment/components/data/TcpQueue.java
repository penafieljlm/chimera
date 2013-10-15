/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data;

import java.util.Collections;
import java.util.List;
import org.jnetpcap.protocol.tcpip.Tcp;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class TcpQueue {

    private final List<PduAtomic> inQueue;
    private int inNextSequenceNo; // or number of bytes I have received so far

    public TcpQueue() {
        this.inQueue = Collections.synchronizedList(Collections.EMPTY_LIST);
        this.inNextSequenceNo = 1;
    }

    public boolean add(PduAtomic packet) {
        if (packet.packet.hasHeader(new Tcp())) {
            Tcp tcp = packet.packet.getHeader(new Tcp());
            if (packet.inbound) {
                if (tcp.getPayloadLength() > 0 && this.inNextSequenceNo <= tcp.seq()) {
                    if (!this.contains(tcp)) {
                        return this.inQueue.add(packet);
                    }
                }
            }
        }
        return false;
    }

    public PduAtomic poll() {
        for (PduAtomic p : this.inQueue) {
            Tcp ptcp = p.packet.getHeader(new Tcp());
            if (ptcp.seq() == this.inNextSequenceNo && ptcp.getPayloadLength() > 0) {
                this.inNextSequenceNo += ptcp.getPayloadLength();
                this.inQueue.remove(p);
                return p;
            }
        }
        return null;
    }

    public boolean contains(Tcp tcp) {
        for (PduAtomic p : this.inQueue) {
            Tcp ptcp = p.packet.getHeader(new Tcp());
            if (ptcp.seq() == tcp.seq()) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return this.inQueue.isEmpty();
    }
}
