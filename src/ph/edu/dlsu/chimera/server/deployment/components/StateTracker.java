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
import ph.edu.dlsu.chimera.util.PacketTools;
import ph.edu.dlsu.chimera.server.deployment.components.data.Connection;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.Component;
import ph.edu.dlsu.chimera.server.deployment.components.data.ConnectionData;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class StateTracker extends Component {

    public final boolean inbound;
    private final ConcurrentHashMap<Connection, ConnectionData> stateTable;
    private final ConcurrentLinkedQueue<PcapPacket> inQueue;
    private final ConcurrentLinkedQueue<PcapPacket> outQueue;

    public StateTracker(Assembly assembly,
            ConcurrentLinkedQueue<PcapPacket> inQueue,
            ConcurrentLinkedQueue<PcapPacket> outQueue,
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
                    PcapPacket pkt = this.inQueue.poll();
                    if (pkt.hasHeader(new Tcp())) {
                        //tcp packets
                        Connection conn = PacketTools.getConnection(pkt);
                        Tcp tcp = pkt.getHeader(new Tcp());
                        //create state
                        if (!this.stateTable.containsKey(conn)) {
                            if (tcp.flags_SYN()) {
                                this.stateTable.put(conn, new ConnectionData(conn, pkt.getCaptureHeader().timestampInMillis(), this.inbound));
                            }
                        }
                        if (this.stateTable.containsKey(conn)) {
                            //update state
                            this.updateStateDataTraffic(this.stateTable.get(conn));
                            //delete state
                            if(this.stateTable.get(conn).isDone()) {
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

    protected abstract void updateStateDataTraffic(ConnectionData data);

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
