/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.jpcap.net.Packet;
import net.sourceforge.jpcap.net.TCPPacket;
import net.sourceforge.jpcap.net.UDPPacket;
import ph.edu.dlsu.chimera.core.Diagnostic;
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

    protected final ConcurrentHashMap<Connection, ConnectionData> stateTable;
    protected final ConcurrentLinkedQueue<Packet> inQueue;
    protected final ConcurrentLinkedQueue<Packet> outQueue;

    public StateTracker(Assembly assembly, ConcurrentLinkedQueue<Packet> inQueue, ConcurrentLinkedQueue<Packet> outQueue, ConcurrentHashMap<Connection, ConnectionData> stateTable) {
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
                    //poll packet
                    Packet pkt = this.inQueue.poll();
                    //get connection
                    Connection conn = this.getConnection(pkt);
                    if(conn != null) {
                        //create state
                        this.createStateIfNotExisting(conn, pkt);
                        //update state (implicit output)
                        if(this.stateTable.containsKey(conn))
                            this.updateStateDataTraffic(this.stateTable.get(conn), pkt);
                    }
                    //send output signal
                    this.outQueue.add(pkt);
                }
            }
        }
    }

    private void createStateIfNotExisting(Connection conn, Packet pkt) {
        if(!this.stateTable.containsKey(conn)) {
            if(pkt instanceof TCPPacket)
                this.stateTable.put(conn, new ConnectionDataTCP(conn, pkt.getTimeval()));
            if(pkt instanceof UDPPacket)
                this.stateTable.put(conn, new ConnectionDataUDP(conn, pkt.getTimeval()));
        }
    }

    private Connection getConnection(Packet pkt) {
        try {
            if(pkt instanceof TCPPacket)
                return new Connection((TCPPacket) pkt);
            if(pkt instanceof UDPPacket)
                return new Connection((UDPPacket) pkt);
        } catch (UnknownHostException ex) {
            Logger.getLogger(StateTracker.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    protected abstract void updateStateDataTraffic(ConnectionData data, Packet recv);

    @Override
    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = super.getDiagnostics();
        if(this.inQueue != null)
            diag.add(new Diagnostic("inqueue", "Inbound Queued Packets", this.inQueue.size()));
        else
            diag.add(new Diagnostic("inqueue", "Inbound Queued Packets", "N/A"));
        return diag;
    }
    
}
