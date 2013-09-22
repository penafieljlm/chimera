/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components.data.net;

import net.sourceforge.jpcap.net.TCPPacket;
import net.sourceforge.jpcap.util.Timeval;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class HTTPPacket extends TCPPacket {

    public HTTPPacket(int lLen, byte[] bytes) {
        super(lLen, bytes);
    }

    public HTTPPacket(int lLen, byte[] bytes, Timeval tv) {
        super(lLen, bytes, tv);
    }

}
