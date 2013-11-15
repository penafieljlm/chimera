/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.components;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import org.jnetpcap.protocol.tcpip.Tcp;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.pdu.PduAtomic;
import ph.edu.dlsu.chimera.util.PacketUtils;
import ph.edu.dlsu.chimera.core.SocketPair;
import ph.edu.dlsu.chimera.core.Connection;
import ph.edu.dlsu.chimera.core.IntermodulePipe;

/**
 * Tracks states and ensures TCP delivery.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class ComponentStateTracker extends ComponentActive {

    public final ConcurrentHashMap<SocketPair, Connection> stateTable;
    public final IntermodulePipe<PduAtomic> inQueue;
    public final IntermodulePipe<PduAtomic> outQueue;
    private long processed;

    public ComponentStateTracker(IntermodulePipe<PduAtomic> inQueue,
            IntermodulePipe<PduAtomic> outQueue,
            ConcurrentHashMap<SocketPair, Connection> stateTable) {
        this.setPriority(Thread.NORM_PRIORITY);
        this.stateTable = stateTable;
        this.inQueue = inQueue;
        this.outQueue = outQueue;
        if (this.inQueue != null) {
            this.inQueue.setReader(this);
        }
        if (this.outQueue != null) {
            this.outQueue.setWriter(this);
        }
        this.processed = 0;
    }

    @Override
    protected void componentRun() throws Exception {
        while (super.running) {
            if (this.inQueue != null) {
                if (this.inQueue.isEmpty()) {
                    synchronized (this) {
                        this.wait();
                    }
                }
                if (this.stateTable != null) {
                    while (!this.inQueue.isEmpty()) {
                        synchronized (this.stateTable) {
                            //poll packet
                            PduAtomic pkt = this.inQueue.poll();
                            synchronized (pkt) {
                                try {
                                    if (pkt.packet.hasHeader(new Tcp())) {
                                        //tcp packets
                                        SocketPair socks = PacketUtils.getSocketPair(pkt.packet);
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
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                                this.processed++;
                                //forward
                                if (this.outQueue != null) {
                                    this.outQueue.add(pkt);
                                }
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
