/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;
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
                            return;
                        }
                        //default forward
                        if (this.outQueue != null) {
                            this.outQueue.add(pkt);
                            return;
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
                    this.tcpAssemblerTable.put(conn, (AssemblerTCP) this.tcpPortProtocolLookup.get(conn.destinationPort).copyAssemblerType());
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
}
