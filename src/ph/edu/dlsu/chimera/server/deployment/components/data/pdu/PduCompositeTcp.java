/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data.pdu;

import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.server.deployment.components.assembler.Assembler;
import ph.edu.dlsu.chimera.server.core.Connection;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class PduCompositeTcp extends PduComposite {

    public PduCompositeTcp(ConcurrentLinkedQueue<PduAtomic> packets,
            Connection connection,
            Assembler assembler,
            boolean inbound) {
        super(packets, connection, assembler, inbound);
    }
}
