/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.admin.messages;

import java.util.ArrayList;
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
        ArrayList<NICData> interfaces = new ArrayList<>();
        for (PcapIf device : assembly.getInterfaces()) {
            interfaces.add(new NICData(device));
        }
        return new MessageInterfaces(interfaces);
    }
}
