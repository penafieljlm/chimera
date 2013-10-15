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
public class CriteriaAtomicIpTcpSrc extends Criteria {

    public final InetAddress source;
    public final int sourcePort;

    public CriteriaAtomicIpTcpSrc() {
        this(null, -1);
    }

    public CriteriaAtomicIpTcpSrc(InetAddress source,
            int sourcePort) {
        super("socktcpsrc", "Source TCP Socket");
        this.source = source;
        this.sourcePort = sourcePort;
    }

    @Override
    public Criteria createInstance(PduAtomic pkt) {
        SocketPair socks = ToolsPacket.getConnection(pkt.packet);
        if (socks != null && pkt.packet.hasHeader(new Tcp())) {
            return new CriteriaAtomicIpTcpSrc(socks.source, socks.sourcePort);
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
        final CriteriaAtomicIpTcpSrc other = (CriteriaAtomicIpTcpSrc) obj;
        if (this.source != other.source && (this.source == null || !this.source.equals(other.source))) {
            return false;
        }
        if (this.sourcePort != other.sourcePort) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.source != null ? this.source.hashCode() : 0);
        hash = 83 * hash + this.sourcePort;
        return hash;
    }
    @Override
    public String getInstanceString() {
        return this.source.getHostAddress() + ":" + this.sourcePort + " -[IP:TCP]> any:any";
    }
}
