/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.components;

import java.util.ArrayList;
import org.jnetpcap.Pcap;
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
public final class ComponentSniffer extends ComponentActiveProcessor<PcapPacket, PduAtomic> implements PcapPacketHandler<Pcap> {

    public final String inPcapIf;
    public final PacketFilter accessFilter;
    public final boolean allowFiltered;
    public final boolean ingress;
    public final int direction;
    private Pcap pcap;
    private StringBuilder errBuff;

    public ComponentSniffer(IntermodulePipe<PduAtomic> outQueue,
            String inPcapIf,
            PacketFilter accessFilter,
            boolean allowFiltered,
            boolean ingress,
            int direction) {
        super(null, outQueue);
        this.setPriority(Thread.MAX_PRIORITY);
        this.inPcapIf = inPcapIf;
        this.accessFilter = accessFilter;
        this.allowFiltered = allowFiltered;
        this.ingress = ingress;
        this.direction = direction;
    }

    public ComponentSniffer(IntermodulePipe<PduAtomic> outQueue,
            String inPcapIf,
            boolean ingress,
            int direction) {
        this(outQueue, inPcapIf, null, true, ingress, direction);
    }

    @Override
    protected void preLoop() throws Exception {
        if (!Pcap.isPcap100Loaded()) {
            throw new Exception("Error: [Sniffer] libpcap 1.0.0 must be supported.");
        }
        if (this.inPcapIf == null) {
            throw new Exception("Error: [Sniffer] Unable to access capture device.");
        }
        this.errBuff = new StringBuilder();
        try {
            this.pcap = Pcap.create(this.inPcapIf, this.errBuff);
            if (this.pcap == null) {
                throw new Exception("Error: [Sniffer] Unable to open interface '" + this.inPcapIf + "'.");
            }
        } catch (Exception ex) {
            throw new Exception("Error: [Sniffer] Unable to open interface '" + this.inPcapIf + "'.");
        }
        if (this.pcap.setSnaplen(64 * 1024) != Pcap.OK) {
            throw new Exception("Error: [Sniffer] Unable to set snaplen.");
        }
        if (this.pcap.setPromisc(Pcap.MODE_PROMISCUOUS) != Pcap.OK) {
            throw new Exception("Error: [Sniffer] Unable to set promisc.");
        }
        if (this.pcap.setTimeout(10 * 1000) != Pcap.OK) {
            throw new Exception("Error: [Sniffer] Unable to set timeout.");
        }
        if (this.pcap.setBufferSize(128 * 1024 * 1024) != Pcap.OK) {
            throw new Exception("Error: [Sniffer] Unable to set buffer size.");
        }
        if (this.pcap.setDirection(this.direction) == Pcap.ERROR) {
            throw new Exception("Error: [Sniffer] Unable to set direction.");
        }
        if (this.pcap.activate() != Pcap.OK) {
            throw new Exception("Error: [Sniffer] Unable to activate.");
        }
    }

    @Override
    public void componentRun() throws Exception {
        this.preLoop();
        switch (this.pcap.loop(-1, this, this.pcap)) {
            case Pcap.OK:
                break;
            case Pcap.ERROR:
                throw new Exception("Error: [Sniffer] " + this.errBuff.toString());
            case Pcap.ERROR_BREAK:
                break;
        }
        this.postLoop();
    }

    @Override
    protected PduAtomic process(PcapPacket input) throws Exception {
        if (this.outQueue != null) {
            boolean allow = this.allowFiltered;
            if (this.accessFilter != null) {
                allow = !(this.accessFilter.matches(input) ^ allow);
            }
            if (allow) {
                return new PduAtomic(input, this.ingress);
            }
        }
        return null;
    }

    @Override
    public void nextPacket(PcapPacket packet, Pcap user) {
        try {
            PduAtomic out = this.process(packet);
            if (this.outQueue != null && out != null) {
                this.outQueue.add(out);
            }
            this.processed++;
        } catch (Exception ex) {
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
        return diag;
    }
}
