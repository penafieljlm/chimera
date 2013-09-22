/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components.data.packet;

import ph.edu.dlsu.chimera.server.deployment.components.data.net.HTTPPacket;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PacketL3IPL4TCPL7HTTP extends PacketL3IPL4TCP<HTTPPacket> {

    public PacketL3IPL4TCPL7HTTP(HTTPPacket packet) {
        super(packet);
    }

}
