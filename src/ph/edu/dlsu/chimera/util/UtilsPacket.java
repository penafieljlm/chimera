package ph.edu.dlsu.chimera.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;
import ph.edu.dlsu.chimera.core.TcpSocketPair;

/**
 * This utility class provide functions dealing with packets.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class UtilsPacket {

    /**
     * Gets the TcpSocketPair from the specified packet.
     *
     * @param pkt The packet
     * @return The TcpSocketPair from the specified packet
     */
    public static TcpSocketPair getSocketPair(PcapPacket pkt) {
        try {
            if (pkt.hasHeader(new Ip4())) {
                try {
                    Ip4 ip = pkt.getHeader(new Ip4());
                    if (pkt.hasHeader(new Tcp())) {
                        Tcp tcp = pkt.getHeader(new Tcp());
                        return new TcpSocketPair(ip, tcp);
                    }
                } catch (UnknownHostException ex) {
                }
            }
        } catch (Exception ex) {
        }
        return null;
    }

    /**
     * Gets the IP Address from the specified packet.
     *
     * @param pkt The packet
     * @param source True if the address to be acquired is the source IP, else,
     * it shall be the destination address
     * @return
     */
    public static InetAddress getIpAddress(PcapPacket pkt, boolean source) {
        try {
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
        } catch (Exception ex) {
        }
        return null;
    }

    /**
     * Reverses the source and destination MAC addresses of the given Ethernet
     * header,
     *
     * @param arg The Ethernet header
     * @return The reversed header
     */
    public static Ethernet reverse(Ethernet arg) {
        byte[] src = arg.source();
        byte[] dst = arg.destination();
        arg.source(dst);
        arg.destination(src);
        return arg;
    }

    /**
     * Reverses the source and destination IP addresses of the given IPv4
     * header,
     *
     * @param arg The IPv4 header
     * @return The reversed header
     */
    public static Ip4 reverse(Ip4 arg) {
        byte[] src = arg.source();
        byte[] dst = arg.destination();
        arg.source(dst);
        arg.destination(src);
        return arg;
    }

    /**
     * Reverses the source and destination ports of the given TCP header,
     *
     * @param arg The TCP header
     * @return The reversed header
     */
    public static Tcp reverse(Tcp arg) {
        int src = arg.source();
        int dst = arg.destination();
        arg.source(dst);
        arg.destination(src);
        return arg;
    }

    /**
     * Reverses the source and destination ports of the given UDP header,
     *
     * @param arg The UDP header
     * @return The reversed header
     */
    public static Udp reverse(Udp arg) {
        int src = arg.source();
        int dst = arg.destination();
        arg.source(dst);
        arg.destination(src);
        return arg;
    }
}
