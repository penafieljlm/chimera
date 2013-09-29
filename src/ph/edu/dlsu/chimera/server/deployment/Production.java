/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment;

import java.util.concurrent.ConcurrentLinkedQueue;
import org.jnetpcap.packet.PcapPacket;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.Deployment;
import ph.edu.dlsu.chimera.server.deployment.components.SnifferNetwork;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class Production extends Deployment {

    public Production(Assembly assembly, String ifInbound) {
        super("Production");
        ConcurrentLinkedQueue<PcapPacket> inSnifferQueue = new ConcurrentLinkedQueue<PcapPacket>();
        super.components.put("in-sniffer", new SnifferNetwork(assembly, inSnifferQueue, ifInbound));
    }

}
