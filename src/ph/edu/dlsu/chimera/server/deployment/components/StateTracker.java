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
import ph.edu.dlsu.chimera.server.deployment.components.data.ConnectionData;
import ph.edu.dlsu.chimera.server.deployment.components.data.ConnectionDataTCP;
import ph.edu.dlsu.chimera.server.deployment.components.data.ConnectionDataUDP;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class StateTracker extends Component {

    public final boolean inbound;
    protected final ConcurrentHashMap<Connection, ConnectionData> stateTable;
    protected final ConcurrentLinkedQueue<PcapPacket> inQueue;
    protected final ConcurrentLinkedQueue<PcapPacket> outQueue;
    protected final ConcurrentHashMap<Integer, ConnectionDataUDP> udpPortConnectionDataType;

    public StateTracker(Assembly assembly,
            boolean inbound,
            ConcurrentLinkedQueue<PcapPacket> inQueue,
            ConcurrentLinkedQueue<PcapPacket> outQueue,
            ConcurrentHashMap<Connection, ConnectionData> stateTable,
            ConcurrentHashMap<Integer, ConnectionDataUDP> udpPortConnectionDataType) {
        super(assembly);
        this.inbound = inbound;
        this.stateTable = stateTable;
        this.inQueue = inQueue;
        this.outQueue = outQueue;
        this.udpPortConnectionDataType = udpPortConnectionDataType;
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
                        //create state / query state
                        ConnectionData connDat = null;
                        if(!this.stateTable.containsKey(conn)) {
                            //create state
                            connDat = this.createConnectionData(pkt, conn);
                        } else {
                            //query state
                            connDat = this.stateTable.get(conn);
                        }
                        if(connDat != null) {
                            //has appropriate connection data type
                            //update state
                            connDat.append(pkt);
                            //forward packets to assembler / injector
                            PcapPacket front = this.pollConnectionData(connDat);
                            while(front != null) {
                                if(this.outQueue != null) {
                                    this.outQueue.add(front);
                                }
                                front = this.pollConnectionData(connDat);
                            }
                            //delete state
                            if(connDat.isDone()) {
                                this.stateTable.remove(conn);
                            }
                        } else {
                            //no appropriate connection data type
                            if(this.outQueue != null) {
                                this.outQueue.add(pkt);
                            }
                        }
                    }
                }
            }
        }
    }

    private ConnectionData createConnectionData(PcapPacket pkt, Connection connection) {
        if(pkt.hasHeader(new Tcp())) {
            return new ConnectionDataTCP(connection, pkt.getCaptureHeader().timestampInNanos(), this.inbound);
        }
        if(pkt.hasHeader(new Udp())) {
            Udp udp = pkt.getHeader(new Udp());
            return this.udpPortConnectionDataType.get(udp.destination()).createInstance(connection, pkt.getCaptureHeader().timestampInNanos(), this.inbound);
        }
        return null;
    }

    private PcapPacket pollConnectionData(ConnectionData data) {
        if(this.inbound ^ data.inbound) {
            return data.pollFromDestination();
        } else {
            return data.pollFromSource();
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
            diag.add(new Diagnostic("outquque", "Outbound Queued Packets", this.outQueue.size()));
        } else {
            diag.add(new Diagnostic("outqueue", "Outbound Queued Packets", "N/A"));
        }
        return diag;
    }
}
