/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment;

import java.util.concurrent.ConcurrentLinkedQueue;
import net.sourceforge.jpcap.net.Packet;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.Deployment;
import ph.edu.dlsu.chimera.server.deployment.components.SnifferNetwork;
import ph.edu.dlsu.chimera.server.deployment.components.Sorter;
import ph.edu.dlsu.chimera.server.deployment.components.data.packet.PacketGeneric;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class Gathering extends Deployment {

    public Gathering(Assembly assembly, String ifInbound) {
        super(assembly);
        super.setName("Data Gathering");
        //inbound assembly
        ConcurrentLinkedQueue<Packet> outSnifferQueue = new ConcurrentLinkedQueue<Packet>();
        super.components.put("in.sniffer", new SnifferNetwork(assembly, outSnifferQueue, ifInbound));
        ConcurrentLinkedQueue<PacketGeneric> outSorterQueue = new ConcurrentLinkedQueue<PacketGeneric>();
        super.components.put("in.sorter", new Sorter(assembly, outSnifferQueue, outSorterQueue));
        //outbound assembly
    }

}
