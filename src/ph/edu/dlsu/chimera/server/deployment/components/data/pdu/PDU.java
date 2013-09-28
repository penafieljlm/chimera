/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components.data.pdu;

import java.util.concurrent.ConcurrentLinkedQueue;
import net.sourceforge.jpcap.net.Packet;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class PDU {

    private final ConcurrentLinkedQueue<Packet> packets;

    public PDU(ConcurrentLinkedQueue<Packet> packets) {
        this.packets = packets;
    }

}
