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
import ph.edu.dlsu.chimera.server.deployment.components.data.SocketPair;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.deployment.components.data.Connection;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomicEnd;

/**
 * Tracks states and ensures TCP delivery.
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class ComponentStateTracker extends ComponentActive {

    public final boolean inbound;
    public final ConcurrentHashMap<SocketPair, Connection> stateTable;
    public final ConcurrentLinkedQueue<PduAtomic> inQueue;
    public final ConcurrentLinkedQueue<PduAtomic> outQueue;

    public ComponentStateTracker(Assembly assembly,
            ConcurrentLinkedQueue<PduAtomic> inQueue,
            ConcurrentLinkedQueue<PduAtomic> outQueue,
            ConcurrentHashMap<SocketPair, Connection> stateTable,
            boolean inbound) {
        super(assembly);
        this.setPriority(Thread.NORM_PRIORITY);
        this.stateTable = stateTable;
        this.inQueue = inQueue;
        this.outQueue = outQueue;
        this.inbound = inbound;
    }

    @Override
    protected void componentRun() throws Exception {
        while (super.running) {
            if (this.inQueue != null && this.stateTable != null) {
                while (!this.inQueue.isEmpty()) {
                    //poll packet
                    PduAtomic pkt = this.inQueue.poll();
                    if (pkt instanceof PduAtomicEnd) {
                        //signal end
                        if (this.outQueue != null) {
                            this.outQueue.add(pkt);
                        }
                        return;
                    }
                    if (pkt.packet.hasHeader(new Tcp())) {
                        //tcp packets
                        SocketPair socks = ToolsPacket.getSocketPair(pkt.packet);
                        Tcp tcp = pkt.packet.getHeader(new Tcp());
                        //create state
                        if (!this.stateTable.containsKey(socks)) {
                            if (tcp.flags_SYN() && !tcp.flags_ACK()) {
                                this.stateTable.put(socks, new Connection(socks, pkt.packet.getCaptureHeader().timestampInNanos(), this.inbound));
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
            diag.add(new Diagnostic("outquque", "Outbound Queued Packets", this.outQueue.size()));
        } else {
            diag.add(new Diagnostic("outqueue", "Outbound Queued Packets", "N/A"));
        }
        return diag;
    }
}
