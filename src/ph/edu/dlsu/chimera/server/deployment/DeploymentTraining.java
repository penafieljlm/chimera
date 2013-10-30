/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.sourceforge.jpcap.net.Packet;
import ph.edu.dlsu.chimera.server.deployment.components.data.TCPState;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.Deployment;
import ph.edu.dlsu.chimera.server.deployment.components.SnifferDump;
import ph.edu.dlsu.chimera.server.deployment.components.Sorter;
import ph.edu.dlsu.chimera.server.deployment.components.StateTrackerInbound;
import ph.edu.dlsu.chimera.server.deployment.components.data.TCPStateData;
import ph.edu.dlsu.chimera.server.deployment.components.data.packet.PacketGeneric;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class DeploymentTraining extends Deployment {

    public Training(Assembly assembly, String inFile) {
        super("Training");
        ConcurrentHashMap<TCPState, TCPStateData> stateTable = new ConcurrentHashMap<TCPState, TCPStateData>();
        //inbound assembly
        ConcurrentLinkedQueue<Packet> outSnifferQueue = new ConcurrentLinkedQueue<Packet>();
        super.components.put("in.sniffer", new SnifferDump(assembly, outSnifferQueue, inFile));
        ConcurrentLinkedQueue<PacketGeneric> outSorterQueue = new ConcurrentLinkedQueue<PacketGeneric>();
        super.components.put("in.sorter", new Sorter(assembly, outSnifferQueue, outSorterQueue));
        ConcurrentLinkedQueue<PacketGeneric> outStateTrackerQueue = new ConcurrentLinkedQueue<PacketGeneric>();
        super.components.put("in.statetracker", new StateTrackerInbound(assembly, outSorterQueue, outStateTrackerQueue, stateTable));
        //outbound assembly
    }

}
