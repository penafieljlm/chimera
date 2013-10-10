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
public abstract class PduComposite extends Pdu {

    private final ConcurrentLinkedQueue<PduAtomic> packets;

    public PduComposite(ConcurrentLinkedQueue<PduAtomic> packets) {
        this.packets = packets;
    }

    public PduAtomic[] getPackets() {
        return (PduAtomic[]) this.packets.toArray();
    }

}
