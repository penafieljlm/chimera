/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data.pdu;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PDUCompositeHTTP extends PDUComposite {

    public final String header;
    public final String body;

    public PDUCompositeHTTP(ConcurrentLinkedQueue<PDUAtomic> packets, String header, String body) {
        super(packets);
        this.header = header;
        this.body = body;
    }

    @Override
    public String printDebug() {
        return this.header + this.body;
    }
}
