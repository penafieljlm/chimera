/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data.stats.atomic;

import java.net.InetAddress;
import ph.edu.dlsu.chimera.server.deployment.components.data.SocketPair;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;
import ph.edu.dlsu.chimera.util.ToolsPacket;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class CriteriaAtomicIpSrcDst extends CriteriaAtomic {

    public final InetAddress source;
    public final InetAddress destination;

    public CriteriaAtomicIpSrcDst() {
        this(null, null);
    }

    public CriteriaAtomicIpSrcDst(InetAddress source,
            InetAddress destination) {
        super("ipsrcdst", "Source and Destination IP");
        this.source = source;
        this.destination = destination;
    }

    @Override
    public CriteriaAtomic createInstance(PduAtomic pkt) {
        SocketPair socks = ToolsPacket.getConnection(pkt.packet);
        if (socks != null) {
            return new CriteriaAtomicIpSrcDst(socks.source, socks.destination);
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
        final CriteriaAtomicIpSrcDst other = (CriteriaAtomicIpSrcDst) obj;
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
