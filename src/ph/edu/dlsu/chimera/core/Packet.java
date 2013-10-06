/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.core;

import org.jnetpcap.Pcap;
import org.jnetpcap.packet.PcapPacket;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class Packet {

    public final Pcap pcap;
    public final PcapPacket packet;

    public Packet(Pcap pcap, PcapPacket packet) {
        this.pcap = pcap;
        this.packet = packet;
    }

}
