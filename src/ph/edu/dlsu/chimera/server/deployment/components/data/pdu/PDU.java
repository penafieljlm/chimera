/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components.data.pdu;

import java.util.concurrent.ConcurrentLinkedQueue;
import org.jnetpcap.packet.PcapPacket;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class PDU {

    private final ConcurrentLinkedQueue<PcapPacket> packets;

    public PDU(ConcurrentLinkedQueue<PcapPacket> packets) {
        this.packets = packets;
    }

}
