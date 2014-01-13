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
import ph.edu.dlsu.chimera.core.TcpSocketPair;
import ph.edu.dlsu.chimera.core.Connection;
import ph.edu.dlsu.chimera.assembler.AssemblerTcp;
import ph.edu.dlsu.chimera.assembler.AssemblerUdp;
import ph.edu.dlsu.chimera.core.TrafficDirection;
import ph.edu.dlsu.chimera.core.tools.IntermodulePipe;
import ph.edu.dlsu.chimera.util.UtilsPacket;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class ComponentAssembler extends ComponentActiveProcessor<PduAtomic, PduAtomic> {

    public final ConcurrentHashMap<TcpSocketPair, AssemblerTcp> tcpAssemblerTable;
    public final ConcurrentHashMap<Integer, AssemblerTcp> tcpPortProtocolLookup;
    public final ConcurrentHashMap<TcpSocketPair, AssemblerUdp> udpAssemblerTable;
    public final ConcurrentHashMap<Integer, AssemblerUdp> udpPortProtocolLookup;
    public final ConcurrentHashMap<TcpSocketPair, Connection> stateTable;

    public ComponentAssembler(IntermodulePipe<PduAtomic> inQueue,
            IntermodulePipe<PduAtomic> outQueue,
            ConcurrentHashMap<Integer, AssemblerTcp> tcpPortProtocolLookup,
            ConcurrentHashMap<Integer, AssemblerUdp> udpPortProtocolLookup,
            ConcurrentHashMap<TcpSocketPair, Connection> stateTable) {
        super(inQueue, outQueue);
        this.setPriority(Thread.NORM_PRIORITY);
        this.tcpAssemblerTable = new ConcurrentHashMap<>();
        this.tcpPortProtocolLookup = tcpPortProtocolLookup;
        this.udpAssemblerTable = new ConcurrentHashMap<>();
        this.udpPortProtocolLookup = udpPortProtocolLookup;
        this.stateTable = stateTable;
    }

    @Override
    protected PduAtomic process(PduAtomic input) throws Exception {
        if (input.direction == TrafficDirection.Ingress) {
            try {
                if (input.packet.hasHeader(new Tcp())) {
                    this.handleTcp(input);
                }
            } catch (Exception ex) {
            }
            return input;
        } else {
            throw new Exception("Error: [Assembler] Encountered egress packet.");
        }
    }

    private void handleTcp(PduAtomic pkt) throws Exception {
        if (this.tcpAssemblerTable != null) {
            if (this.tcpPortProtocolLookup != null) {
                TcpSocketPair socks = UtilsPacket.getSocketPair(pkt.packet);
                if (!this.tcpAssemblerTable.containsKey(socks)) {
                    //create assembler
                    AssemblerTcp asm = this.tcpPortProtocolLookup.get(socks.destinationPort);
                    if (asm != null) {
                        AssemblerTcp asmnew = (AssemblerTcp) asm.createAssemblerInstance(pkt);
                        if (asmnew != null) {
                            this.tcpAssemblerTable.put(socks, asmnew);
                        }
                    }
                }
                if (this.tcpAssemblerTable.containsKey(socks)) {
                    AssemblerTcp asm = this.tcpAssemblerTable.get(socks);
                    //append packet
                    asm.append(pkt);
                    //attempt delete
                    if (!this.stateTable.containsKey(socks)) {
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
        return diag;
    }
}
