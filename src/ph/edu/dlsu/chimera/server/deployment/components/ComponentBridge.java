/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment.components;

import org.jnetpcap.Pcap;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.lan.Ethernet;
import ph.edu.dlsu.chimera.server.Assembly;

/**
 * Used by Training Phase and Passive Mode to let live packets pass through.
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ComponentBridge extends ComponentActive implements PcapPacketHandler<Pcap> {

    public final Pcap inPcap;
    public final Pcap outPcap;
    
    public ComponentBridge(Assembly assembly, Pcap inPcap, Pcap outPcap) {
        super(assembly);
        this.setPriority(Thread.MAX_PRIORITY);
        this.inPcap = inPcap;
        this.outPcap = outPcap;
    }
    
    @Override
    protected void componentRun() throws Exception {
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
        this.outPcap.sendPacket(pp.getHeader(new Ethernet()));
    }


}
