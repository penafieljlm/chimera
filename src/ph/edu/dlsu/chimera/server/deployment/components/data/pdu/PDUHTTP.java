/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components.data.pdu;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PDUHTTP extends PDU {

    private String header;
    private String body;

    public PDUHTTP(byte[] header, byte[] body) {
        this.header = new String(header);
        this.body = new String(body);
    }

}
