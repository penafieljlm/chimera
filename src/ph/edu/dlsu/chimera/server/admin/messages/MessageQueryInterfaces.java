/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.admin.messages;

import java.util.ArrayList;
import java.util.List;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
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
        List<PcapIf> ifaces = new ArrayList<PcapIf>();
        StringBuilder errbuff = new StringBuilder();
        int result = Pcap.findAllDevs(ifaces, errbuff);
        if (result == 0) {
            ArrayList<NICData> interfaces = new ArrayList<NICData>();
            for (PcapIf device : ifaces) {
                interfaces.add(new NICData(device));
            }
            return new MessageInterfaces(interfaces);
        }
        throw new Exception("Unable to find devices!");
    }
}
