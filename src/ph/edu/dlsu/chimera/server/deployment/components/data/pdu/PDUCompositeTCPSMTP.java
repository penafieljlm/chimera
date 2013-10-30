/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data.pdu;

import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.server.deployment.components.assembler.Assembler;
import ph.edu.dlsu.chimera.server.core.Connection;
import ph.edu.dlsu.chimera.server.core.SocketPair;

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
            boolean inbound,
            long timestampInNanos) {
        super(connection, assembler, inbound, timestampInNanos);
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
