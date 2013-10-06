/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data;

import java.util.Collections;
import java.util.List;
import org.jnetpcap.protocol.tcpip.Tcp;
import ph.edu.dlsu.chimera.core.Packet;
import ph.edu.dlsu.chimera.util.PacketTools;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class TCPPacketSequence {

    private final List<Packet> list;
    private int nextSequenceNo; // or number of bytes I have received so far

    public TCPPacketSequence() {
        this.list = Collections.synchronizedList(Collections.EMPTY_LIST);
        this.nextSequenceNo = 1;
    }

    public boolean add(Packet packet) {
        if (packet.packet.hasHeader(new Tcp())) {
            Tcp tcp = packet.packet.getHeader(new Tcp());
            if (tcp.getPayloadLength() > 0 && this.nextSequenceNo <= tcp.seq()) {
                if (!this.contains(tcp)) {
                    return this.list.add(packet);
                }
            }
            if (tcp.getPayloadLength() > 0 && tcp.seq() < this.nextSequenceNo) {
                packet.pcap.sendPacket(PacketTools.createTcpAck(tcp, this.nextSequenceNo));
                return false;
            }
        }
        return false;
    }

    public Packet poll() {
        for (Packet p : this.list) {
            Tcp ptcp = p.packet.getHeader(new Tcp());
            if (ptcp.seq() == this.nextSequenceNo && ptcp.getPayloadLength() > 0) {
                this.nextSequenceNo += ptcp.getPayloadLength();
                this.list.remove(p);
                p.pcap.sendPacket(PacketTools.createTcpAck(ptcp, this.nextSequenceNo));
                return p;
            }
        }
        return null;
    }

    public boolean contains(Tcp tcp) {
        for (Packet p : this.list) {
            Tcp ptcp = p.packet.getHeader(new Tcp());
            if (ptcp.seq() == tcp.seq()) {
                return true;
            }
        }
        return false;
    }
}
