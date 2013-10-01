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
import ph.edu.dlsu.chimera.server.deployment.components.handler.Assembler;
import ph.edu.dlsu.chimera.server.deployment.components.data.Connection;
import ph.edu.dlsu.chimera.server.deployment.components.data.ConnectionData;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PDU;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PDUAssembler extends Component {

    private final ConcurrentLinkedQueue<PcapPacket> inQueue;
    private final ConcurrentLinkedQueue<PDU> outQueue;
    private final ConcurrentHashMap<Connection, 

    public PDUAssembler(Assembly assembly, ConcurrentLinkedQueue<PcapPacket> inQueue, ConcurrentLinkedQueue<PDU> outQueue) {
        super(assembly);
        this.inQueue = inQueue;
        this.outQueue = outQueue;
    }

    @Override
    protected void componentRun() {
        while (super.running) {
            if (this.inQueue != null) {
                //poll packet
                PcapPacket pkt = this.inQueue.poll();
                //
            }
        }
    }
}
