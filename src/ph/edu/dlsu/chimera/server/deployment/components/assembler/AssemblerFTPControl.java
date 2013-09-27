/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components.assembler;

import net.sourceforge.jpcap.net.Packet;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PDU;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class AssemblerFTPControl extends Assembler {

    @Override
    public boolean isDone() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean assemblePDU(Packet segment) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Assembler duplicate() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void reset() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected PDU producePDU() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
