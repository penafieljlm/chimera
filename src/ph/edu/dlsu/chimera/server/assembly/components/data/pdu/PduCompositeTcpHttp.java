/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.assembly.components.data.pdu;

import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.server.assembly.components.assembler.Assembler;
import ph.edu.dlsu.chimera.core.Connection;
import ph.edu.dlsu.chimera.core.SocketPair;
import ph.edu.dlsu.chimera.server.assembly.components.data.pdu.PduCompositeTcp;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PduCompositeTcpHttp extends PduCompositeTcp {

    public final String header;
    public final String body;

    public PduCompositeTcpHttp(Connection connection,
            Assembler assembler,
            String header,
            String body,
            boolean inbound,
            long timestampInNanos) {
        super(connection, assembler, inbound, timestampInNanos);
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
