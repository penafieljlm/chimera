/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.jnetpcap.packet.JHeader;
import org.jnetpcap.packet.JHeaderPool;
import org.jnetpcap.packet.Payload;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;
import ph.edu.dlsu.chimera.server.core.SocketPair;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class ToolsPacket {

    private static final JHeaderPool headerPool = new JHeaderPool();

    public static String getPacketProtocolName(PcapPacket pkt) {
        int off = 1;
        int id = pkt.getHeaderIdByIndex(pkt.getHeaderCount() - off);
        JHeader jh = ToolsPacket.headerPool.getHeader(id);
        if (jh != null) {
            while (jh instanceof Payload) {
                off++;
                id = pkt.getHeaderIdByIndex(pkt.getHeaderCount() - off);
                jh = ToolsPacket.headerPool.getHeader(id);
            }
            return jh.getName();
        } else {
            return null;
        }
    }

    public static SocketPair getSocketPair(PcapPacket pkt) {
        if (pkt.hasHeader(new Ip4())) {
            try {
                Ip4 ip = pkt.getHeader(new Ip4());
                if (pkt.hasHeader(new Tcp())) {
                    Tcp tcp = pkt.getHeader(new Tcp());
                    return new SocketPair(ip, tcp);
                }
                if (pkt.hasHeader(new Udp())) {
                    Udp udp = pkt.getHeader(new Udp());
                    return new SocketPair(ip, udp);
                }
            } catch (UnknownHostException ex) {
            }
        }
        return null;
    }

    public static InetAddress getIpAddress(PcapPacket pkt, boolean source) {
        if (pkt.hasHeader(new Ip4())) {
            try {
                Ip4 ip = pkt.getHeader(new Ip4());
                if (source) {
                    return InetAddress.getByAddress(ip.source());
                } else {
                    return InetAddress.getByAddress(ip.destination());
                }
            } catch (UnknownHostException ex) {
            }
        }
        return null;
    }

    public static int getTcpPort(PcapPacket pkt, boolean source) {
        if (pkt.hasHeader(new Tcp())) {
            Tcp tcp = pkt.getHeader(new Tcp());
            if (source) {
                return tcp.source();
            } else {
                return tcp.destination();
            }
        }
        return -1;
    }

    public static int getUdpPort(PcapPacket pkt, boolean source) {
        if (pkt.hasHeader(new Udp())) {
            Udp udp = pkt.getHeader(new Udp());
            if (source) {
                return udp.source();
            } else {
                return udp.destination();
            }
        }
        return -1;
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
}