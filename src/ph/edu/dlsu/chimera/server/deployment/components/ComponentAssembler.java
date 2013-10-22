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
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.core.SocketPair;
import ph.edu.dlsu.chimera.server.core.Connection;
import ph.edu.dlsu.chimera.server.deployment.components.assembler.AssemblerTcp;
import ph.edu.dlsu.chimera.server.deployment.components.assembler.AssemblerUdp;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduComposite;
import ph.edu.dlsu.chimera.util.ToolsPacket;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class ComponentAssembler extends ComponentActive {

    public final ConcurrentLinkedQueue<PduAtomic> inQueue;
    public final ConcurrentLinkedQueue<PduAtomic> outQueue;
    public final ConcurrentHashMap<SocketPair, AssemblerTcp> tcpAssemblerTable;
    public final ConcurrentHashMap<Integer, AssemblerTcp> tcpPortProtocolLookup;
    public final ConcurrentHashMap<SocketPair, AssemblerUdp> udpAssemblerTable;
    public final ConcurrentHashMap<Integer, AssemblerUdp> udpPortProtocolLookup;
    public final ConcurrentHashMap<SocketPair, Connection> stateTable;

    public ComponentAssembler(Assembly assembly,
            ConcurrentLinkedQueue<PduAtomic> inQueue,
            ConcurrentLinkedQueue<PduAtomic> outQueue,
            ConcurrentHashMap<Integer, AssemblerTcp> tcpPortProtocolLookup,
            ConcurrentHashMap<Integer, AssemblerUdp> udpPortProtocolLookup,
            ConcurrentHashMap<SocketPair, Connection> stateTable) {
        super(assembly);
        this.setPriority(Thread.NORM_PRIORITY);
        this.inQueue = inQueue;
        this.outQueue = outQueue;
        this.tcpAssemblerTable = new ConcurrentHashMap<>();
        this.tcpPortProtocolLookup = tcpPortProtocolLookup;
        this.udpAssemblerTable = new ConcurrentHashMap<>();
        this.udpPortProtocolLookup = udpPortProtocolLookup;
        this.stateTable = stateTable;
    }

    @Override
    protected void componentRun() throws Exception {
        while (super.running) {
            if (this.inQueue != null) {
                while (!this.inQueue.isEmpty()) {
                    //poll packet
                    PduAtomic pkt = this.inQueue.poll();
                    if (pkt.inbound) {
                        //tcp forward
                        if (pkt.packet.hasHeader(new Tcp())) {
                            this.handleTcp(pkt);
                        }
                        //forward
                        if (this.outQueue != null) {
                            this.outQueue.add(pkt);
                        } else {
                            throw new Exception("Error: [Assembler] outQueue is null.");
                        }
                    } else {
                        throw new Exception("Error: [Assembler] Encountered outbound packet.");
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
                SocketPair socks = ToolsPacket.getSocketPair(pkt.packet);
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
            diag.add(new Diagnostic("tcpqueues", "TCP Assemblers in Deployment", this.tcpAssemblerTable.size()));
        } else {
            diag.add(new Diagnostic("tcpqueues", "TCP Assemblers in Deployment", "N/A"));
        }
        if (this.udpAssemblerTable != null) {
            diag.add(new Diagnostic("udpqueues", "UDP Assemblers in Deployment", this.udpAssemblerTable.size()));
        } else {
            diag.add(new Diagnostic("udpqueues", "UDP Assemblers in Deployment", "N/A"));
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
