/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components.data.packet;

import net.sourceforge.jpcap.net.TCPPacket;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PacketL3IPL4TCP<PType extends TCPPacket> extends PacketL3IP<PType> {

    public PacketL3IPL4TCP(PType packet) {
        super(packet);
    }

}
