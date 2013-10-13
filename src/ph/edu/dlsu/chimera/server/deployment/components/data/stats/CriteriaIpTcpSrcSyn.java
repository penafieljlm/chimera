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
public class CriteriaIpTcpSrcSyn extends Criteria {

    public final InetAddress source;
    public final int sourcePort;

    public CriteriaIpTcpSrcSyn() {
        this(null, -1);
    }

    public CriteriaIpTcpSrcSyn(InetAddress source,
            int sourcePort) {
        super("socktcpsrcsyn", "Source TCP Socket Session Creation");
        this.source = source;
        this.sourcePort = sourcePort;
    }

    @Override
    public Criteria createInstance(PduAtomic pkt) {
        SocketPair socks = ToolsPacket.getConnection(pkt.packet);
        if (socks != null && pkt.packet.hasHeader(new Tcp())) {
            Tcp tcp = pkt.packet.getHeader(new Tcp());
            if (tcp.flags_SYN() && !tcp.flags_ACK()) {
                return new CriteriaIpTcpSrcSyn(socks.source, socks.sourcePort);
            }
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
        if (this.source != other.destination && (this.source == null || !this.source.equals(other.destination))) {
            return false;
        }
        if (this.sourcePort != other.destinationPort) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.source != null ? this.source.hashCode() : 0);
        hash = 67 * hash + this.sourcePort;
        return hash;
    }

    @Override
    public String getInstanceString() {
        return this.source.getHostAddress() + ":" + this.sourcePort + " ->[SYN]-> any:any";
    }
}
