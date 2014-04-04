package ph.edu.dlsu.chimera.core;

import de.tbsol.iptablesjava.rules.IpRule;
import de.tbsol.iptablesjava.rules.match.ModTcp;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;

/**
 * An instance of this class constitutes an object which identifies a pair of
 * TCP Sockets (a pair of IP Address and port number).
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class TcpSocketPair implements Serializable {

    /**
     * Source IP Address
     */
    public final InetAddress source;
    /**
     * Source Port
     */
    public final int sourcePort;
    /**
     * Destination IP Address
     */
    public final InetAddress destination;
    /**
     * Destination Port
     */
    public final int destinationPort;

    /**
     * Constructs a new TcpSocketPair object.
     *
     * @param source Source IP Address
     * @param sourcePort Source Port
     * @param destination Destination IP Address
     * @param destinationPort Destination Port
     */
    public TcpSocketPair(InetAddress source, int sourcePort, InetAddress destination, int destinationPort) {
        this.source = source;
        this.destination = destination;
        this.sourcePort = sourcePort;
        this.destinationPort = destinationPort;
    }

    /**
     * Constructs a new TcpSocketPaid object from an IPv4 header and a TCP
     * header.
     *
     * @param ip4 The IPv4 header
     * @param tcp The TCP header
     * @throws UnknownHostException
     */
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

    /**
     *
     * @return An iptables rule which matches packets having the same socket
     * pair of this TcpSocketPair object
     */
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
