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
import ph.edu.dlsu.chimera.server.ComponentActive;
import ph.edu.dlsu.chimera.server.deployment.components.handler.Assembler;
import ph.edu.dlsu.chimera.server.deployment.components.data.Connection;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PDU;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PDUComposite;
import ph.edu.dlsu.chimera.util.PacketTools;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class ComponentAssembler extends ComponentActive {

    public final ConcurrentLinkedQueue<PDUAtomic> inQueue;
    public final ConcurrentLinkedQueue<PDU> outQueue;
    public final ConcurrentHashMap<Connection, Assembler> streamTable;
    public final ConcurrentHashMap<Integer, Assembler> portProtocolLookupTcp;
    public final ConcurrentHashMap<Integer, Assembler> portProtocolLookupUdp;

    public ComponentAssembler(Assembly assembly,
            ConcurrentLinkedQueue<PDUAtomic> inQueue,
            ConcurrentLinkedQueue<PDU> outQueue,
            ConcurrentHashMap<Integer, Assembler> portProtocolLookupTcp,
            ConcurrentHashMap<Integer, Assembler> portProtocolLookupUdp) {
        super(assembly);
        this.inQueue = inQueue;
        this.outQueue = outQueue;
        this.streamTable = new ConcurrentHashMap<Connection, Assembler>();
        this.portProtocolLookupTcp = portProtocolLookupTcp;
        this.portProtocolLookupUdp = portProtocolLookupUdp;
    }

    @Override
    protected void componentRun() {
        while (super.running) {
            if (this.inQueue != null) {
                //poll packet
                PDUAtomic pkt = this.inQueue.poll();
                if (pkt.inbound) {
                    Connection pconn = PacketTools.getConnection(pkt.packet);
                    if (pconn != null) {
                        if (!this.streamTable.contains(pconn)) {
                            //create stream
                            if (pkt.packet.hasHeader(new Tcp())) {
                                this.streamTable.put(pconn, this.portProtocolLookupTcp.get(pconn.destinationPort).copyAssemblerType());
                            }
                            if (pkt.packet.hasHeader(new Udp())) {
                                this.streamTable.put(pconn, this.portProtocolLookupUdp.get(pconn.destinationPort).copyAssemblerType());
                            }
                        }
                        if (this.streamTable.contains(pconn)) {
                            //update stream
                            Assembler assembler = this.streamTable.get(pconn);
                            assembler.append(pkt);
                            //forward pdu
                            if (this.outQueue != null) {
                                PDU pdu = assembler.poll();
                                while (pdu != null) {
                                    this.outQueue.add(pdu);
                                    pdu = assembler.poll();
                                }
                            }
                        } else {
                            //forwarde atomic
                            if(this.outQueue != null) {
                                this.outQueue.add(pkt);
                            }
                        }
                    }
                }
            }
        }
    }
}
