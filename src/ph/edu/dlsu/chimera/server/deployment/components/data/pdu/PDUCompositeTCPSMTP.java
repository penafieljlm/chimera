/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data.pdu;

import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.server.deployment.components.data.Connection;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PduCompositeTcpSmtp extends PduCompositeTcp {

    public final boolean isCommand;
    public final String smtp;

    public PduCompositeTcpSmtp(ConcurrentLinkedQueue<PduAtomic> packets,
            Connection connection,
            String smtp,
            boolean isCommand,
            boolean inbound) {
        super(packets, connection, inbound);
        this.isCommand = isCommand;
        this.smtp = smtp;
    }

    @Override
    public String printDebug() {
        return this.smtp;
    }
}
