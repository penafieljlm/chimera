/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.jnetpcap.protocol.tcpip.Tcp;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;
import ph.edu.dlsu.chimera.util.ToolsPacket;
import ph.edu.dlsu.chimera.server.core.SocketPair;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.core.Connection;

/**
 * Tracks states and ensures TCP delivery.
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class ComponentStateTracker extends ComponentActive {

    public final ConcurrentHashMap<SocketPair, Connection> stateTable;
    public final ConcurrentLinkedQueue<PduAtomic> inQueue;
    public final ConcurrentLinkedQueue<PduAtomic> outQueue;
    private long processed;

    public ComponentStateTracker(Assembly assembly,
            ConcurrentLinkedQueue<PduAtomic> inQueue,
            ConcurrentLinkedQueue<PduAtomic> outQueue,
            ConcurrentHashMap<SocketPair, Connection> stateTable) {
        super(assembly);
        this.setPriority(Thread.NORM_PRIORITY);
        this.stateTable = stateTable;
        this.inQueue = inQueue;
        this.outQueue = outQueue;
        this.processed = 0;
    }

    @Override
    protected void componentRun() throws Exception {
        while (super.running) {
            if (this.inQueue != null) {
                if (this.stateTable != null) {
                    while (!this.inQueue.isEmpty()) {
                        synchronized (this.stateTable) {
                            //poll packet
                            PduAtomic pkt = this.inQueue.poll();
                            if (pkt.packet.hasHeader(new Tcp())) {
                                //tcp packets
                                SocketPair socks = ToolsPacket.getSocketPair(pkt.packet);
                                Tcp tcp = pkt.packet.getHeader(new Tcp());
                                //create state
                                if (!this.stateTable.containsKey(socks)) {
                                    if (tcp.flags_SYN() && !tcp.flags_ACK()) {
                                        this.stateTable.put(socks, new Connection(socks, pkt.packet.getCaptureHeader().timestampInNanos(), pkt.inbound));
                                    }
                                }
                                if (this.stateTable.containsKey(socks)) {
                                    Connection connection = this.stateTable.get(socks);
                                    pkt.setConnection(connection);
                                    //update state
                                    connection.update(pkt);
                                    //attempt to delete state
                                    if (connection.isDone()) {
                                        this.stateTable.remove(socks);
                                    }
                                }
                            }
                            //forward
                            if (this.outQueue != null) {
                                this.processed++;
                                this.outQueue.add(pkt);
                            } else {
                                throw new Exception("Error: [State Tracker] outQueue is null.");
                            }
                        }
                    }
                } else {
                    throw new Exception("Error: [State Tracker] stateTable is null.");
                }
            } else {
                throw new Exception("Error: [State Tracker] inQueue is null.");
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
            diag.add(new Diagnostic("outqueue", "Outbound Queued Packets", this.outQueue.size()));
        } else {
            diag.add(new Diagnostic("outqueue", "Outbound Queued Packets", "N/A"));
        }
        diag.add(new Diagnostic("processed", "Packets Processed", this.processed));
        return diag;
    }
}
