/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components.data.packet;

import ph.edu.dlsu.chimera.server.deployment.components.data.net.FTPTCPDataPacket;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PacketL3IPL4TCPL7FTPData extends PacketL3IPL4TCP<FTPTCPDataPacket> {

    public PacketL3IPL4TCPL7FTPData(FTPTCPDataPacket packet) {
        super(packet);
    }

}
