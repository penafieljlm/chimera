/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components.data.pdu;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class PDUComposite extends PDU {

    private final ConcurrentLinkedQueue<PDUAtomic> packets;

    public PDUComposite(ConcurrentLinkedQueue<PDUAtomic> packets) {
        this.packets = packets;
    }

    public PDUAtomic[] getPackets() {
        return (PDUAtomic[]) this.packets.toArray();
    }

}
