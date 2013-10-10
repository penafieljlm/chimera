/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.handler;

import java.util.concurrent.ConcurrentLinkedQueue;
import org.jnetpcap.protocol.tcpip.Tcp;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;
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
                this.finishSmtp();
            }
        } else {
            StringBuilder tdata = new StringBuilder(data);
            while (tdata.indexOf(AssemblerTcpSmtp.TOKEN_END) >= 0) {
                int msgend = tdata.indexOf(AssemblerTcpSmtp.TOKEN_END) + AssemblerTcpSmtp.TOKEN_END.length();
                String msgstr = tdata.substring(0, msgend);
                tdata = tdata.delete(0, msgend);
                this.messageBuilder = this.messageBuilder.append(msgstr);
                String msg = this.messageBuilder.toString();
                this.finishSmtp();
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
    public Assembler copyAssemblerType() {
        return new AssemblerTcpSmtp();
    }

    private void resetSmtp() {
        this.smtpPackets = new ConcurrentLinkedQueue<PduAtomic>();
        this.messageBuilder = new StringBuilder();
        this.dataOpen = false;
        this.expectingCmd = false;
    }

    private void finishSmtp() {
        PduCompositeTcpSmtp http = new PduCompositeTcpSmtp(this.smtpPackets, super.connectionData, this.messageBuilder.toString(), !this.dataOpen);
        super.outputPDU(http);
        this.resetSmtp();
    }
}
