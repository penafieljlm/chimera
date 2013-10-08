/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data;

import java.util.Collections;
import java.util.List;
import org.jnetpcap.protocol.tcpip.Tcp;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PDUAtomic;

/**
 * responsible for sure delivery
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class TcpSent {

    public static final long TCP_TIMEOUT_NANO = 300000000000L;

    private final List<PDUAtomic> sent;

    public TcpSent() {
        this.sent = Collections.synchronizedList(Collections.EMPTY_LIST);
    }

    /**
     * called by decision/injector
     * @param packet - inbound data packet
     * @return
     */
    public synchronized boolean sent(PDUAtomic packet) {
        if (packet.inbound) {
            if (packet.packet.hasHeader(new Tcp())) {
                Tcp tcp = packet.packet.getHeader(new Tcp());
                if (!this.contains(tcp)) {
                    return this.sent.add(packet);
                }
            }
        }
        return false;
    }

    public boolean contains(Tcp tcp) {
        for (PDUAtomic p : this.sent) {
            Tcp ptcp = p.packet.getHeader(new Tcp());
            if (ptcp.seq() == tcp.seq()) {
                return true;
            }
        }
        return false;
    }

    /**
     * called by connection data
     * @param packet - outbound ack packet
     * @return
     */
    public synchronized boolean acknowledge(PDUAtomic packet) {
        if (!packet.inbound) {
            if (packet.packet.hasHeader(new Tcp())) {
                Tcp tcp = packet.packet.getHeader(new Tcp());
                boolean result = false;
                for (PDUAtomic p : this.sent) {
                    Tcp ptcp = p.packet.getHeader(new Tcp());
                    if (ptcp.seq() < tcp.ack()) {
                        result |= this.sent.remove(p);
                    }
                }
                return result;
            }
        }
        return false;
    }

    public synchronized List<PDUAtomic> getTimedOutPackets() {
        List<PDUAtomic> result = Collections.synchronizedList(Collections.EMPTY_LIST);
        for(PDUAtomic pkt : this.sent) {
            if(TcpSent.TCP_TIMEOUT_NANO < pkt.getTimeSinceSent()) {
                result.add(pkt);
            }
        }
        return result;
    }

    public boolean isEmpty() {
        return this.sent.isEmpty();
    }
}
