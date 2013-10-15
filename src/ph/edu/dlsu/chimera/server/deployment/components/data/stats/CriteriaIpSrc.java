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
public class CriteriaIpSrc extends Criteria {

    public final InetAddress source;

    public CriteriaIpSrc() {
        this(null);
    }

    public CriteriaIpSrc(InetAddress destination) {
        super("ipsrc", "Source IP");
        this.source = destination;
    }

    @Override
    public Criteria createInstance(PduAtomic pkt) {
        SocketPair socks = ToolsPacket.getSocketPair(pkt.packet);
        if (socks != null) {
            return new CriteriaIpSrc(socks.source);
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
        final CriteriaIpSrc other = (CriteriaIpSrc) obj;
        if (this.source != other.source && (this.source == null || !this.source.equals(other.source))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.source != null ? this.source.hashCode() : 0);
        return hash;
    }
    @Override
    public String getInstanceString() {
        return this.source.getHostAddress() + " -[IP]> any";
    }
}
