/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.assembler;

import net.sourceforge.jpcap.net.Packet;
import net.sourceforge.jpcap.net.TCPPacket;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class AssemblerTCP extends Assembler {

    private TCPPacket[] packets;
    private int expectedSeqNo;

    public AssemblerTCP() {
    }

    @Override
    public final boolean assemblePDU(Packet segment) {
        if (segment instanceof TCPPacket) {
            //declare tcp packet
            TCPPacket tcp = (TCPPacket) segment;
            //if not done
            if (!this.isDone()) {
                //if not encountered
                if (!this.encounteredTCPPacket(tcp)) {
                    if (tcp.getTCPData().length > 0) {
                        //append packet
                        TCPPacket[] append = new TCPPacket[1];
                        append[0] = tcp;
                        TCPPacket[] nPackets = new TCPPacket[this.packets.length + 1];
                        System.arraycopy(this.packets, 0, nPackets, 0, this.packets.length);
                        System.arraycopy(append, 0, nPackets, this.packets.length, append.length);
                        this.packets = nPackets;
                        //while expected seq no is present, build tcp
                    }
                }
            }
        }
        return false;
    }

    private boolean encounteredTCPPacket(TCPPacket tcp) {
        for (TCPPacket pkt : this.packets) {
            if (pkt.getAcknowledgementNumber() == tcp.getAcknowledgementNumber()
                    && pkt.getSequenceNumber() == tcp.getSequenceNumber()) {
                return true;
            }
        }
        return false;
    }

    protected abstract boolean assembleTCP(TCPPacket tcp);

    @Override
    protected void reset() {
        this.packets = new TCPPacket[0];
        this.expectedSeqNo = 1;
    }
}
