/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data.pdu;

import org.jnetpcap.Pcap;
import org.jnetpcap.packet.PcapPacket;
import ph.edu.dlsu.chimera.server.deployment.components.data.ConnectionData;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PduAtomicTcp extends PduAtomic implements PduTcp {

    public final ConnectionData connectionData;

    public PduAtomicTcp(Pcap sourceInjector,
            PcapPacket packet,
            boolean inbound,
            ConnectionData connectionData) {
        super(sourceInjector, packet, inbound);
        this.connectionData = connectionData;
    }

    public ConnectionData getConnectionData() {
        return this.connectionData;
    }
}
