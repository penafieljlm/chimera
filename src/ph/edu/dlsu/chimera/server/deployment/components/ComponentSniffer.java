/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import com.gremwell.jnetbridge.IngressPacket;
import com.gremwell.jnetbridge.PcapPort;
import com.gremwell.jnetbridge.QueueingPortListener;
import java.util.ArrayList;
import org.jnetpcap.packet.PcapPacket;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.server.deployment.components.data.IntermodulePipe;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class ComponentSniffer extends ComponentActive {

    public final boolean inbound;
    public final QueueingPortListener inQueue;
    public final IntermodulePipe<PduAtomic> outQueue;
    private long received;

    public ComponentSniffer(Assembly assembly,
            PcapPort inPcapPort,
            IntermodulePipe<PduAtomic> outQueue,
            boolean inbound) {
        super(assembly);
        this.setPriority(Thread.MAX_PRIORITY);
        this.inbound = inbound;
        this.inQueue = new QueueingPortListener();
        inPcapPort.setListener(this.inQueue);
        this.outQueue = outQueue;
        if (this.outQueue != null) {
            this.outQueue.setWriter(this);
        }
        this.received = 0;
    }

    @Override
    public void componentRun() throws Exception {
        while (super.running) {
            if (this.inQueue != null) {
                IngressPacket pkt = this.inQueue.receive();
                this.received++;
                if (this.outQueue != null) {
                    this.outQueue.add(new PduAtomic(new PcapPacket(pkt.packet), this.inbound));
                }
            } else {
                throw new Exception("Error: [Sniffer] Unable to access capture device.");
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
