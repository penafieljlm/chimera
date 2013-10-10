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
public class PduCompositeTcp extends PduComposite implements PduTcp {

    public final ConnectionData connectionData;

    public PduCompositeTcp(ConcurrentLinkedQueue<PduAtomic> packets,
            ConnectionData connectionData) {
        super(packets);
        this.connectionData = connectionData;
    }

    public ConnectionData getConnectionData() {
        return this.connectionData;
    }
}
