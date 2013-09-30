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
import org.jnetpcap.protocol.tcpip.Udp;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.util.PacketTools;
import ph.edu.dlsu.chimera.server.deployment.components.data.Connection;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.Component;
import ph.edu.dlsu.chimera.server.deployment.components.handler.ProtocolHandler;
import ph.edu.dlsu.chimera.server.deployment.components.data.ConnectionData;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class StateTracker extends Component {

    protected final ConcurrentHashMap<Connection, ConnectionData> stateTable;
    protected final ConcurrentLinkedQueue<PcapPacket> inQueue;
    protected final ConcurrentLinkedQueue<PcapPacket> outQueue;
    protected final ConcurrentHashMap<Integer, ProtocolHandler> portProtocolMap;

    public StateTracker(Assembly assembly,
            ConcurrentLinkedQueue<PcapPacket> inQueue,
            ConcurrentLinkedQueue<PcapPacket> outQueue,
            ConcurrentHashMap<Connection, ConnectionData> stateTable,
            ConcurrentHashMap<Integer, ProtocolHandler> portProtocolMap) {
        super(assembly);
        this.stateTable = stateTable;
        this.inQueue = inQueue;
        this.outQueue = outQueue;
        this.portProtocolMap = portProtocolMap;
    }

    @Override
    protected void componentRun() {
        while (super.running) {
            if (this.inQueue != null && this.stateTable != null) {
                while (!this.inQueue.isEmpty()) {
                    //poll packet
                    PcapPacket pkt = this.inQueue.poll();
                    //get connection
                    Connection conn = PacketTools.getConnection(pkt);
                    if (conn != null) {
                        //create state
                        if (!this.stateTable.containsKey(conn)) {
                            ProtocolHandler a = null;
                            if (pkt.hasHeader(new Tcp())) {
                                Tcp tcp = pkt.getHeader(new Tcp());
                                a = this.portProtocolMap.get(tcp.destination()).copyHandlerType();
                            }
                            if (pkt.hasHeader(new Udp())) {
                                Udp udp = pkt.getHeader(new Udp());
                                a = this.portProtocolMap.get(udp.destination()).copyHandlerType();
                            }
                            this.stateTable.put(conn, new ConnectionData(conn, pkt.getCaptureHeader().timestampInMillis(), a));
                        }
                        //update state
                        if (this.stateTable.containsKey(conn)) {
                            this.updateStateDataTraffic(this.stateTable.get(conn));
                        }
                        
                    }
                    if (this.outQueue != null) {
                        //send output signal
                        this.outQueue.add(pkt);
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
