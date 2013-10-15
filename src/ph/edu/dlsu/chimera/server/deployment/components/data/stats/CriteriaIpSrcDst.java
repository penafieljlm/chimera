/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data.stats;

import java.net.InetAddress;
import ph.edu.dlsu.chimera.server.deployment.components.data.SocketPair;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;
import ph.edu.dlsu.chimera.util.ToolsPacket;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class CriteriaIpSrcDst extends Criteria {

    public final InetAddress source;
    public final InetAddress destination;

    public CriteriaIpSrcDst() {
        this(null, null);
    }

    public CriteriaIpSrcDst(InetAddress source,
            InetAddress destination) {
        super("ipsrcdst", "Source and Destination IP");
        this.source = source;
        this.destination = destination;
    }

    @Override
    public Criteria createInstance(PduAtomic pkt) {
        SocketPair socks = ToolsPacket.getSocketPair(pkt.packet);
        if (socks != null) {
            return new CriteriaIpSrcDst(socks.source, socks.destination);
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
        final CriteriaIpSrcDst other = (CriteriaIpSrcDst) obj;
        if (this.source != other.source && (this.source == null || !this.source.equals(other.source))) {
            return false;
        }
        if (this.destination != other.destination && (this.destination == null || !this.destination.equals(other.destination))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (this.source != null ? this.source.hashCode() : 0);
        hash = 67 * hash + (this.destination != null ? this.destination.hashCode() : 0);
        return hash;
    }
    @Override
    public String getInstanceString() {
        return this.source.getHostAddress() + " -[IP]> " + this.destination.getHostAddress();
    }
}
