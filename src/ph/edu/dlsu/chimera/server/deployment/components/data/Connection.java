/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data;

import java.net.InetAddress;
import java.net.UnknownHostException;
import net.sourceforge.jpcap.capture.PacketCapture;
import net.sourceforge.jpcap.net.TCPPacket;
import net.sourceforge.jpcap.net.UDPPacket;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class Connection {

    public final InetAddress source;
    public final int sourcePort;
    public final InetAddress destination;
    public final int destinationPort;

    public Connection(InetAddress sourceIP, int sourcePort, InetAddress destinationIP, int destinationPort) {
        this.source = sourceIP;
        this.destination = destinationIP;
        this.sourcePort = sourcePort;
        this.destinationPort = destinationPort;
    }

    public Connection(TCPPacket packet) throws UnknownHostException {
        this(
                InetAddress.getByAddress(packet.getSourceAddressBytes()),
                packet.getSourcePort(),
                InetAddress.getByAddress(packet.getDestinationAddressBytes()),
                packet.getDestinationPort()
            );
    }

    public Connection(UDPPacket packet) throws UnknownHostException {
        this(
                InetAddress.getByAddress(packet.getSourceAddressBytes()),
                packet.getSourcePort(),
                InetAddress.getByAddress(packet.getDestinationAddressBytes()),
                packet.getDestinationPort()
            );
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Connection))
            return false;

        Connection con = (Connection) obj;

        return ((con.source.equals(this.source)) && (con.sourcePort == this.sourcePort) && (con.destination.equals(this.destination)) && (con.destinationPort == this.destinationPort)) ||
                ((con.source.equals(this.destination)) && (con.sourcePort == this.destinationPort) && (con.destination.equals(this.source)) && (con.destinationPort == this.sourcePort));

    }

    @Override
    public int hashCode() {
        return ((source.hashCode() ^ sourcePort) ^
                ((destination.hashCode() ^ destinationPort)));
    }
    
}
