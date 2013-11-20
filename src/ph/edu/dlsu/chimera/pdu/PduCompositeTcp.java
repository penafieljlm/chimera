/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.pdu;

import ph.edu.dlsu.chimera.assembler.Assembler;
import ph.edu.dlsu.chimera.core.Connection;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class PduCompositeTcp extends PduComposite {

    public PduCompositeTcp(Connection connection,
            Assembler assembler,
            boolean ingress,
            long timestampInNanos) {
        super(connection, assembler, ingress, timestampInNanos);
    }
}
