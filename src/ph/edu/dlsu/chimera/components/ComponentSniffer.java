/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.components;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.lan.Ethernet;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.core.TrafficDirection;
import ph.edu.dlsu.chimera.core.tools.IntermodulePipe;
import ph.edu.dlsu.chimera.pdu.PduAtomic;
import ph.edu.dlsu.chimera.reflection.PacketFilter;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class ComponentSniffer extends ComponentActiveProcessor<PcapPacket, PduAtomic> implements PcapPacketHandler<Pcap> {

    public final String pcapIf;
    public final PacketFilter accessFilter;
    public final boolean allowFiltered;
    public final TrafficDirection sniffDirection;
    public final TrafficDirection packetDirectionFlag;
    private Pcap pcap;
    private StringBuilder errBuff;
    private byte[] interfaceMacAddress;

    public ComponentSniffer(IntermodulePipe<PduAtomic> outQueue,
            String inPcapIf,
            PacketFilter accessFilter,
            boolean allowFiltered,
            TrafficDirection sniffDirection,
            TrafficDirection packetFlagDirection) {
        super(null, outQueue);
        this.setPriority(Thread.MAX_PRIORITY);
        this.pcapIf = inPcapIf;
        this.accessFilter = accessFilter;
        this.allowFiltered = allowFiltered;
        this.sniffDirection = sniffDirection;
        this.packetDirectionFlag = packetFlagDirection;
    }

    public ComponentSniffer(IntermodulePipe<PduAtomic> outQueue,
            String inPcapIf,
            TrafficDirection sniffDirection,
            TrafficDirection packetFlagDirection) {
        this(outQueue, inPcapIf, null, true, sniffDirection, packetFlagDirection);
    }

    @Override
    protected void preLoop() throws Exception {
        if (!Pcap.isPcap100Loaded()) {
            throw new Exception("Error: [Sniffer] libpcap 1.0.0 must be supported.");
        }
        if (this.pcapIf == null) {
            throw new Exception("Error: [Sniffer] Unable to access capture device.");
        }
        this.errBuff = new StringBuilder();
        try {
            this.pcap = Pcap.create(this.pcapIf, this.errBuff);
            if (this.pcap == null) {
                throw new Exception("Error: [Sniffer] Unable to open interface '" + this.pcapIf + "'.");
            }
        } catch (Exception ex) {
            throw new Exception("Error: [Sniffer] Unable to open interface '" + this.pcapIf + "'.");
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
        if (this.pcap.activate() != Pcap.OK) {
            throw new Exception("Error: [Sniffer] Unable to activate.");
        }
        for (PcapIf iface : PcapIf.findAllDevs(this.errBuff)) {
            if (iface.getName().equals(this.pcapIf)) {
                try {
                    this.interfaceMacAddress = iface.getHardwareAddress();
                } catch (IOException ex) {
                }
                break;
            }
        }
        if (this.interfaceMacAddress == null) {
            throw new Exception("Error: [Sniffer] Unable to get MAC Address.");
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
        if (input.hasHeader(new Ethernet())) {
            Ethernet eth = input.getHeader(new Ethernet());
            if (this.outQueue != null) {
                boolean packetSniffDirectionIsIngress = Arrays.equals(this.interfaceMacAddress, eth.destination());
                if ((this.sniffDirection == TrafficDirection.Ingress && packetSniffDirectionIsIngress)
                        || (this.sniffDirection == TrafficDirection.Egress && !packetSniffDirectionIsIngress)
                        || this.sniffDirection == TrafficDirection.None) {
                    boolean allow = this.allowFiltered;
                    if (this.accessFilter != null) {
                        allow = !(this.accessFilter.matches(input) ^ allow);
                    }
                    if (allow) {
                        return new PduAtomic(input, this.packetDirectionFlag);
                    }
                }
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
