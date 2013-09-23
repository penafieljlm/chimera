/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components.data.packet;

import ph.edu.dlsu.chimera.server.deployment.components.data.net.DNSPacket;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PacketL3IPL4UDPL7DNS extends PacketL3IPL4UDP<DNSPacket> {

    public PacketL3IPL4UDPL7DNS(DNSPacket packet) {
        super(packet);
    }

}
