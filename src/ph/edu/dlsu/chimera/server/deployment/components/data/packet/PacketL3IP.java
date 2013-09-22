/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components.data.packet;

import net.sourceforge.jpcap.net.IPPacket;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PacketL3IP<PType extends IPPacket> extends PacketL2Ethernet<PType> {

    public PacketL3IP(PType packet) {
        super(packet);
    }

}
