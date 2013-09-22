/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.sourceforge.jpcap.net.ARPPacket;
import net.sourceforge.jpcap.net.EthernetPacket;
import net.sourceforge.jpcap.net.ICMPPacket;
import net.sourceforge.jpcap.net.IGMPPacket;
import net.sourceforge.jpcap.net.IPPacket;
import net.sourceforge.jpcap.net.Packet;
import net.sourceforge.jpcap.net.TCPPacket;
import net.sourceforge.jpcap.net.UDPPacket;
import net.sourceforge.jpcap.util.Timeval;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.Component;
import ph.edu.dlsu.chimera.server.deployment.components.data.net.AppPacketEncoding;
import ph.edu.dlsu.chimera.server.deployment.components.data.net.DNSPacket;
import ph.edu.dlsu.chimera.server.deployment.components.data.net.FTPControlPacket;
import ph.edu.dlsu.chimera.server.deployment.components.data.net.FTPTCPDataPacket;
import ph.edu.dlsu.chimera.server.deployment.components.data.net.FTPUDPDataPacket;
import ph.edu.dlsu.chimera.server.deployment.components.data.net.HTTPPacket;
import ph.edu.dlsu.chimera.server.deployment.components.data.net.SMTPPacket;
import ph.edu.dlsu.chimera.server.deployment.components.data.packet.PacketGeneric;
import ph.edu.dlsu.chimera.server.deployment.components.data.packet.PacketL2Ethernet;
import ph.edu.dlsu.chimera.server.deployment.components.data.packet.PacketL3ARP;
import ph.edu.dlsu.chimera.server.deployment.components.data.packet.PacketL3IPL4ICMP;
import ph.edu.dlsu.chimera.server.deployment.components.data.packet.PacketL3IPL4IGMP;
import ph.edu.dlsu.chimera.server.deployment.components.data.packet.PacketL3IP;
import ph.edu.dlsu.chimera.server.deployment.components.data.packet.PacketL3IPL4TCP;
import ph.edu.dlsu.chimera.server.deployment.components.data.packet.PacketL3IPL4TCPL7FTPControl;
import ph.edu.dlsu.chimera.server.deployment.components.data.packet.PacketL3IPL4TCPL7FTPData;
import ph.edu.dlsu.chimera.server.deployment.components.data.packet.PacketL3IPL4TCPL7HTTP;
import ph.edu.dlsu.chimera.server.deployment.components.data.packet.PacketL3IPL4TCPL7SMTP;
import ph.edu.dlsu.chimera.server.deployment.components.data.packet.PacketL3IPL4UDP;
import ph.edu.dlsu.chimera.server.deployment.components.data.packet.PacketL3IPL4UDPL7DNS;
import ph.edu.dlsu.chimera.server.deployment.components.data.packet.PacketL3IPL4UDPL7FTPData;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class Sorter extends Component {

    private final ConcurrentLinkedQueue<Packet> inQueue;
    private final ConcurrentLinkedQueue<PacketGeneric> outQueue;

    private long ctr_proc;
    private long ctr_eth;
    private long ctr_etharp;
    private long ctr_ethip;
    private long ctr_ethipicmp;
    private long ctr_ethipigmp;
    private long ctr_ethiptcp;
    private long ctr_ethipudp;
    private long ctr_ethiptcphttp;
    private long ctr_ethiptcpsmtp;
    private long ctr_ethiptcpftpdat;
    private long ctr_ethiptcpftpctrl;
    private long ctr_ethipudpdns;
    private long ctr_ethipudpftpdat;

    public Sorter(Assembly assembly, ConcurrentLinkedQueue<Packet> inQueue, ConcurrentLinkedQueue<PacketGeneric> outQueue) {
        super(assembly);
        this.inQueue = inQueue;
        this.outQueue = outQueue;
        this.ctr_proc = 0;
        this.ctr_eth = 0;
        this.ctr_etharp = 0;
        this.ctr_ethip = 0;
        this.ctr_ethipicmp = 0;
        this.ctr_ethipigmp = 0;
        this.ctr_ethiptcp = 0;
        this.ctr_ethiptcphttp = 0;
        this.ctr_ethiptcpsmtp = 0;
        this.ctr_ethiptcpftpdat = 0;
        this.ctr_ethiptcpftpctrl = 0;
        this.ctr_ethipudp = 0;
        this.ctr_ethipudpdns = 0;
        this.ctr_ethipudpftpdat = 0;
    }

    @Override
    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = super.getDiagnostics();
        diag.add(new Diagnostic("inqueue", "Inbound Queued Packets", this.inQueue.size()));
        diag.add(new Diagnostic("outqueue", "Outbound Queued Packets", this.outQueue.size()));
        diag.add(new Diagnostic("processed", "Processed Packets", this.ctr_proc));
        diag.add(new Diagnostic("eth", "Received Ethernet Packets", this.ctr_eth));
        diag.add(new Diagnostic("eth.arp", "Received ARP Packets", this.ctr_etharp));
        diag.add(new Diagnostic("eth.ip", "Received IP Packets", this.ctr_ethip));
        diag.add(new Diagnostic("eth.ip.icmp", "Received ICMP Packets", this.ctr_ethipicmp));
        diag.add(new Diagnostic("eth.ip.igmp", "Received IGMP Packets", this.ctr_ethipigmp));
        diag.add(new Diagnostic("eth.ip.tcp", "Received TCP Packets", this.ctr_ethiptcp));
        diag.add(new Diagnostic("eth.ip.tcp.http", "Received HTTP Packets", this.ctr_ethiptcphttp));
        diag.add(new Diagnostic("eth.ip.tcp.smtp", "Received SMTP Packets", this.ctr_ethiptcpsmtp));
        diag.add(new Diagnostic("eth.ip.tcp.ftp.dat", "Received FTP Data (TCP) Packets", this.ctr_ethiptcpftpdat));
        diag.add(new Diagnostic("eth.ip.tcp.ftp.ctrl", "Received FTP Control Packets", this.ctr_ethiptcpftpctrl));
        diag.add(new Diagnostic("eth.ip.udp", "Received UDP Packets", this.ctr_ethipudp));
        diag.add(new Diagnostic("eth.ip.udp.dns", "Received DNS Packets", this.ctr_ethipudpdns));
        diag.add(new Diagnostic("eth.ip.udp.ftp.dat", "Received FTP Data (UDP) Packets", this.ctr_ethipudpftpdat));
        return diag;
    }

    @Override
    public void componentRun() {
        while(super.running) {
            while(!this.inQueue.isEmpty()) {
                Packet in = this.inQueue.poll();
                this.outQueue.add(this.filterLayer2(in));
            }
        }
    }

    private PacketGeneric filterLayer2(Packet pkt) {
        this.ctr_proc++;
        if(pkt instanceof EthernetPacket)
            return this.filterLayer3Ethernet((EthernetPacket) pkt);
        return new PacketGeneric(pkt);
    }

    private PacketL2Ethernet filterLayer3Ethernet(EthernetPacket pkt) {
        this.ctr_eth++;
        if(pkt instanceof IPPacket)
            return this.filterLayer4IP((IPPacket) pkt);
        if(pkt instanceof ARPPacket)
            return this.filterLayer4ARP((ARPPacket) pkt);
        return new PacketL2Ethernet(pkt);
    }

    private PacketL3ARP filterLayer4ARP(ARPPacket pkt) {
        this.ctr_etharp++;
        return new PacketL3ARP((ARPPacket) pkt);
    }

    private PacketL3IP filterLayer4IP(IPPacket pkt) {
        this.ctr_ethip++;
        if(pkt instanceof TCPPacket)
            return this.filterLayer7TCP((TCPPacket) pkt);
        if(pkt instanceof UDPPacket)
            return this.filterLayer7UDP((UDPPacket) pkt);
        if(pkt instanceof ICMPPacket)
            return this.filterLayer7ICMP((ICMPPacket) pkt);
        if(pkt instanceof IGMPPacket)
            return this.filterLayer7IGMP((IGMPPacket) pkt);
        return new PacketL3IP(pkt);
    }

    private PacketL3IPL4ICMP filterLayer7ICMP(ICMPPacket pkt) {
        this.ctr_ethipicmp++;
        return new PacketL3IPL4ICMP((ICMPPacket) pkt);
    }

    private PacketL3IPL4IGMP filterLayer7IGMP(IGMPPacket pkt) {
        this.ctr_ethipigmp++;
        return new PacketL3IPL4IGMP((IGMPPacket) pkt);
    }

    private PacketL3IPL4TCP filterLayer7TCP(TCPPacket pkt) {
        this.ctr_ethiptcp++;
        int port = pkt.getDestinationPort();
        Class pkttype = this.assembly.portProtocolMap.getProtocolForPort(TCPPacket.class, port);
        if(pkttype != null) {
            int lLen = pkt.getEthernetHeaderLength();
            byte[] bytes = AppPacketEncoding.toByteArray(pkt);
            Timeval tv = pkt.getTimeval();

            if(pkttype.equals(HTTPPacket.class)) {
                this.ctr_ethiptcphttp++;
                return new PacketL3IPL4TCPL7HTTP(new HTTPPacket(lLen, bytes, tv));
            }
            if(pkttype.equals(SMTPPacket.class)) {
                this.ctr_ethiptcpsmtp++;
                return new PacketL3IPL4TCPL7SMTP(new SMTPPacket(lLen, bytes, tv));
            }
            if(pkttype.equals(FTPTCPDataPacket.class)) {
                this.ctr_ethiptcpftpdat++;
                return new PacketL3IPL4TCPL7FTPData(new FTPTCPDataPacket(lLen, bytes, tv));
            }
            if(pkttype.equals(FTPControlPacket.class)) {
                this.ctr_ethiptcpftpctrl++;
                return new PacketL3IPL4TCPL7FTPControl(new FTPControlPacket(lLen, bytes, tv));
            }
        }
        return new PacketL3IPL4TCP(pkt);
    }

    private PacketL3IPL4UDP filterLayer7UDP(UDPPacket pkt) {
        this.ctr_ethipudp++;
        int port = pkt.getDestinationPort();
        Class pkttype = this.assembly.portProtocolMap.getProtocolForPort(UDPPacket.class, port);
        if(pkttype != null) {
            int lLen = pkt.getEthernetHeaderLength();
            byte[] bytes = AppPacketEncoding.toByteArray(pkt);
            Timeval tv = pkt.getTimeval();

            if(pkttype.equals(DNSPacket.class)) {
                this.ctr_ethipudpdns++;
                return new PacketL3IPL4UDPL7DNS(new DNSPacket(lLen, bytes, tv));
            }
            if(pkttype.equals(FTPUDPDataPacket.class)) {
                this.ctr_ethipudpftpdat++;
                return new PacketL3IPL4UDPL7FTPData(new FTPUDPDataPacket(lLen, bytes, tv));
            }
        }
        return new PacketL3IPL4UDP(pkt);
    }

}
