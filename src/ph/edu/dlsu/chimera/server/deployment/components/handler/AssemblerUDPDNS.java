/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components.handler;

import net.sourceforge.jpcap.net.Packet;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PDU;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class AssemblerUDPDNS extends Assembler {

    @Override
    public boolean isDone() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean append(Packet segment) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Assembler copyHandlerType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void reset() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected PDU internalPDUConstruct() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
