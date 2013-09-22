/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server;

import java.util.ArrayList;
import java.util.HashMap;
import net.sourceforge.jpcap.net.IPPacket;
import net.sourceforge.jpcap.net.Packet;
import net.sourceforge.jpcap.net.TCPPacket;
import net.sourceforge.jpcap.net.UDPPacket;
import ph.edu.dlsu.chimera.server.deployment.components.data.net.DNSPacket;
import ph.edu.dlsu.chimera.server.deployment.components.data.net.FTPControlPacket;
import ph.edu.dlsu.chimera.server.deployment.components.data.net.FTPTCPDataPacket;
import ph.edu.dlsu.chimera.server.deployment.components.data.net.FTPUDPDataPacket;
import ph.edu.dlsu.chimera.server.deployment.components.data.net.HTTPPacket;
import ph.edu.dlsu.chimera.server.deployment.components.data.net.SMTPPacket;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class PortProtocolMap {

    private final ArrayList<PortProtocolMapping> map;

    public PortProtocolMap() {
        this.map = new ArrayList<PortProtocolMapping>();
        this.map.add(new PortProtocolMapping<UDPPacket, FTPUDPDataPacket>(20, "FTP.TCP.Data", UDPPacket.class, FTPUDPDataPacket.class));
        this.map.add(new PortProtocolMapping<TCPPacket, FTPTCPDataPacket>(20, "FTP.UDP.Data", TCPPacket.class, FTPTCPDataPacket.class));
        this.map.add(new PortProtocolMapping<TCPPacket, FTPControlPacket>(21, "FTP.Control", TCPPacket.class, FTPControlPacket.class));
        this.map.add(new PortProtocolMapping<TCPPacket, SMTPPacket>(25, "SMTP", TCPPacket.class, SMTPPacket.class));
        this.map.add(new PortProtocolMapping<UDPPacket, DNSPacket>(53, "DNS", UDPPacket.class, DNSPacket.class));
        this.map.add(new PortProtocolMapping<TCPPacket, HTTPPacket>(80, "HTTP", TCPPacket.class, HTTPPacket.class));
    }

    public <TL4 extends IPPacket> Class getProtocolForPort(Class<TL4> layer4, int port) {
        for(PortProtocolMapping m : this.map) {
            if(m.layer4.equals(layer4)) {
                if(m.port == port) {
                    return m.layer7;
                }
            }
        }
        return null;
    }

    public <TL4 extends IPPacket> Class getProtocolForName(Class<TL4> layer4, String name) {
        for(PortProtocolMapping m : this.map) {
            if(m.layer4.equals(layer4)) {
                if(m.name.equals(name)) {
                    return m.layer7;
                }
            }
        }
        return null;
    }


}
