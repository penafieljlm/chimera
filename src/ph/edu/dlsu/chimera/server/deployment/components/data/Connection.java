/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.network.Ip6;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class Connection {

    public final InetAddress source;
    public final int sourcePort;
    public final InetAddress destination;
    public final int destinationPort;

    public Connection(InetAddress source, int sourcePort, InetAddress destination, int destinationPort) {
        this.source = source;
        this.destination = destination;
        this.sourcePort = sourcePort;
        this.destinationPort = destinationPort;
    }

    public Connection(Ip4 ip4, Tcp tcp) throws UnknownHostException {
        this(
                InetAddress.getByAddress(ip4.source()),
                tcp.source(),
                InetAddress.getByAddress(ip4.destination()),
                tcp.destination());
    }

    public Connection(Ip4 ip4, Udp udp) throws UnknownHostException {
        this(
                InetAddress.getByAddress(ip4.source()),
                udp.source(),
                InetAddress.getByAddress(ip4.destination()),
                udp.destination());
    }

    public Connection(Ip6 ip6, Tcp tcp) throws UnknownHostException {
        this(
                InetAddress.getByAddress(ip6.source()),
                tcp.source(),
                InetAddress.getByAddress(ip6.destination()),
                tcp.destination());
    }

    public Connection(Ip6 ip6, Udp udp) throws UnknownHostException {
        this(
                InetAddress.getByAddress(ip6.source()),
                udp.source(),
                InetAddress.getByAddress(ip6.destination()),
                udp.destination());
    }

    @Override
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof Connection)) {
            return false;
        }

        Connection con = (Connection) obj;

        return ((con.source.equals(this.source)) && (con.sourcePort == this.sourcePort) && (con.destination.equals(this.destination)) && (con.destinationPort == this.destinationPort))
                || ((con.source.equals(this.destination)) && (con.sourcePort == this.destinationPort) && (con.destination.equals(this.source)) && (con.destinationPort == this.sourcePort));

    }

    @Override
    public synchronized int hashCode() {
        return ((source.hashCode() ^ sourcePort)
                ^ ((destination.hashCode() ^ destinationPort)));
    }
}
