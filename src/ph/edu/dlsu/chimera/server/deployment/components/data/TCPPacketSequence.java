/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components.data;

import java.util.Collections;
import java.util.List;
import net.sourceforge.jpcap.net.TCPPacket;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class TCPPacketSequence {

    private final List<TCPPacket> list;
    private int nextSequenceNo;

    public TCPPacketSequence() {
        this.list = Collections.synchronizedList(Collections.EMPTY_LIST);
        this.nextSequenceNo = 1;
    }

    public boolean add(TCPPacket packet) {
        if(packet.getData().length > 0) {
            if(!this.contains(packet)) {
                return this.list.add(packet);
            }
        }
        return false;
    }

    public TCPPacket poll() {
        for(TCPPacket p : this.list) {
            if(p.getSequenceNumber() == this.nextSequenceNo && p.getData().length > 0) {
                this.nextSequenceNo += p.getData().length;
                this.list.remove(p);
                return p;
            }
        }
        return null;
    }

    public boolean contains(TCPPacket packet) {
        for(TCPPacket p : this.list) {
            if(p.getSequenceNumber() == packet.getSequenceNumber())
                return true;
        }
        return false;
    }

}
