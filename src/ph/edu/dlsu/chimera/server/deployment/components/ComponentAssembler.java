/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PDUAtomic;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.deployment.components.data.Connection;
import ph.edu.dlsu.chimera.server.deployment.components.data.ConnectionData;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PDU;
import ph.edu.dlsu.chimera.server.deployment.components.handler.AssemblerTCP;
import ph.edu.dlsu.chimera.server.deployment.components.handler.AssemblerUDP;
import ph.edu.dlsu.chimera.util.PacketTools;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class ComponentAssembler extends ComponentActive {

    public final ConcurrentLinkedQueue<PDUAtomic> inQueue;
    public final ConcurrentLinkedQueue<PDU> outQueue;
    public final ConcurrentHashMap<Connection, AssemblerTCP> tcpAssemblerTable;
    public final ConcurrentHashMap<Integer, AssemblerTCP> tcpPortProtocolLookup;
    public final ConcurrentHashMap<Connection, AssemblerUDP> udpAssemblerTable;
    public final ConcurrentHashMap<Integer, AssemblerUDP> udpPortProtocolLookup;
    public final ConcurrentHashMap<Connection, ConnectionData> stateTable;

    public ComponentAssembler(Assembly assembly,
            ConcurrentLinkedQueue<PDUAtomic> inQueue,
            ConcurrentLinkedQueue<PDU> outQueue,
            ConcurrentHashMap<Integer, AssemblerTCP> tcpPortProtocolLookup,
            ConcurrentHashMap<Integer, AssemblerUDP> udpPortProtocolLookup,
            ConcurrentHashMap<Connection, ConnectionData> stateTable) {
        super(assembly);
        this.inQueue = inQueue;
        this.outQueue = outQueue;
        this.tcpAssemblerTable = new ConcurrentHashMap<Connection, AssemblerTCP>();
        this.tcpPortProtocolLookup = tcpPortProtocolLookup;
        this.udpAssemblerTable = new ConcurrentHashMap<Connection, AssemblerUDP>();
        this.udpPortProtocolLookup = udpPortProtocolLookup;
        this.stateTable = stateTable;
    }

    @Override
    protected void componentRun() {
        while (super.running) {
            if (this.inQueue != null) {
                while (!this.inQueue.isEmpty()) {
                    //poll packet
                    PDUAtomic pkt = this.inQueue.poll();
                    if (pkt.inbound) {
                        //tcp forward
                        if (pkt.packet.hasHeader(new Tcp())) {
                            this.handleTcp(pkt);
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

    private void handleTcp(PDUAtomic pkt) {
        if (this.tcpAssemblerTable != null) {
            if (this.tcpPortProtocolLookup != null) {
                Connection conn = PacketTools.getConnection(pkt.packet);
                if (!this.tcpAssemblerTable.contains(conn)) {
                    //create assembler
                    AssemblerTCP asm = this.tcpPortProtocolLookup.get(conn.destinationPort);
                    if (asm != null) {
                        this.tcpAssemblerTable.put(conn, (AssemblerTCP) asm.copyAssemblerType());
                    }
                }
                if (this.tcpAssemblerTable.contains(conn)) {
                    AssemblerTCP asm = this.tcpAssemblerTable.get(conn);
                    //append packet
                    asm.append(pkt);
                    //attempt delete
                    if (!this.stateTable.contains(conn)) {
                        this.tcpAssemblerTable.remove(conn);
                    }
                    //forward pdus
                    if (this.outQueue != null) {
                        PDU pdu = asm.poll();
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
