/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.util;

import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jnetpcap.packet.JHeader;
import org.jnetpcap.packet.JHeaderPool;
import org.jnetpcap.packet.JMemoryPacket;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.network.Ip6;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentStateTracker;
import ph.edu.dlsu.chimera.server.deployment.components.data.SocketPair;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class ToolsPacket {
    
    private static final JHeaderPool headerPool = new JHeaderPool();

    public static String getPacketProtocolName(PcapPacket pkt) {
        int id = pkt.getHeaderIdByIndex(pkt.getHeaderCount() - 1);
        return ToolsPacket.headerPool.getHeader(id).getName();
    }

    public static SocketPair getSocketPair(PcapPacket pkt) {
        try {
            if (pkt.hasHeader(new Tcp())) {
                Tcp tcp = pkt.getHeader(new Tcp());
                if (pkt.hasHeader(new Ip4())) {
                    Ip4 ip = pkt.getHeader(new Ip4());
                    return new SocketPair(ip, tcp);
                }
                if (pkt.hasHeader(new Ip6())) {
                    Ip6 ip = pkt.getHeader(new Ip6());
                    return new SocketPair(ip, tcp);
                }
            }
            if (pkt.hasHeader(new Udp())) {
                Udp udp = pkt.getHeader(new Udp());
                if (pkt.hasHeader(new Ip4())) {
                    Ip4 ip = pkt.getHeader(new Ip4());
                    return new SocketPair(ip, udp);
                }
                if (pkt.hasHeader(new Ip6())) {
                    Ip6 ip = pkt.getHeader(new Ip6());
                    return new SocketPair(ip, udp);
                }
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(ComponentStateTracker.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static Ethernet reverse(Ethernet arg) {
        byte[] src = arg.source();
        byte[] dst = arg.destination();
        arg.source(dst);
        arg.destination(src);
        return arg;
    }

    public static Ip4 reverse(Ip4 arg) {
        byte[] src = arg.source();
        byte[] dst = arg.destination();
        arg.source(dst);
        arg.destination(src);
        return arg;
    }

    public static Ip6 reverse(Ip6 arg) {
        byte[] src = arg.source();
        byte[] dst = arg.destination();
        arg.sourceToByteArray(dst);
        arg.destinationToByteArray(src);
        return arg;
    }

    public static Tcp reverse(Tcp arg) {
        int src = arg.source();
        int dst = arg.destination();
        arg.source(dst);
        arg.destination(src);
        return arg;
    }

    public static Udp reverse(Udp arg) {
        int src = arg.source();
        int dst = arg.destination();
        arg.source(dst);
        arg.destination(src);
        return arg;
    }

    public static JPacket createTcpAck(Tcp tcp, int ack) {
        byte[] data = new byte[0];
        JHeader h = tcp;
        while (h != null) {
            data = ToolsArray.concat(h.getHeader(), data);
            h = h.getParent();
        }
        //packet with tcp payload omitted
        JPacket jp = new JMemoryPacket(data);
        h = jp.getHeader(new Tcp());
        while (h != null) {
            if (h instanceof Tcp) {
                Tcp _tcp = (Tcp) h;
                _tcp = ToolsPacket.reverse(_tcp);
                _tcp.flags(0);
                _tcp.flags_ACK(true);
                _tcp.ack(ack);
                _tcp.checksum(_tcp.calculateChecksum());
            }
            if (h instanceof Ip6) {
                Ip6 _ip6 = (Ip6) h;
                _ip6 = ToolsPacket.reverse(_ip6);
            }
            if (h instanceof Ip4) {
                Ip4 _ip4 = (Ip4) h;
                _ip4 = ToolsPacket.reverse(_ip4);
                _ip4.checksum(_ip4.calculateChecksum());
            }
            if (h instanceof Ethernet) {
                Ethernet _eth = (Ethernet) h;
                _eth = ToolsPacket.reverse(_eth);
                _eth.checksum(_eth.calculateChecksum());
            }
            h = h.getParent();
        }
        jp.scan(Ethernet.ID);
        return jp;
    }
}
