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
public class CriteriaAtomicIpDst extends CriteriaAtomic {

    public final InetAddress destination;

    public CriteriaAtomicIpDst() {
        this(null);
    }

    public CriteriaAtomicIpDst(InetAddress destination) {
        super("ipdst", "Destination IP");
        this.destination = destination;
    }

    @Override
    public CriteriaAtomic createInstance(PduAtomic pkt) {
        SocketPair socks = ToolsPacket.getConnection(pkt.packet);
        if (socks != null) {
            return new CriteriaAtomicIpDst(socks.destination);
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
        final CriteriaAtomicIpDst other = (CriteriaAtomicIpDst) obj;
        if (this.destination != other.destination && (this.destination == null || !this.destination.equals(other.destination))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.destination != null ? this.destination.hashCode() : 0);
        return hash;
    }
    @Override
    public String getInstanceString() {
        return "any -[IP]> " + this.destination.getHostAddress();
    }
}
