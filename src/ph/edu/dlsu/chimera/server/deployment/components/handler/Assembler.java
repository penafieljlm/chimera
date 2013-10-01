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
public abstract class Assembler {

    public abstract boolean isDone();

    public abstract boolean appendPDU(PcapPacket segment);

    public abstract boolean assemblePDU();

    public PDU toPdu() {
        if(this.isDone()) {
            return this.internalPDUConstruct();
        }
        return null;
    }

    public abstract Assembler copyHandlerType();

    protected abstract PDU internalPDUConstruct();
    
}
