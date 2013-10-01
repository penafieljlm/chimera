/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components.data;

import java.util.Collections;
import java.util.List;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.tcpip.Tcp;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class TCPPacketSequence {

    private final List<PcapPacket> list;
    private int nextSequenceNo;

    public TCPPacketSequence() {
        this.list = Collections.synchronizedList(Collections.EMPTY_LIST);
        this.nextSequenceNo = 1;
    }

    public boolean add(PcapPacket packet) {
        if(packet.hasHeader(new Tcp())) {
            Tcp tcp = packet.getHeader(new Tcp());
            if(tcp.getPayloadLength() > 0) {
                if(!this.contains(tcp)) {
                    return this.list.add(packet);
                }
            }
        }
        return false;
    }

    public PcapPacket poll() {
        for(PcapPacket p : this.list) {
            Tcp ptcp = p.getHeader(new Tcp());
            if(ptcp.seq() == this.nextSequenceNo && ptcp.getPayloadLength() > 0) {
                this.nextSequenceNo += ptcp.getPayloadLength();
                this.list.remove(p);
                return p;
            }
        }
        return null;
    }

    public boolean contains(Tcp tcp) {
        for(PcapPacket p : this.list) {
            Tcp ptcp = p.getHeader(new Tcp());
            if(ptcp.seq() == tcp.seq())
                return true;
        }
        return false;
    }

}
