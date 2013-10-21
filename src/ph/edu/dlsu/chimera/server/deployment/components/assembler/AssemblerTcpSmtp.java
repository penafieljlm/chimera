/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.assembler;

import org.jnetpcap.protocol.tcpip.Tcp;
import ph.edu.dlsu.chimera.server.core.Connection;
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
    private StringBuilder messageBuilder;
    private boolean dataOpen;
    private boolean expectingCmd;

    public AssemblerTcpSmtp() {
        this(-1, null);
    }

    public AssemblerTcpSmtp(long timeCreatedNanos, Connection connection) {
        super(timeCreatedNanos, connection);
        this.resetSmtp();
    }

    @Override
    protected void appendTCP(Tcp tcp, PduAtomic pkt) {
        String data = new String(tcp.getPayload());
        if (!this.dataOpen && !this.expectingCmd) {
            this.messageBuilder.append(data);
            if (data.endsWith(AssemblerTcpSmtp.TOKEN_DAT_END)) {
                //data segment ends
                this.finishSmtp(pkt.inbound, pkt.timestampInNanos);
            }
        } else {
            StringBuilder tdata = new StringBuilder(data);
            while (tdata.indexOf(AssemblerTcpSmtp.TOKEN_END) >= 0) {
                int msgend = tdata.indexOf(AssemblerTcpSmtp.TOKEN_END) + AssemblerTcpSmtp.TOKEN_END.length();
                String msgstr = tdata.substring(0, msgend);
                tdata = tdata.delete(0, msgend);
                this.messageBuilder = this.messageBuilder.append(msgstr);
                String msg = this.messageBuilder.toString();
                this.finishSmtp(pkt.inbound, pkt.timestampInNanos);
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
    protected AssemblerTcp createTcpAssemblerInstance(Tcp tcp, PduAtomic firstPacket) {
        if (firstPacket.getConnection() != null) {
            return new AssemblerTcpSmtp(firstPacket.packet.getCaptureHeader().timestampInNanos(), firstPacket.getConnection());
        }
        return null;
    }

    private void resetSmtp() {
        this.messageBuilder = new StringBuilder();
        this.dataOpen = false;
        this.expectingCmd = false;
    }

    private void finishSmtp(boolean inbound, long timestampInNanos) {
        PduCompositeTcpSmtp http = new PduCompositeTcpSmtp(super.connection,
                this,
                this.messageBuilder.toString(),
                !this.dataOpen,
                inbound,
                timestampInNanos);
        super.outputPDU(http);
        this.resetSmtp();
    }

    @Override
    public boolean isAttackDetected() {
        //TODO : implement
        return false;
    }
}
