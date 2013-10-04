/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.jnetpcap.packet.PcapPacket;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.ComponentActive;
import ph.edu.dlsu.chimera.server.deployment.components.handler.Assembler;
import ph.edu.dlsu.chimera.server.deployment.components.data.Connection;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PDU;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class PDUAssembler extends ComponentActive {

    public final ConcurrentLinkedQueue<PcapPacket> inQueue;
    public final ConcurrentLinkedQueue<PDU> outQueue;
    public final ConcurrentHashMap<Connection, Assembler> streamTable;

    public PDUAssembler(Assembly assembly, ConcurrentLinkedQueue<PcapPacket> inQueue, ConcurrentLinkedQueue<PDU> outQueue) {
        super(assembly);
        this.inQueue = inQueue;
        this.outQueue = outQueue;
        this.streamTable = new ConcurrentHashMap<Connection, Assembler>();
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
