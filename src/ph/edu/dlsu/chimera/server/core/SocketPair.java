/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.core;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class SocketPair {

    public final InetAddress source;
    public final int sourcePort;
    public final InetAddress destination;
    public final int destinationPort;

    public SocketPair(InetAddress source, int sourcePort, InetAddress destination, int destinationPort) {
        this.source = source;
        this.destination = destination;
        this.sourcePort = sourcePort;
        this.destinationPort = destinationPort;
    }

    public SocketPair(Ip4 ip4, Tcp tcp) throws UnknownHostException {
        this(
                InetAddress.getByAddress(ip4.source()),
                tcp.source(),
                InetAddress.getByAddress(ip4.destination()),
                tcp.destination());
    }

    public SocketPair(Ip4 ip4, Udp udp) throws UnknownHostException {
        this(
                InetAddress.getByAddress(ip4.source()),
                udp.source(),
                InetAddress.getByAddress(ip4.destination()),
                udp.destination());
    }

    @Override
    public synchronized boolean equals(Object obj) {
        if (obj instanceof SocketPair) {
            SocketPair socks = (SocketPair) obj;
            return ((socks.source.equals(this.source)) && (socks.sourcePort == this.sourcePort) && (socks.destination.equals(this.destination)) && (socks.destinationPort == this.destinationPort))
                    || ((socks.source.equals(this.destination)) && (socks.sourcePort == this.destinationPort) && (socks.destination.equals(this.source)) && (socks.destinationPort == this.sourcePort));
        }
        return false;
    }

    @Override
    public synchronized int hashCode() {
        return ((source.hashCode() ^ sourcePort)
                ^ ((destination.hashCode() ^ destinationPort)));
    }
}
