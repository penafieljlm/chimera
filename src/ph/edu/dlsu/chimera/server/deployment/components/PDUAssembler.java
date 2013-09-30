/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.jnetpcap.packet.PcapPacket;
import ph.edu.dlsu.chimera.util.PacketTools;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.Component;
import ph.edu.dlsu.chimera.server.deployment.components.handler.ProtocolHandler;
import ph.edu.dlsu.chimera.server.deployment.components.data.Connection;
import ph.edu.dlsu.chimera.server.deployment.components.data.ConnectionData;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PDU;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PDUAssembler extends Component {

    public final ConcurrentLinkedQueue<PcapPacket> inQueue;
    public final ConcurrentLinkedQueue<PDU> outQueue;
    public final ConcurrentHashMap<Connection, ConnectionData> stateTable;

    public PDUAssembler(Assembly assembly, ConcurrentLinkedQueue<PcapPacket> inQueue, ConcurrentLinkedQueue<PDU> outQueue, ConcurrentHashMap<Connection, ConnectionData> stateTable) {
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
                PcapPacket pkt = this.inQueue.poll();
                //get connection
                Connection conn = PacketTools.getConnection(pkt);
                if (conn != null) {
                    //connection valid
                    if (this.stateTable.containsKey(conn)) {
                        //connection exists
                        ProtocolHandler handler = this.stateTable.get(conn).inboundHandler;
                        if (handler != null) {
                            //handler exists : append and assemble pdu
                            handler.appendPDU(pkt);
                            if (handler.assemblePDU()) {
                                //assembly done
                                PDU pdu = handler.poll();
                                if (pdu != null) {
                                    //pdu is ok
                                    if (this.outQueue != null) {
                                        //out queue exists : push
                                        this.outQueue.add(pdu);
                                    }
                                }
                            }
                        }
                    } else {
                        //connection does not exists (ie. destroyed)
                        if (this.outQueue != null) {
                            //out queue exists : push atomic PDU
                            this.outQueue.add(pdu);
                        }
                    }
                } else {
                    //TODO: implement for non port protocols
                }
            }
        }
    }
}
