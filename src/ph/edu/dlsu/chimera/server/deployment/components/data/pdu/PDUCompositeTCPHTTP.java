/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data.pdu;

import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.server.deployment.components.assembler.Assembler;
import ph.edu.dlsu.chimera.server.deployment.components.data.Connection;
import ph.edu.dlsu.chimera.server.deployment.components.data.SocketPair;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PduCompositeTcpHttp extends PduCompositeTcp {

    public final String header;
    public final String body;

    public PduCompositeTcpHttp(ConcurrentLinkedQueue<PduAtomic> packets,
            Connection connection,
            Assembler assembler,
            String header,
            String body,
            boolean inbound) {
        super(packets, connection, assembler, inbound);
        this.header = header;
        this.body = body;
    }

    @Override
    public long size() {
        return this.header.length() + this.body.length();
    }

    @Override
    public String printDebug() {
        return this.header + this.body;
    }
}
