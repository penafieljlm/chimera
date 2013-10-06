/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.tcpip.Tcp;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.core.Packet;
import ph.edu.dlsu.chimera.util.PacketTools;
import ph.edu.dlsu.chimera.server.deployment.components.data.Connection;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.ComponentActive;
import ph.edu.dlsu.chimera.server.deployment.components.data.ConnectionData;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class StateTracker extends ComponentActive {

    public final boolean inbound;
    public final ConcurrentHashMap<Connection, ConnectionData> stateTable;
    public final ConcurrentLinkedQueue<Packet> inQueue;
    public final ConcurrentLinkedQueue<Packet> outQueue;

    public StateTracker(Assembly assembly,
            ConcurrentLinkedQueue<Packet> inQueue,
            ConcurrentLinkedQueue<Packet> outQueue,
            ConcurrentHashMap<Connection, ConnectionData> stateTable,
            boolean inbound) {
        super(assembly);
        this.stateTable = stateTable;
        this.inQueue = inQueue;
        this.outQueue = outQueue;
        this.inbound = inbound;
    }

    @Override
    protected void componentRun() {
        while (super.running) {
            if (this.inQueue != null && this.stateTable != null) {
                while (!this.inQueue.isEmpty()) {
                    //poll packet
                    Packet pkt = this.inQueue.poll();
                    if (pkt.packet.hasHeader(new Tcp())) {
                        //tcp packets
                        Connection conn = PacketTools.getConnection(pkt.packet);
                        Tcp tcp = pkt.packet.getHeader(new Tcp());
                        //create state
                        if (!this.stateTable.containsKey(conn)) {
                            if (tcp.flags_SYN()) {
                                this.stateTable.put(conn, new ConnectionData(pkt.packet.getCaptureHeader().timestampInNanos(), this.inbound));
                            }
                        }
                        if (this.stateTable.containsKey(conn)) {
                            //update state
                            this.stateTable.get(conn).update(pkt.packet, this.inbound);
                            //delete state
                            if (this.stateTable.get(conn).isDone()) {
                                this.stateTable.remove(conn);
                            }
                            //forward
                            if (this.outQueue != null) {
                                this.outQueue.add(pkt);
                            }
                        }
                    } else {
                        //non tcp packets
                        if (this.outQueue != null) {
                            this.outQueue.add(pkt);
                        }
                    }
                }
            }
        }
    }

    @Override
    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = super.getDiagnostics();
        if (this.stateTable != null) {
            diag.add(new Diagnostic("states", "State Table Size", this.stateTable.size()));
        } else {
            diag.add(new Diagnostic("states", "State Table Size", "N/A"));
        }
        if (this.inQueue != null) {
            diag.add(new Diagnostic("inqueue", "Inbound Queued Packets", this.inQueue.size()));
        } else {
            diag.add(new Diagnostic("inqueue", "Inbound Queued Packets", "N/A"));
        }
        if (this.outQueue != null) {
            diag.add(new Diagnostic("outquque", "Outbound Queued Packets", this.inQueue.size()));
        } else {
            diag.add(new Diagnostic("outqueue", "Outbound Queued Packets", "N/A"));
        }
        return diag;
    }
}
