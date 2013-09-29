/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.util;

import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.jpcap.net.Packet;
import net.sourceforge.jpcap.net.TCPPacket;
import net.sourceforge.jpcap.net.UDPPacket;
import ph.edu.dlsu.chimera.server.deployment.components.StateTracker;
import ph.edu.dlsu.chimera.server.deployment.components.data.Connection;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class PacketTools {

    public static Connection getConnection(Packet pkt) {
        try {
            if(pkt instanceof TCPPacket)
                return new Connection((TCPPacket) pkt);
            if(pkt instanceof UDPPacket)
                return new Connection((UDPPacket) pkt);
        } catch (UnknownHostException ex) {
            Logger.getLogger(StateTracker.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
