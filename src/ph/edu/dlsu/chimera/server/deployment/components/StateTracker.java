/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.sourceforge.jpcap.net.Packet;
import net.sourceforge.jpcap.net.TCPPacket;
import net.sourceforge.jpcap.net.UDPPacket;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.core.PacketTools;
import ph.edu.dlsu.chimera.server.deployment.components.data.Connection;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.Component;
import ph.edu.dlsu.chimera.server.deployment.components.assembler.Assembler;
import ph.edu.dlsu.chimera.server.deployment.components.data.ConnectionData;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class StateTracker extends Component {

    protected final ConcurrentHashMap<Connection, ConnectionData> stateTable;
    protected final ConcurrentLinkedQueue<Packet> inQueue;
    protected final ConcurrentLinkedQueue<Packet> outQueue;
    protected final ConcurrentHashMap<Integer, Assembler> portProtocolMap;

    public StateTracker(Assembly assembly, 
            ConcurrentLinkedQueue<Packet> inQueue,
            ConcurrentLinkedQueue<Packet> outQueue,
            ConcurrentHashMap<Connection, ConnectionData> stateTable,
            ConcurrentHashMap<Integer, Assembler> portProtocolMap) {
        super(assembly);
        this.stateTable = stateTable;
        this.inQueue = inQueue;
        this.outQueue = outQueue;
        this.portProtocolMap = portProtocolMap;
    }

    @Override
    protected void componentRun() {
        while (super.running) {
            if(this.inQueue != null && this.stateTable != null) {
                while(!this.inQueue.isEmpty()) {
                    //poll packet
                    Packet pkt = this.inQueue.poll();
                    //get connection
                    Connection conn = PacketTools.getConnection(pkt);
                    if(conn != null) {
                        //create state
                        this.createStateIfNotExisting(conn, pkt);
                        //update state (implicit output)
                        if(this.stateTable.containsKey(conn))
                            this.updateStateDataTraffic(this.stateTable.get(conn), pkt);
                    }
                    if(this.outQueue != null) {
                        //send output signal
                        this.outQueue.add(pkt);
                    }
                }
            }
        }
    }

    private void createStateIfNotExisting(Connection conn, Packet pkt) {
        if(!this.stateTable.containsKey(conn)) {
            Assembler a = null;
            if(pkt instanceof TCPPacket) {
                TCPPacket tcp = (TCPPacket) pkt;
                a = this.portProtocolMap.get(tcp.getDestinationPort()).duplicate();
            }
            if(pkt instanceof UDPPacket){
                UDPPacket udp = (UDPPacket) pkt;
                a = this.portProtocolMap.get(udp.getDestinationPort()).duplicate();
            }
            this.stateTable.put(conn, new ConnectionData(conn, pkt.getTimeval(), a));
        }
    }

    protected abstract void updateStateDataTraffic(ConnectionData data, Packet recv);

    @Override
    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = super.getDiagnostics();
        if(this.stateTable != null)
            diag.add(new Diagnostic("states", "State Table Size", this.stateTable.size()));
        else
            diag.add(new Diagnostic("states", "State Table Size", "N/A"));
        if(this.inQueue != null)
            diag.add(new Diagnostic("inqueue", "Inbound Queued Packets", this.inQueue.size()));
        else
            diag.add(new Diagnostic("inqueue", "Inbound Queued Packets", "N/A"));
        if(this.outQueue != null)
            diag.add(new Diagnostic("outquque", "Outbound Queued Packets", this.inQueue.size()));
        else
            diag.add(new Diagnostic("outqueue", "Outbound Queued Packets", "N/A"));
        return diag;
    }
    
}
