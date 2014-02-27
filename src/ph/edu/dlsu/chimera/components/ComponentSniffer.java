/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.components;

import java.util.ArrayList;
import java.util.Arrays;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapUtils;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.lan.Ethernet;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.core.TrafficDirection;
import ph.edu.dlsu.chimera.core.tools.IntermodulePipe;
import ph.edu.dlsu.chimera.core.PduAtomic;
import ph.edu.dlsu.chimera.reflection.PacketFilter;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class ComponentSniffer extends ComponentActiveProcessor<PcapPacket, PduAtomic> implements PcapPacketHandler<Pcap> {

    public final String pcapIf;
    public final PacketFilter accessFilter;
    public final boolean allowFiltered;
    private Pcap pcap;
    private StringBuilder errBuff;
    private byte[] interfaceMacAddress;

    public ComponentSniffer(IntermodulePipe<PduAtomic> outQueue,
            String inPcapIf,
            PacketFilter accessFilter,
            boolean allowFiltered) {
        super(null, outQueue);
        this.setPriority(Thread.MAX_PRIORITY);
        this.pcapIf = inPcapIf;
        this.accessFilter = accessFilter;
        this.allowFiltered = allowFiltered;
    }

    public ComponentSniffer(IntermodulePipe<PduAtomic> outQueue,
            String inPcapIf) {
        this(outQueue, inPcapIf, null, true);
    }

    @Override
    protected void preLoop() throws Exception {
        if (this.pcapIf == null) {
            throw new Exception("Error: [Sniffer] Unable to access capture device.");
        }
        this.errBuff = new StringBuilder();
        try {
            this.pcap = Pcap.openLive(this.pcapIf, 64 * 1024, Pcap.MODE_PROMISCUOUS, 10 * 1000, this.errBuff);
            if (this.pcap == null) {
                throw new Exception("Error: [Sniffer] Unable to open interface '" + this.pcapIf + "'.");
            }
        } catch (Exception ex) {
            throw new Exception("Error: [Sniffer] Unable to open interface '" + this.pcapIf + "'.");
        }
        this.interfaceMacAddress = PcapUtils.getHardwareAddress(this.pcapIf);
        if (this.interfaceMacAddress == null) {
            throw new Exception("Error: [Sniffer] Unable to get MAC Address.");
        }
    }

    @Override
    public void componentRun() throws Exception {
        this.preLoop();
        switch (this.pcap.loop(-1, this, this.pcap)) {
            case 0:
                break;
            case -1:
                throw new Exception("Error: [Sniffer] " + this.errBuff.toString());
            case -2:
                break;
        }
        this.postLoop();
    }

    @Override
    protected PduAtomic process(PcapPacket input) throws Exception {
        if (input.hasHeader(new Ethernet())) {
            if (this.outQueue != null) {
                boolean allow = this.allowFiltered;
                if (this.accessFilter != null) {
                    allow = !(this.accessFilter.matches(input) ^ allow);
                }
                if (allow) {
                    Ethernet eth = input.getHeader(new Ethernet());
                    if (Arrays.equals(this.interfaceMacAddress, eth.destination())) {
                        //ingress to iface, egress to network
                        return new PduAtomic(input, TrafficDirection.Egress);
                    } else if (Arrays.equals(this.interfaceMacAddress, eth.source())) {
                        //egress to iface, ingress to network
                        return new PduAtomic(input, TrafficDirection.Ingress);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void nextPacket(PcapPacket packet, Pcap user) {
        try {
            super.ingressStats.commitEncounter(this.getProcessedTimestampInNanos(packet), this.getProcessedSize(packet));
            PduAtomic out = this.process(packet);
            if (this.outQueue != null && out != null) {
                this.outQueue.add(out);
                super.egressStats.commitEncounter(out.timestampInNanos, out.size());
            }
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

    @Override
    public long getProcessedTimestampInNanos(PcapPacket input) {
        return input.getCaptureHeader().timestampInNanos();
    }

    @Override
    public long getProcessedSize(PcapPacket input) {
        return input.size();
    }

    @Override
    public synchronized void kill() {
        super.kill();
        this.pcap.breakloop();
    }
}
