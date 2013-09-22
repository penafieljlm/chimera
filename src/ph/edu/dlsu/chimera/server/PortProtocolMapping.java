/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server;

import net.sourceforge.jpcap.net.IPPacket;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PortProtocolMapping<TL4 extends IPPacket, TL7 extends TL4> {

    public final int port;
    public final String name;
    public final Class<TL4> layer4;
    public final Class<TL7> layer7;

    public PortProtocolMapping(int port, String name, Class<TL4> layer4, Class<TL7> layer7) {
        this.port = port;
        this.name = name;
        this.layer4 = layer4;
        this.layer7 = layer7;
    }

}
