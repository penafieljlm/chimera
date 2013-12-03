/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.components;

import java.util.ArrayList;
import org.jnetpcap.Pcap;
import org.jnetpcap.Pcap.Direction;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.core.tools.IntermodulePipe;
import ph.edu.dlsu.chimera.pdu.PduAtomic;
import ph.edu.dlsu.chimera.reflection.PacketFilter;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class ComponentSniffer extends ComponentActive implements PcapPacketHandler<Pcap> {

    public final String inPcapIf;
    public final IntermodulePipe<PduAtomic> outQueue;
    public final PacketFilter accessFilter;
    public final boolean allowFiltered;
    public final boolean ingress;
    public final int direction;
    private long received;

    public ComponentSniffer(IntermodulePipe<PduAtomic> outQueue,
            String inPcapIf,
            PacketFilter accessFilter,
            boolean allowFiltered,
            boolean ingress,
            int direction) {
        this.setPriority(Thread.MAX_PRIORITY);
        this.inPcapIf = inPcapIf;
        this.outQueue = outQueue;
        if (this.outQueue != null) {
            this.outQueue.setWriter(this);
        }
        this.accessFilter = accessFilter;
        this.allowFiltered = allowFiltered;
        this.ingress = ingress;
        this.direction = direction;
        this.received = 0;
    }

    public ComponentSniffer(IntermodulePipe<PduAtomic> outQueue,
            String inPcapIf,
            boolean ingress,
            int direction) {
        this(outQueue, inPcapIf, null, true, ingress, direction);
    }

    @Override
    public void componentRun() throws Exception {
        if(!Pcap.isPcap100Loaded()) {
            throw new Exception("Error: [Sniffer] libpcap 1.0.0 must be supported.");
        }
        if (this.inPcapIf == null) {
            throw new Exception("Error: [Sniffer] Unable to access capture device.");
        }
        StringBuilder errBuff = new StringBuilder();
        Pcap pcap;
        try {
            pcap = Pcap.create(this.inPcapIf, errBuff);
            if (pcap == null) {
                throw new Exception("Error: [Sniffer] Unable to open interface '" + this.inPcapIf + "'.");
            }
        } catch (Exception ex) {
            throw new Exception("Error: [Sniffer] Unable to open interface '" + this.inPcapIf + "'.");
        }
        if (pcap.setSnaplen(64 * 1024) != Pcap.OK) {
            throw new Exception("Error: [Sniffer] Unable to set snaplen.");
        }
        if (pcap.setPromisc(Pcap.MODE_PROMISCUOUS) != Pcap.OK) {
            throw new Exception("Error: [Sniffer] Unable to set promisc.");
        }
        if (pcap.setTimeout(10 * 1000) != Pcap.OK) {
            throw new Exception("Error: [Sniffer] Unable to set timeout.");
        }
        if (pcap.setBufferSize(128 * 1024 * 1024) != Pcap.OK) {
            throw new Exception("Error: [Sniffer] Unable to set buffer size.");
        }
        if (pcap.setDirection(this.direction) == Pcap.ERROR) {
            throw new Exception("Error: [Sniffer] Unable to set direction.");
        }
        if (pcap.activate() != Pcap.OK) {
            throw new Exception("Error: [Sniffer] Unable to activate.");
        }
        switch (pcap.loop(-1, this, pcap)) {
            case Pcap.OK:
                break;
            case Pcap.ERROR:
                throw new Exception("Error: [Sniffer] " + errBuff.toString());
            case Pcap.ERROR_BREAK:
                break;
        }
    }

    @Override
    public void nextPacket(PcapPacket packet, Pcap user) {
        this.received++;
        if (this.outQueue != null) {
            boolean allow = this.allowFiltered;
            if (this.accessFilter != null) {
                allow = !(this.accessFilter.matches(packet) ^ allow);
            }
            if (allow) {
                this.outQueue.add(new PduAtomic(packet, this.ingress));
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
