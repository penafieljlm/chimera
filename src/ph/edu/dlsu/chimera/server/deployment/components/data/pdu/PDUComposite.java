/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components.data.pdu;

import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.server.deployment.components.assembler.Assembler;
import ph.edu.dlsu.chimera.server.deployment.components.data.Connection;
import ph.edu.dlsu.chimera.server.deployment.components.data.SocketPair;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class PduComposite extends Pdu {

    public final Connection connection;
    public final Assembler assembler;
    private final ConcurrentLinkedQueue<PduAtomic> packets;

    public PduComposite(ConcurrentLinkedQueue<PduAtomic> packets,
            Connection connection,
            Assembler assembler,
            boolean inbound) {
        super(inbound);
        this.connection = connection;
        this.assembler = assembler;
        this.packets = packets;
    }

    public PduAtomic[] getPackets() {
        return (PduAtomic[]) this.packets.toArray();
    }

    @Override
    public long timestampInNanos() {
        if(!this.packets.isEmpty()) {
            return this.packets.peek().timestampInNanos();
        }
        return -1;
    }
}
