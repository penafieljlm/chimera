/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components.handler;

import org.jnetpcap.packet.PcapPacket;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PDU;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class ProtocolHandler {

    public abstract boolean isDone();

    public abstract boolean appendPDU(PcapPacket segment);

    public PDU poll() {
        if(this.isDone()) {
            PDU result = this.producePDU();
            this.reset();
            return result;
        }
        return null;
    }

    public abstract ProtocolHandler copyHandlerType();

    protected abstract void reset();

    protected abstract PDU producePDU();
    
}
