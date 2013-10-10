/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.deployment.components.data.Connection;
import ph.edu.dlsu.chimera.server.deployment.components.data.ConnectionData;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.Pdu;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomicTcp;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduEnd;
import ph.edu.dlsu.chimera.server.deployment.components.handler.AssemblerTcp;
import ph.edu.dlsu.chimera.server.deployment.components.handler.AssemblerUdp;
import ph.edu.dlsu.chimera.util.PacketTools;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class ComponentAssembler extends ComponentActive {

    public final ConcurrentLinkedQueue<PduAtomic> inQueue;
    public final ConcurrentLinkedQueue<Pdu> outQueue;
    public final ConcurrentHashMap<Connection, AssemblerTcp> tcpAssemblerTable;
    public final ConcurrentHashMap<Integer, AssemblerTcp> tcpPortProtocolLookup;
    public final ConcurrentHashMap<Connection, AssemblerUdp> udpAssemblerTable;
    public final ConcurrentHashMap<Integer, AssemblerUdp> udpPortProtocolLookup;
    public final ConcurrentHashMap<Connection, ConnectionData> stateTable;

    public ComponentAssembler(Assembly assembly,
            ConcurrentLinkedQueue<PduAtomic> inQueue,
            ConcurrentLinkedQueue<Pdu> outQueue,
            ConcurrentHashMap<Integer, AssemblerTcp> tcpPortProtocolLookup,
            ConcurrentHashMap<Integer, AssemblerUdp> udpPortProtocolLookup,
            ConcurrentHashMap<Connection, ConnectionData> stateTable) {
        super(assembly);
        this.inQueue = inQueue;
        this.outQueue = outQueue;
        this.tcpAssemblerTable = new ConcurrentHashMap<Connection, AssemblerTcp>();
        this.tcpPortProtocolLookup = tcpPortProtocolLookup;
        this.udpAssemblerTable = new ConcurrentHashMap<Connection, AssemblerUdp>();
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
                    if (pkt instanceof PduEnd) {
                        //signal end
                        if (this.outQueue != null) {
                            this.outQueue.add(pkt);
                        }
                        return;
                    }
                    if (pkt.inbound) {
                        //tcp forward
                        if (pkt instanceof PduAtomicTcp) {
                            this.handleTcp((PduAtomicTcp) pkt);
                            continue;
                        }
                        //default forward
                        if (this.outQueue != null) {
                            this.outQueue.add(pkt);
                            continue;
                        }
                    }
                }
            }
        }
    }

    private void handleTcp(PduAtomicTcp pkt) {
        if (this.tcpAssemblerTable != null) {
            if (this.tcpPortProtocolLookup != null) {
                Connection conn = PacketTools.getConnection(pkt.packet);
                if (!this.tcpAssemblerTable.contains(conn)) {
                    //create assembler
                    AssemblerTcp asm = this.tcpPortProtocolLookup.get(conn.destinationPort);
                    if (asm != null) {
                        AssemblerTcp asmnew = (AssemblerTcp) asm.copyAssemblerType();
                        asmnew.setConnectionData(pkt.connectionData);
                        this.tcpAssemblerTable.put(conn, asmnew);
                    }
                }
                if (this.tcpAssemblerTable.contains(conn)) {
                    AssemblerTcp asm = this.tcpAssemblerTable.get(conn);
                    //append packet
                    asm.append(pkt);
                    //attempt delete
                    if (!this.stateTable.contains(conn)) {
                        this.tcpAssemblerTable.remove(conn);
                    }
                    //forward pdus
                    if (this.outQueue != null) {
                        Pdu pdu = asm.poll();
                        while (pdu != null) {
                            this.outQueue.add(pdu);
                            pdu = asm.poll();
                        }
                    }
                }
            }
        }
    }

    @Override
    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = super.getDiagnostics();
        if (this.tcpAssemblerTable != null) {
            diag.add(new Diagnostic("tcpqueues", "TCP Assembler in Deployment", this.tcpAssemblerTable.size()));
        } else {
            diag.add(new Diagnostic("tcpqueues", "TCP Assembler in Deployment", "N/A"));
        }
        if (this.udpAssemblerTable != null) {
            diag.add(new Diagnostic("udpqueues", "UDP Assembler in Deployment", this.udpAssemblerTable.size()));
        } else {
            diag.add(new Diagnostic("udpqueues", "UDP Assembler in Deployment", "N/A"));
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
