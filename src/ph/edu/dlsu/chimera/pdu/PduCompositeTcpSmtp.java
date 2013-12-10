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
public class PduCompositeTcpSmtp extends PduCompositeTcp {

    public final boolean isCommand;
    public final String smtp;

    public PduCompositeTcpSmtp(Connection connection,
            Assembler assembler,
            String smtp,
            boolean isCommand,
            boolean ingress,
            long timestampInNanos) {
        super(connection, assembler, ingress, timestampInNanos);
        this.isCommand = isCommand;
        this.smtp = smtp;
    }

    @Override
    public long size() {
        return this.smtp.length();
    }

    @Override
    public String printDebug() {
        return this.smtp;
    }
}
