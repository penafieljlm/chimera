/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data.stats;

import java.net.InetAddress;
import org.jnetpcap.protocol.tcpip.Tcp;
import ph.edu.dlsu.chimera.server.deployment.components.data.SocketPair;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;
import ph.edu.dlsu.chimera.util.ToolsPacket;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class CriteriaIpTcpDst extends Criteria {

    public final InetAddress destination;
    public final int destinationPort;

    public CriteriaIpTcpDst() {
        this(null, -1);
    }

    public CriteriaIpTcpDst(InetAddress destination,
            int destinationPort) {
        super("socktcpdst", "Destination TCP Socket");
        this.destination = destination;
        this.destinationPort = destinationPort;
    }

    @Override
    public Criteria createInstance(PduAtomic pkt) {
        SocketPair socks = ToolsPacket.getConnection(pkt.packet);
        if (socks != null && pkt.packet.hasHeader(new Tcp())) {
            return new CriteriaIpTcpDst(socks.destination, socks.destinationPort);
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final CriteriaIpTcpDst other = (CriteriaIpTcpDst) obj;
        if (this.destination != other.destination && (this.destination == null || !this.destination.equals(other.destination))) {
            return false;
        }
        if (this.destinationPort != other.destinationPort) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.destination != null ? this.destination.hashCode() : 0);
        hash = 67 * hash + this.destinationPort;
        return hash;
    }

    @Override
    public String getInstanceString() {
        return "any:any -> " + this.destination.getHostAddress() + ":" + this.destinationPort;
    }
}
