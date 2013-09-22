/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components.data.packet;

import net.sourceforge.jpcap.net.IGMPPacket;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PacketL3IPL4IGMP<PType extends IGMPPacket> extends PacketL3IP<PType> {

    public PacketL3IPL4IGMP(PType packet) {
        super(packet);
    }

}
