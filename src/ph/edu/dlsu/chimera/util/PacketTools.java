/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.network.Ip6;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;
import ph.edu.dlsu.chimera.server.deployment.components.StateTracker;
import ph.edu.dlsu.chimera.server.deployment.components.data.Connection;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class PacketTools {

    public static Connection getConnection(PcapPacket pkt) {
        try {
            if(pkt.hasHeader(new Ip4())) {
                Ip4 ip = pkt.getHeader(new Ip4());
                if(pkt.hasHeader(new Tcp()))
                    return new Connection(ip, pkt.getHeader(new Tcp()));
                if(pkt.hasHeader(new Udp()))
                    return new Connection(ip, pkt.getHeader(new Udp()));
            }
            if(pkt.hasHeader(new Ip6())) {
                Ip6 ip = pkt.getHeader(new Ip6());
                if(pkt.hasHeader(new Tcp()))
                    return new Connection(ip, pkt.getHeader(new Tcp()));
                if(pkt.hasHeader(new Udp()))
                    return new Connection(ip, pkt.getHeader(new Udp()));
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(StateTracker.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
