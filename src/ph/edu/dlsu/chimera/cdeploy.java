/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera;

import org.jnetpcap.packet.JHeader;
import org.jnetpcap.packet.structure.JField;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class cdeploy {

    public static void main(String[] args) throws Exception {
        JHeader p = new Udp();
        for (JField f : p.getFields()) {
            System.out.println(f.getName());
        }
    }
}
