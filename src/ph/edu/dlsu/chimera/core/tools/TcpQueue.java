package ph.edu.dlsu.chimera.core.tools;

import java.util.Collections;
import java.util.List;
import org.jnetpcap.protocol.tcpip.Tcp;
import ph.edu.dlsu.chimera.core.TrafficDirection;
import ph.edu.dlsu.chimera.core.PduAtomic;

/**
 * An instance of this class constitutes an object which can arrange the
 * contents of a TCP Stream. Still in experimental mode. Not used in the first.
 * release.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class TcpQueue {

    private final List<PduAtomic> inQueue;
    private int inNextSequenceNo; // or number of bytes I have received so far

    /**
     * Constructs a new TcpQueue object
     */
    public TcpQueue() {
        this.inQueue = Collections.synchronizedList(Collections.EMPTY_LIST);
        this.inNextSequenceNo = 1;
    }

    /**
     * Adds a new packet to this TCP Stream.
     *
     * @param packet The packet
     * @return True if successful
     */
    public boolean add(PduAtomic packet) {
        try {
            if (packet.packet.hasHeader(new Tcp())) {
                Tcp tcp = packet.packet.getHeader(new Tcp());
                if (packet.direction == TrafficDirection.Ingress) {
                    if (tcp.getPayloadLength() > 0 && this.inNextSequenceNo <= tcp.seq()) {
                        if (!this.contains(tcp)) {
                            return this.inQueue.add(packet);
                        }
                    }
                }
            }
        } catch (Exception ex) {

        }
        return false;
    }

    /**
     *
     * @return The next packet in the TCP Stream
     */
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

    /**
     * Checks if this TCPQueue contains the specified TCP header
     *
     * @param tcp The tcp header of the packet
     * @return
     */
    public boolean contains(Tcp tcp) {
        for (PduAtomic p : this.inQueue) {
            Tcp ptcp = p.packet.getHeader(new Tcp());
            if (ptcp.seq() == tcp.seq()) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return True if this TCPQueue is empty
     */
    public boolean isEmpty() {
        return this.inQueue.isEmpty();
    }
}
