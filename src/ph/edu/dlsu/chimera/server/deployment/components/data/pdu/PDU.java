/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components.data.pdu;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class Pdu {

    public final boolean inbound;

    public Pdu(boolean inbound) {
        this.inbound = inbound;
    }

    public String printDebug() {
        return null;
    }

}
