/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.assembly.components;

import com.gremwell.jnetbridge.IngressPacket;
import com.gremwell.jnetbridge.PcapPort;
import com.gremwell.jnetbridge.QueueingPortListener;
import java.util.ArrayList;
import org.jnetpcap.packet.PcapPacket;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.server.assembly.components.data.IntermodulePipe;
import ph.edu.dlsu.chimera.server.assembly.components.data.pdu.PduAtomic;
import ph.edu.dlsu.chimera.core.reflection.PacketFilter;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class ComponentSniffer extends ComponentActive {

    public final boolean inbound;
    public final PcapPort inPcapPort;
    public final IntermodulePipe<PduAtomic> outQueue;
    public final PacketFilter filter;
    public final boolean allowFiltered;
    private long received;

    public ComponentSniffer(PcapPort inPcapPort,
            IntermodulePipe<PduAtomic> outQueue,
            PacketFilter filter,
            boolean allowFiltered,
            boolean inbound) {
        this.setPriority(Thread.MAX_PRIORITY);
        this.inbound = inbound;
        this.inPcapPort = inPcapPort;
        this.outQueue = outQueue;
        if (this.outQueue != null) {
            this.outQueue.setWriter(this);
        }
        this.filter = filter;
        this.allowFiltered = allowFiltered;
        this.received = 0;
    }

    public ComponentSniffer(PcapPort inPcapPort,
            IntermodulePipe<PduAtomic> outQueue,
            boolean inbound) {
        this(inPcapPort, outQueue, null, true, inbound);
    }

    @Override
    public void componentRun() throws Exception {
        if (this.inPcapPort == null) {
            throw new Exception("Error: [Sniffer] Unable to access capture device.");
        }
        QueueingPortListener inQueue = new QueueingPortListener();
        this.inPcapPort.setListener(inQueue);
        while (super.running) {
            IngressPacket pkt = inQueue.receive();
            this.received++;
            if (this.outQueue != null) {
                PcapPacket pcappkt = new PcapPacket(pkt.packet);
                if (this.filter != null) {
                    if (!(this.filter.matches(pcappkt) ^ this.allowFiltered)) {
                        this.outQueue.add(new PduAtomic(pcappkt, this.inbound));
                    }
                } else {
                    if (this.allowFiltered) {
                        this.outQueue.add(new PduAtomic(pcappkt, this.inbound));
                    }
                }
            }
        }
    }

    @Override
    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = super.getDiagnostics();
        if (this.outQueue != null) {
            diag.add(new Diagnostic("outqueue", "Outbound Queued Packets", this.outQueue.size()));
        } else {
            diag.add(new Diagnostic("outqueue", "Outbound Queued Packets", "N/A"));
        }
        diag.add(new Diagnostic("received", "Packets Received", this.received));
        return diag;
    }
}
