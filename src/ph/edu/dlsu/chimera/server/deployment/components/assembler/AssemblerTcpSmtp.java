/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.assembler;

import java.util.concurrent.ConcurrentLinkedQueue;
import org.jnetpcap.protocol.tcpip.Tcp;
import ph.edu.dlsu.chimera.server.deployment.components.data.Connection;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomicTcp;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduCompositeTcpSmtp;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class AssemblerTcpSmtp extends AssemblerTcp {

    public static String TOKEN_END = "\r\n";
    public static String TOKEN_CMD_DATA = "DATA";
    public static String TOKEN_DAT_END = AssemblerTcpSmtp.TOKEN_END + "." + AssemblerTcpSmtp.TOKEN_END;
    private ConcurrentLinkedQueue<PduAtomic> smtpPackets;
    private StringBuilder messageBuilder;
    private boolean dataOpen;
    private boolean expectingCmd;

    public AssemblerTcpSmtp() {
        this(null);
    }

    public AssemblerTcpSmtp(Connection connection) {
        super(connection);
        this.resetSmtp();
    }

    @Override
    protected void appendTCP(Tcp tcp, PduAtomic pkt) {
        this.smtpPackets.add(pkt);
        String data = new String(tcp.getPayload());
        if (!this.dataOpen && !this.expectingCmd) {
            this.messageBuilder.append(data);
            if (data.endsWith(AssemblerTcpSmtp.TOKEN_DAT_END)) {
                //data segment ends
                this.finishSmtp(pkt.inbound);
            }
        } else {
            StringBuilder tdata = new StringBuilder(data);
            while (tdata.indexOf(AssemblerTcpSmtp.TOKEN_END) >= 0) {
                int msgend = tdata.indexOf(AssemblerTcpSmtp.TOKEN_END) + AssemblerTcpSmtp.TOKEN_END.length();
                String msgstr = tdata.substring(0, msgend);
                tdata = tdata.delete(0, msgend);
                this.messageBuilder = this.messageBuilder.append(msgstr);
                String msg = this.messageBuilder.toString();
                this.finishSmtp(pkt.inbound);
                if (msg.toUpperCase().startsWith(AssemblerTcpSmtp.TOKEN_CMD_DATA)) {
                    //data clause received
                    this.dataOpen = true;
                }
            }
            if (tdata.length() > 0) {
                this.messageBuilder = this.messageBuilder.append(tdata.toString());
                this.expectingCmd = true;
            }
        }
    }

    @Override
    public Assembler createAssemblerInstance(PduAtomic firstPacket) {
        if (firstPacket instanceof PduAtomicTcp) {
            PduAtomicTcp pkttcp = (PduAtomicTcp) firstPacket;
            return new AssemblerTcpSmtp(pkttcp.connection);
        }
        return null;
    }

    private void resetSmtp() {
        this.smtpPackets = new ConcurrentLinkedQueue<PduAtomic>();
        this.messageBuilder = new StringBuilder();
        this.dataOpen = false;
        this.expectingCmd = false;
    }

    private void finishSmtp(boolean inbound) {
        PduCompositeTcpSmtp http = new PduCompositeTcpSmtp(this.smtpPackets,
                super.connection,
                this.messageBuilder.toString(),
                !this.dataOpen,
                inbound);
        super.outputPDU(http);
        this.resetSmtp();
    }
}
