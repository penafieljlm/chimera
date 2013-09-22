/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.admin.messages;

import java.math.BigInteger;
import java.net.InetAddress;
import java.util.ArrayList;
import net.sourceforge.jpcap.capture.PacketCapture;
import ph.edu.dlsu.chimera.client.admin.messages.ClientShellMessage;
import ph.edu.dlsu.chimera.client.admin.messages.MessageInterfaces;
import ph.edu.dlsu.chimera.core.NICData;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.admin.Session;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class MessageQueryInterfaces implements ServerMessage {

    public ClientShellMessage handleMessage(Session session, Assembly assembly) throws Exception {
        PacketCapture pcap = new PacketCapture();
        String[] devices = PacketCapture.lookupDevices();
        ArrayList<NICData> interfaces = new ArrayList<NICData>();
        for (String device : devices) {
            String network = "N/A";
            String netmask = "N/A";
            try {
                network = InetAddress.getByAddress(BigInteger.valueOf(pcap.getNetwork(device)).toByteArray()).getHostAddress();
                netmask = InetAddress.getByAddress(BigInteger.valueOf(pcap.getNetmask(device)).toByteArray()).getHostAddress();
            } catch (Exception ex) {
                
            }
            interfaces.add(new NICData(device, network, netmask));
        }
        return new MessageInterfaces(interfaces);
    }
}
