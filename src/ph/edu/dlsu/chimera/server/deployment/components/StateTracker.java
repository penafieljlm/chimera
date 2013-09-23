/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.jpcap.net.TCPPacket;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.server.deployment.components.data.TCPState;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.Component;
import ph.edu.dlsu.chimera.server.deployment.components.data.TCPStateData;
import ph.edu.dlsu.chimera.server.deployment.components.data.packet.PacketGeneric;
import ph.edu.dlsu.chimera.server.deployment.components.data.packet.PacketL3IPL4TCP;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class StateTracker extends Component {

    protected final ConcurrentHashMap<TCPState, TCPStateData> stateTable;
    protected final ConcurrentLinkedQueue<PacketGeneric> inQueue;
    protected final ConcurrentLinkedQueue<PacketGeneric> outQueue;

    public StateTracker(Assembly assembly, ConcurrentLinkedQueue<PacketGeneric> inQueue, ConcurrentLinkedQueue<PacketGeneric> outQueue, ConcurrentHashMap<TCPState, TCPStateData> stateTable) {
        super(assembly);
        this.stateTable = stateTable;
        this.inQueue = inQueue;
        this.outQueue = outQueue;
    }

    @Override
    protected void componentRun() {
        while (super.running) {
            if(this.inQueue != null) {
                while(!this.inQueue.isEmpty()) {
                    PacketGeneric pkt = this.inQueue.poll();
                    if(pkt instanceof PacketL3IPL4TCP) {
                        TCPPacket tcp = (TCPPacket) pkt.packet;
                        TCPState state = this.extractStateId(tcp);
                        if(state != null) {
                            if (this.stateTable.containsKey(state)) {
                                this.updateStateDataTraffic(this.stateTable.get(state));
                            } else {
                                this.stateTable.put(state, new TCPStateData(tcp.getTimeval()));
                                this.updateStateDataTraffic(this.stateTable.get(state));
                            }
                        }
                    }
                    if(this.outQueue != null)
                        this.outQueue.add(pkt);
                }
            }
        }
    }

    /**
     * Creates an appropriate TCPState identifier using the provided TCPPacket.
     * @param tcp - the packet to deduce the TCPState identifier from.
     * @return the appropriate TCPState identifier.
     */
    protected abstract TCPState extractStateId(TCPPacket tcp);

    /**
     * Updates the encounter count flowing towards the appropriate direction.
     * @param data - the TCPStateData to update.
     */
    protected abstract void updateStateDataTraffic(TCPStateData data);

    @Override
    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = super.getDiagnostics();
        if(this.inQueue != null)
            diag.add(new Diagnostic("inqueue", "Inbound Queued Packets", this.inQueue.size()));
        else
            diag.add(new Diagnostic("inqueue", "Inbound Queued Packets", "N/A"));
        if(this.outQueue != null)
            diag.add(new Diagnostic("outqueue", "Outbound Queued Packets", this.outQueue.size()));
        else
            diag.add(new Diagnostic("outqueue", "Outbound Queued Packets", "N/A"));
        return diag;
    }
    
}
