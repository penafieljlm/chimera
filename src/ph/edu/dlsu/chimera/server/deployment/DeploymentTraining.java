/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment;

import ph.edu.dlsu.chimera.server.Assembly;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class DeploymentTraining extends Deployment {

    public DeploymentTraining(Assembly assembly, String inFile) {
        super("Training");
//        ConcurrentHashMap<Connection, TCPStateData> stateTable = new ConcurrentHashMap<Connection, TCPStateData>();
//        //inbound assembly
//        ConcurrentLinkedQueue<Packet> outSnifferQueue = new ConcurrentLinkedQueue<Packet>();
//        super.components.put("in.sniffer", new SnifferDump(assembly, outSnifferQueue, inFile));
//        ConcurrentLinkedQueue<PacketGeneric> outSorterQueue = new ConcurrentLinkedQueue<PacketGeneric>();
//        super.components.put("in.sorter", new Sorter(assembly, outSnifferQueue, outSorterQueue));
//        ConcurrentLinkedQueue<PacketGeneric> outStateTrackerQueue = new ConcurrentLinkedQueue<PacketGeneric>();
//        super.components.put("in.statetracker", new StateTrackerInbound(assembly, outSorterQueue, outStateTrackerQueue, stateTable));
//        //outbound assembly
    }

}
