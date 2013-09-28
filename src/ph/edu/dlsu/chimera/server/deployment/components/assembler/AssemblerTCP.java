/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.assembler;

import net.sourceforge.jpcap.net.Packet;
import net.sourceforge.jpcap.net.TCPPacket;
import ph.edu.dlsu.chimera.server.deployment.components.data.TCPPacketSequence;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class AssemblerTCP extends Assembler {

    private TCPPacketSequence packetSequence;

    public AssemblerTCP() {
        //packet seq does not restart over many messages
        this.packetSequence = new TCPPacketSequence();
    }

    @Override
    public boolean appendPDU(Packet segment) {
        if(segment instanceof TCPPacket) {
            TCPPacket tcp = (TCPPacket) segment;
            if(!this.packetSequence.contains(tcp)) {
                this.packetSequence.add(tcp);
            }
        }
        return true;
    }

    @Override
    public boolean assemblePDU() {
        if(!this.isDone()) {
            TCPPacket latest = this.packetSequence.poll();
            while(latest != null) {
                this.appendTCP(latest);
                if(this.isDone())
                    return true;
                latest = this.packetSequence.poll();
            }
            return false;
        }
        return true;
    }

    /**
     * Only called when not done.
     * @param tcp
     * @return
     */
    protected abstract boolean appendTCP(TCPPacket tcp);
    
}
