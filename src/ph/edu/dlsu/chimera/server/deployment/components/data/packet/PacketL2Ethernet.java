/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components.data.packet;

import net.sourceforge.jpcap.net.EthernetPacket;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PacketL2Ethernet<PType extends EthernetPacket> extends PacketGeneric<PType> {

    public PacketL2Ethernet(PType packet) {
        super(packet);
    }

}
