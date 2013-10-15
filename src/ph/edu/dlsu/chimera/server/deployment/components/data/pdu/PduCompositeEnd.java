/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data.pdu;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PduCompositeEnd extends PduComposite {

    public PduCompositeEnd(boolean inbound) {
        super(null, null, null, inbound);
    }

    @Override
    public long size() {
        return -1;
    }
}
