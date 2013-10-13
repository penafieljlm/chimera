/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data.pdu;

import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.server.deployment.components.data.Connection;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PduCompositeTcp extends PduComposite implements PduTcp {

    public final Connection connection;

    public PduCompositeTcp(ConcurrentLinkedQueue<PduAtomic> packets,
            Connection connection,
            boolean inbound) {
        super(packets, inbound);
        this.connection = connection;
    }

    public Connection getConnection() {
        return this.connection;
    }
}
