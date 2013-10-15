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
import ph.edu.dlsu.chimera.server.deployment.components.data.SocketPair;
import ph.edu.dlsu.chimera.server.deployment.components.data.Connection;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomicEnd;
import ph.edu.dlsu.chimera.server.deployment.components.assembler.AssemblerTcp;
import ph.edu.dlsu.chimera.server.deployment.components.assembler.AssemblerUdp;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduComposite;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduCompositeEnd;
import ph.edu.dlsu.chimera.util.ToolsPacket;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class ComponentAssembler extends ComponentActive {

    public final ConcurrentLinkedQueue<PduAtomic> inQueue;
    public final ConcurrentLinkedQueue<PduComposite> outQueueComposite;
    public final ConcurrentLinkedQueue<PduAtomic> outQueueAtomic;
    public final ConcurrentHashMap<SocketPair, AssemblerTcp> tcpAssemblerTable;
    public final ConcurrentHashMap<Integer, AssemblerTcp> tcpPortProtocolLookup;
    public final ConcurrentHashMap<SocketPair, AssemblerUdp> udpAssemblerTable;
    public final ConcurrentHashMap<Integer, AssemblerUdp> udpPortProtocolLookup;
    public final ConcurrentHashMap<SocketPair, Connection> stateTable;

    public ComponentAssembler(Assembly assembly,
            ConcurrentLinkedQueue<PduAtomic> inQueue,
            ConcurrentLinkedQueue<PduComposite> outQueueComposite,
            ConcurrentLinkedQueue<PduAtomic> outQueueAtomic,
            ConcurrentHashMap<Integer, AssemblerTcp> tcpPortProtocolLookup,
            ConcurrentHashMap<Integer, AssemblerUdp> udpPortProtocolLookup,
            ConcurrentHashMap<SocketPair, Connection> stateTable) {
        super(assembly);
        this.setPriority(Thread.NORM_PRIORITY);
        this.inQueue = inQueue;
        this.outQueueComposite = outQueueComposite;
        this.outQueueAtomic = outQueueAtomic;
        this.tcpAssemblerTable = new ConcurrentHashMap<SocketPair, AssemblerTcp>();
        this.tcpPortProtocolLookup = tcpPortProtocolLookup;
        this.udpAssemblerTable = new ConcurrentHashMap<SocketPair, AssemblerUdp>();
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
                    if (pkt instanceof PduAtomicEnd) {
                        //signal end
                        if (this.outQueueComposite != null) {
                            this.outQueueComposite.add(new PduCompositeEnd(pkt.inbound));
                        }
                        if (this.outQueueAtomic != null) {
                            this.outQueueAtomic.add(pkt);
                        }
                        return;
                    }
                    if (pkt.inbound) {
                        //tcp forward
                        if (pkt.packet.hasHeader(new Tcp())) {
                            this.handleTcp(pkt);
                            continue;
                        }
                        //default forward
                        if (this.outQueueAtomic != null) {
                            this.outQueueAtomic.add(pkt);
                            continue;
                        }
                    }
                }
            }
        }
    }

    private void handleTcp(PduAtomic pkt) {
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
                    //forward pdus
                    if (this.outQueueComposite != null) {
                        PduComposite pdu = asm.poll();
                        while (pdu != null) {
                            this.outQueueComposite.add(pdu);
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
        if (this.outQueueComposite != null) {
            diag.add(new Diagnostic("outquque", "Outbound Queued Packets", this.outQueueComposite.size()));
        } else {
            diag.add(new Diagnostic("outqueue", "Outbound Queued Packets", "N/A"));
        }
        return diag;
    }
}
