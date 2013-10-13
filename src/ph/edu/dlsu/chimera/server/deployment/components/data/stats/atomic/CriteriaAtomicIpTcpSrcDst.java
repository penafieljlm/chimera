/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data.stats.atomic;

import java.net.InetAddress;
import org.jnetpcap.protocol.tcpip.Tcp;
import ph.edu.dlsu.chimera.server.deployment.components.data.SocketPair;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;
import ph.edu.dlsu.chimera.util.ToolsPacket;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class CriteriaAtomicIpTcpSrcDst extends CriteriaAtomic {

    public final InetAddress source;
    public final int sourcePort;
    public final InetAddress destination;
    public final int destinationPort;

    public CriteriaAtomicIpTcpSrcDst() {
        this(null, -1, null, -1);
    }

    public CriteriaAtomicIpTcpSrcDst(InetAddress source,
            int sourcePort,
            InetAddress destination,
            int destinationPort) {
        super("socktcpsrcdst", "Source and Destination TCP Sockets");
        this.source = source;
        this.sourcePort = sourcePort;
        this.destination = destination;
        this.destinationPort = destinationPort;
    }

    @Override
    public CriteriaAtomic createInstance(PduAtomic pkt) {
        SocketPair socks = ToolsPacket.getConnection(pkt.packet);
        if (socks != null && pkt.packet.hasHeader(new Tcp())) {
            return new CriteriaAtomicIpTcpSrcDst(socks.source, socks.sourcePort, socks.destination, socks.destinationPort);
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CriteriaAtomicIpTcpSrcDst other = (CriteriaAtomicIpTcpSrcDst) obj;
        if (this.source != other.source && (this.source == null || !this.source.equals(other.source))) {
            return false;
        }
        if (this.sourcePort != other.sourcePort) {
            return false;
        }
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
        hash = 37 * hash + (this.source != null ? this.source.hashCode() : 0);
        hash = 37 * hash + this.sourcePort;
        hash = 37 * hash + (this.destination != null ? this.destination.hashCode() : 0);
        hash = 37 * hash + this.destinationPort;
        return hash;
    }
    
    @Override
    public String getInstanceString() {
        return this.source.getHostAddress() + ":" + this.sourcePort + " -[IP:TCP]> " + this.destination.getHostAddress() + ":" + this.destinationPort;
    }
}
