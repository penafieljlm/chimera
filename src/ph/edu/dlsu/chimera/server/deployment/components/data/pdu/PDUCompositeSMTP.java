/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data.pdu;

import java.util.concurrent.ConcurrentLinkedQueue;
import org.jnetpcap.packet.PcapPacket;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PDUCompositeSMTP extends PDUComposite {

    public final boolean isCommand;
    public final String smtp;

    public PDUCompositeSMTP(ConcurrentLinkedQueue<PDUAtomic> packets, String smtp, boolean isCommand) {
        super(packets);
        this.isCommand = isCommand;
        this.smtp = smtp;
    }

    @Override
    public String printDebug() {
        return this.smtp;
    }
}
