/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.sourceforge.jpcap.net.Packet;
import net.sourceforge.jpcap.net.TCPPacket;
import ph.edu.dlsu.chimera.core.PacketTools;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.Component;
import ph.edu.dlsu.chimera.server.deployment.components.assembler.Assembler;
import ph.edu.dlsu.chimera.server.deployment.components.data.Connection;
import ph.edu.dlsu.chimera.server.deployment.components.data.ConnectionData;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PDU;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PDUAssembler extends Component {

    public final ConcurrentLinkedQueue<Packet> inQueue;
    public final ConcurrentLinkedQueue<PDU> outQueue;
    public final ConcurrentHashMap<Connection, ConnectionData> stateTable;

    public PDUAssembler(Assembly assembly, ConcurrentLinkedQueue<Packet> inQueue, ConcurrentLinkedQueue<PDU> outQueue, ConcurrentHashMap<Connection, ConnectionData> stateTable) {
        super(assembly);
        this.inQueue = inQueue;
        this.outQueue = outQueue;
        this.stateTable = stateTable;
    }

    @Override
    protected void componentRun() {
        while (super.running) {
            if (this.inQueue != null && this.stateTable != null) {
                //poll packet
                Packet pkt = this.inQueue.poll();
                //get connection
                Connection conn = PacketTools.getConnection(pkt);
                if (conn != null) {
                    if (this.stateTable.containsKey(conn)) {
                        //build messages
                        Assembler assembler = this.stateTable.get(conn).inboundAssembler;
                        if (assembler != null) {
                            if (!assembler.isDone()) {
                                assembler.assemblePDU(pkt);
                            }
                            if (assembler.isDone()) {
                                PDU pdu = assembler.poll();
                                if (pdu != null) {
                                    if (this.outQueue != null) {
                                        this.outQueue.add(pdu);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
