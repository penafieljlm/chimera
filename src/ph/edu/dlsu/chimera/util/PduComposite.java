/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.util;

import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.server.deployment.components.assembler.Assembler;
import ph.edu.dlsu.chimera.server.core.Connection;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class PduComposite extends Pdu {

    public final Connection connection;
    public final Assembler assembler;

    public PduComposite(Connection connection,
            Assembler assembler,
            boolean inbound,
            long timestampInNanos) {
        super(inbound, timestampInNanos);
        this.connection = connection;
        this.assembler = assembler;
    }
}
