/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components.data.pdu;

import java.util.concurrent.ConcurrentLinkedQueue;
import net.sourceforge.jpcap.net.Packet;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PDUHTTP extends PDU {

    private String header;
    private String body;

    public PDUHTTP(ConcurrentLinkedQueue<Packet> packets, String header, String body) {
        super(packets);
        this.header = header;
        this.body = body;
    }

}
