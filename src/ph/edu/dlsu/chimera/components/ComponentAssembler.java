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
import ph.edu.dlsu.chimera.core.SocketPair;
import ph.edu.dlsu.chimera.core.Connection;
import ph.edu.dlsu.chimera.assembler.AssemblerTcp;
import ph.edu.dlsu.chimera.assembler.AssemblerUdp;
import ph.edu.dlsu.chimera.core.IntermodulePipe;
import ph.edu.dlsu.chimera.util.PacketUtils;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class ComponentAssembler extends ComponentActive {

    public final IntermodulePipe<PduAtomic> inQueue;
    public final IntermodulePipe<PduAtomic> outQueue;
    public final ConcurrentHashMap<SocketPair, AssemblerTcp> tcpAssemblerTable;
    public final ConcurrentHashMap<Integer, AssemblerTcp> tcpPortProtocolLookup;
    public final ConcurrentHashMap<SocketPair, AssemblerUdp> udpAssemblerTable;
    public final ConcurrentHashMap<Integer, AssemblerUdp> udpPortProtocolLookup;
    public final ConcurrentHashMap<SocketPair, Connection> stateTable;
    private long processed;

    public ComponentAssembler(IntermodulePipe<PduAtomic> inQueue,
            IntermodulePipe<PduAtomic> outQueue,
            ConcurrentHashMap<Integer, AssemblerTcp> tcpPortProtocolLookup,
            ConcurrentHashMap<Integer, AssemblerUdp> udpPortProtocolLookup,
            ConcurrentHashMap<SocketPair, Connection> stateTable) {
        this.setPriority(Thread.NORM_PRIORITY);
        this.inQueue = inQueue;
        this.outQueue = outQueue;
        if (this.inQueue != null) {
            this.inQueue.setReader(this);
        }
        if (this.outQueue != null) {
            this.outQueue.setWriter(this);
        }
        this.tcpAssemblerTable = new ConcurrentHashMap<>();
        this.tcpPortProtocolLookup = tcpPortProtocolLookup;
        this.udpAssemblerTable = new ConcurrentHashMap<>();
        this.udpPortProtocolLookup = udpPortProtocolLookup;
        this.stateTable = stateTable;
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
                while (!this.inQueue.isEmpty()) {
                    //poll packet
                    PduAtomic pkt = this.inQueue.poll();
                    synchronized (pkt) {
                        if (pkt.inbound) {
                            //tcp forward
                            try {
                                if (pkt.packet.hasHeader(new Tcp())) {
                                    this.handleTcp(pkt);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            this.processed++;
                            //forward
                            if (this.outQueue != null) {
                                this.outQueue.add(pkt);
                            }
                        } else {
                            throw new Exception("Error: [Assembler] Encountered outbound packet.");
                        }
                    }
                }
            } else {
                throw new Exception("Error: [Assembler] inQueue is null.");
            }
        }
    }

    private void handleTcp(PduAtomic pkt) throws Exception {
        if (this.tcpAssemblerTable != null) {
            if (this.tcpPortProtocolLookup != null) {
                SocketPair socks = PacketUtils.getSocketPair(pkt.packet);
                if (!this.tcpAssemblerTable.contains(socks)) {
                    //create assembler
                    AssemblerTcp asm = this.tcpPortProtocolLookup.get(socks.destinationPort);
                    if (asm != null) {
                        AssemblerTcp asmnew = (AssemblerTcp) asm.createAssemblerInstance(pkt);
                        if (asmnew != null) {
                            this.tcpAssemblerTable.put(socks, asmnew);
                        }
                    }
                }
                if (this.tcpAssemblerTable.contains(socks)) {
                    AssemblerTcp asm = this.tcpAssemblerTable.get(socks);
                    //append packet
                    asm.append(pkt);
                    //attempt delete
                    if (!this.stateTable.contains(socks)) {
                        this.tcpAssemblerTable.remove(socks);
                    }
                }
            } else {
                throw new Exception("Error: [Assembler] tcpPortProtocolLookup is null.");
            }
        } else {
            throw new Exception("Error: [Assembler] tcpAssemblerTable is null.");
        }
    }

    @Override
    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = super.getDiagnostics();
        if (this.tcpAssemblerTable != null) {
            diag.add(new Diagnostic("tcpqueues", "TCP Assemblers Active", this.tcpAssemblerTable.size()));
        } else {
            diag.add(new Diagnostic("tcpqueues", "TCP Assemblers Active", "N/A"));
        }
        if (this.udpAssemblerTable != null) {
            diag.add(new Diagnostic("udpqueues", "UDP Assemblers Active", this.udpAssemblerTable.size()));
        } else {
            diag.add(new Diagnostic("udpqueues", "UDP Assemblers Active", "N/A"));
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
