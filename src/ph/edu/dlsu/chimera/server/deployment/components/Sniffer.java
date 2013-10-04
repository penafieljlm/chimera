/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jnetpcap.Pcap;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.ComponentActive;
import ph.edu.dlsu.chimera.core.Diagnostic;

/**
 * 
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class Sniffer extends ComponentActive implements PcapPacketHandler<String> {

    public final ConcurrentLinkedQueue<PcapPacket> outQueue;
    public final Pcap inPcap;
    public int received;

    public Sniffer(Assembly assembly, Pcap inPcap, ConcurrentLinkedQueue<PcapPacket> outQueue) {
        super(assembly);
        this.outQueue = outQueue;
        this.received = 0;
        this.inPcap = inPcap;
    }

    @Override
    public void componentRun() {
        try {
            switch (this.inPcap.loop(-1, this, "")) {
                case 0:
                    //count exhausted
                    break;
                case -1:
                    //error
                    break;
                case -2:
                    //break loop called
                    break;
            }
            this.inPcap.close();
        } catch (Exception ex) {
            Logger.getLogger(Sniffer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void nextPacket(PcapPacket pp, String t) {
        this.received++;
        if (this.outQueue != null) {
            this.outQueue.add(pp);
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
