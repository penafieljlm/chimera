/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.jpcap.net.TCPPacket;
import ph.edu.dlsu.chimera.server.deployment.components.data.TCPState;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.deployment.components.data.TCPStateData;
import ph.edu.dlsu.chimera.server.deployment.components.data.packet.PacketGeneric;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class StateTrackerOutbound extends StateTracker {

    public StateTrackerOutbound(Assembly assembly, ConcurrentLinkedQueue<PacketGeneric> inQueue, ConcurrentLinkedQueue<PacketGeneric> outQueue, ConcurrentHashMap<TCPState, TCPStateData> stateTable) {
        super(assembly, inQueue, outQueue, stateTable);
    }

    @Override
    protected TCPState extractStateId(TCPPacket tcp) {
        try {
            InetAddress outside = InetAddress.getByAddress(tcp.getDestinationAddressBytes());
            int outsidePort = tcp.getDestinationPort();
            InetAddress inside = InetAddress.getByAddress(tcp.getSourceAddressBytes());
            int insidePort = tcp.getSourcePort();
            return new TCPState(inside, insidePort, outside, outsidePort);
        } catch (UnknownHostException ex) {
            Logger.getLogger(StateTrackerInbound.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    protected void updateStateDataTraffic(TCPStateData data) {
        data.outboundIncrement();
    }

}
