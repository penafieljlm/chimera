/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.sourceforge.jpcap.net.Packet;
import ph.edu.dlsu.chimera.server.deployment.components.data.Connection;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.deployment.components.data.ConnectionData;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class StateTrackerInbound extends StateTracker {

    public StateTrackerInbound(Assembly assembly, ConcurrentLinkedQueue<Packet> inQueue, ConcurrentLinkedQueue<Packet> outQueue, ConcurrentHashMap<Connection, ConnectionData> stateTable) {
        super(assembly, inQueue, outQueue, stateTable);
    }
    
    @Override
    protected void updateStateDataTraffic(ConnectionData data, Packet recv) {
        data.update(recv, true);
    }

}
