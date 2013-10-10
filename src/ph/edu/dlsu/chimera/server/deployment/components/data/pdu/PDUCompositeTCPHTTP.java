/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data.pdu;

import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.server.deployment.components.data.ConnectionData;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PduCompositeTcpHttp extends PduCompositeTcp {

    public final String header;
    public final String body;

    public PduCompositeTcpHttp(ConcurrentLinkedQueue<PduAtomic> packets,
            ConnectionData connectionData,
            String header,
            String body) {
        super(packets, connectionData);
        this.header = header;
        this.body = body;
    }

    @Override
    public String printDebug() {
        return this.header + this.body;
    }
}
