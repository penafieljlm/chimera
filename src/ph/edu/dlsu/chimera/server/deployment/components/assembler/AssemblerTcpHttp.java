/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.assembler;

import org.jnetpcap.protocol.tcpip.Tcp;
import ph.edu.dlsu.chimera.server.core.Connection;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduCompositeTcpHttp;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class AssemblerTcpHttp extends AssemblerTcp {

    public static String TOKEN_ATTR_CONTENT_LEN = "content-length:";
    public static String TOKEN_ATTR_VALUE_KEEP_ALIVE = "connection: keep-alive";
    public static String TOKEN_DIV = "\r\n";
    public static String TOKEN_HEADER_END = AssemblerTcpHttp.TOKEN_DIV + AssemblerTcpHttp.TOKEN_DIV;
    private StringBuilder headerBuilder;
    private StringBuilder bodyBuilder;
    private boolean headerOk;
    private boolean keepAlive;
    private int bodyLength;

    public AssemblerTcpHttp() {
        this(-1, null);
    }

    public AssemblerTcpHttp(long timeCreatedNanos, Connection connection) {
        super(timeCreatedNanos, connection);
        this.resetHttp();
    }

    @Override
    protected void appendTCP(Tcp tcp, PduAtomic pkt) {
        String data = new String(tcp.getPayload());
        if (!this.headerOk) {
            //build header
            if (data.contains(AssemblerTcpHttp.TOKEN_HEADER_END)) {
                //end header and append body
                this.headerOk = true;
                int dataStart = data.indexOf(AssemblerTcpHttp.TOKEN_HEADER_END) + AssemblerTcpHttp.TOKEN_HEADER_END.length();
                String header = data.substring(0, dataStart);
                String body = data.substring(dataStart);
                this.headerBuilder.append(header);
                //determine content length
                String okHeader = this.headerBuilder.toString().toLowerCase();
                if (okHeader.contains(AssemblerTcpHttp.TOKEN_ATTR_VALUE_KEEP_ALIVE)) {
                    //is keep alive
                    this.keepAlive = true;
                    this.bodyLength = 0;
                    if (okHeader.contains(AssemblerTcpHttp.TOKEN_ATTR_CONTENT_LEN)) {
                        int lenStart = okHeader.indexOf(AssemblerTcpHttp.TOKEN_ATTR_CONTENT_LEN) + AssemblerTcpHttp.TOKEN_HEADER_END.length();
                        int lenEnd = okHeader.indexOf(AssemblerTcpHttp.TOKEN_DIV, lenStart);
                        String contentLenAttr = okHeader.substring(lenStart, lenEnd);
                        contentLenAttr = contentLenAttr.trim();
                        try {
                            this.bodyLength = Integer.valueOf(contentLenAttr);
                        } catch (NumberFormatException ex) {
                        }
                    }
                    if (this.bodyLength != 0) {
                        this.bodyBuilder.append(body);
                    }
                    if (this.bodyLength == 0) {
                        this.finishHttp(pkt.inbound, pkt.timestampInNanos);
                    }
                }
            } else {
                //continue header
                this.headerBuilder.append(data);
            }
        } else {
            //build body
            this.bodyBuilder.append(data);
            if (this.keepAlive) {
                //wait until body length reached
                if (this.bodyBuilder.toString().length() >= this.bodyLength) {
                    this.finishHttp(pkt.inbound, pkt.timestampInNanos);
                }
            } else {
                //wait until fin flag
                if (tcp.flags_FIN()) {
                    this.finishHttp(pkt.inbound, pkt.timestampInNanos);
                }
            }
        }
    }

    @Override
    protected AssemblerTcp createTcpAssemblerInstance(Tcp tcp, PduAtomic firstPacket) {
        if (firstPacket.getConnection() != null) {
            return new AssemblerTcpHttp(firstPacket.packet.getCaptureHeader().timestampInNanos(), firstPacket.getConnection());
        }
        return null;
    }

    private void resetHttp() {
        this.headerBuilder = new StringBuilder();
        this.bodyBuilder = new StringBuilder();
        this.headerOk = false;
        this.keepAlive = false;
        this.bodyLength = -1;
    }

    private void finishHttp(boolean inbound, long timestampInNanos) {
        PduCompositeTcpHttp http = new PduCompositeTcpHttp(super.connection,
                this,
                this.headerBuilder.toString(),
                this.bodyBuilder.toString(),
                inbound,
                timestampInNanos);
        super.outputPDU(http);
        this.resetHttp();
    }

    @Override
    public boolean isAttackDetected() {
        //TODO : implement
        return false;
    }
}
