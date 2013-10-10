/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data.pdu;

import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.server.deployment.components.data.ConnectionData;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PduCompositeTcpSmtp extends PduCompositeTcp {

    public final boolean isCommand;
    public final String smtp;

    public PduCompositeTcpSmtp(ConcurrentLinkedQueue<PduAtomic> packets,
            ConnectionData connectionData,
            String smtp,
            boolean isCommand) {
        super(packets, connectionData);
        this.isCommand = isCommand;
        this.smtp = smtp;
    }

    @Override
    public String printDebug() {
        return this.smtp;
    }
}
