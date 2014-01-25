/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.core;

import de.tbsol.iptablesjava.rules.IpRule;
import de.tbsol.iptablesjava.rules.match.ModTcp;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class TcpSocketPair implements Serializable {

    public final InetAddress source;
    public final int sourcePort;
    public final InetAddress destination;
    public final int destinationPort;

    public TcpSocketPair(InetAddress source, int sourcePort, InetAddress destination, int destinationPort) {
        this.source = source;
        this.destination = destination;
        this.sourcePort = sourcePort;
        this.destinationPort = destinationPort;
    }

    public TcpSocketPair(Ip4 ip4, Tcp tcp) throws UnknownHostException {
        this(
                InetAddress.getByAddress(ip4.source()),
                tcp.source(),
                InetAddress.getByAddress(ip4.destination()),
                tcp.destination());
    }

    @Override
    public synchronized boolean equals(Object obj) {
        if (obj instanceof TcpSocketPair) {
            TcpSocketPair socks = (TcpSocketPair) obj;
            return ((socks.source.equals(this.source)) && (socks.sourcePort == this.sourcePort) && (socks.destination.equals(this.destination)) && (socks.destinationPort == this.destinationPort))
                    || ((socks.source.equals(this.destination)) && (socks.sourcePort == this.destinationPort) && (socks.destination.equals(this.source)) && (socks.destinationPort == this.sourcePort));
        }
        return false;
    }

    @Override
    public synchronized int hashCode() {
        return ((this.source.hashCode() ^ this.sourcePort)
                ^ ((this.destination.hashCode() ^ this.destinationPort)));
    }

    public IpRule createRule() {
        try {
            IpRule rule = new IpRule();
            rule.setProtocol(IpRule.IpProto.IPPROTO_TCP);
            rule.setSource(this.source);
            rule.setSourceMask(InetAddress.getByName("255.255.255.255"));
            rule.setDestination(this.destination);
            rule.setDestinationMask(InetAddress.getByName("255.255.255.255"));
            ModTcp tcp = new ModTcp();
            tcp.setSourcePortStart(this.sourcePort);
            tcp.setSourcePortEnd(this.sourcePort);
            tcp.setDestinationPortStart(this.destinationPort);
            tcp.setDestinationPortEnd(this.destinationPort);
            rule.addModule(tcp);
            return rule;
        } catch (Exception ex) {
        }
        return null;
    }
}
