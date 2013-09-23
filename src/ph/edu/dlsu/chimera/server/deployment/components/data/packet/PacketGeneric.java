/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components.data.packet;

import net.sourceforge.jpcap.net.Packet;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PacketGeneric<PType extends Packet> {

    public final PType packet;

    public PacketGeneric(PType packet) {
        this.packet = packet;
    }

}
