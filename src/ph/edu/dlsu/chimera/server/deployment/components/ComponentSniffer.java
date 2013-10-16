/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.jnetpcap.Pcap;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;

/**
 * 
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class ComponentSniffer extends ComponentActive implements PcapPacketHandler<Pcap> {

    public final boolean inbound;
    public final ConcurrentLinkedQueue<PduAtomic> outQueue;
    public final Pcap inPcap;
    public int received;

    public ComponentSniffer(Assembly assembly,
            Pcap inPcap,
            ConcurrentLinkedQueue<PduAtomic> outQueue,
            boolean inbound) {
        super(assembly);
        this.setPriority(Thread.MAX_PRIORITY);
        this.inbound = inbound;
        this.outQueue = outQueue;
        this.received = 0;
        this.inPcap = inPcap;
    }

    @Override
    public void componentRun() throws Exception {
        switch (this.inPcap.loop(-1, this, this.inPcap)) {
            case 0:
                throw new Exception("Count exhausted.");
            case -1:
                throw new Exception("Pcap loop error.");
            case -2:
                throw new Exception("Break loop called.");
        }
        this.inPcap.close();
    }

    public void nextPacket(PcapPacket pp, Pcap t) {
        this.received++;
        if (this.outQueue != null) {
            this.outQueue.add(new PduAtomic(t, pp, this.inbound));
        }
    }

    @Override
    public synchronized void kill() {
        super.kill();
        this.inPcap.breakloop();
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
