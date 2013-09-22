/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components.data.packet;

import ph.edu.dlsu.chimera.server.deployment.components.data.net.FTPControlPacket;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PacketL3IPL4TCPL7FTPControl extends PacketL3IPL4TCP<FTPControlPacket> {

    public PacketL3IPL4TCPL7FTPControl(FTPControlPacket packet) {
        super(packet);
    }

}
