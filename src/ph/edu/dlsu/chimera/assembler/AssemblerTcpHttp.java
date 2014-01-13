/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.assembler;

import java.util.ArrayList;
import java.util.Date;
import org.jnetpcap.protocol.tcpip.Tcp;
import ph.edu.dlsu.chimera.core.Connection;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.core.TrafficDirection;
import ph.edu.dlsu.chimera.pdu.PduAtomic;
import ph.edu.dlsu.chimera.pdu.PduCompositeTcpHttp;
import ph.edu.dlsu.chimera.util.UtilsTime;

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
    private boolean keepAlive;
    private long bodyLength;
    private long headerOkTimeNs;

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
        if (!this.headerIsAssembled()) {
            //build header
            if (data.contains(AssemblerTcpHttp.TOKEN_HEADER_END)) {
                //end header and append body
                this.headerOkTimeNs = UtilsTime.nowNs();
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
                        this.finishHttp(pkt.direction, pkt.timestampInNanos);
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
                    this.finishHttp(pkt.direction, pkt.timestampInNanos);
                }
            } else {
                //wait until fin flag
                if (tcp.flags_FIN()) {
                    this.finishHttp(pkt.direction, pkt.timestampInNanos);
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
        this.keepAlive = false;
        this.bodyLength = -1;
        this.headerOkTimeNs = -1;
    }

    private void finishHttp(TrafficDirection direction, long timestampInNanos) {
        PduCompositeTcpHttp http = new PduCompositeTcpHttp(super.connection,
                this,
                this.headerBuilder.toString(),
                this.bodyBuilder.toString(),
                direction,
                timestampInNanos);
        super.outputPDU(http);
        this.resetHttp();
    }

    public boolean headerIsAssembled() {
        return this.headerOkTimeNs != -1;
    }

    public long getTimeHeaderIsNotAssembledMs() {
        return (this.headerIsAssembled()) ? -1 : this.getOngoingConstructionTimeMs();
    }

    public long getTimeHeaderIsAssembledMs() {
        return (!this.headerIsAssembled()) ? -1 : UtilsTime.nowMs() - UtilsTime.nsToMs(this.headerOkTimeNs);
    }

    @Override
    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = super.getDiagnostics();
        Date hasmcomplete = (!this.headerIsAssembled()) ? null : new java.sql.Date(UtilsTime.nsToMs(this.headerOkTimeNs));
        diag.add(new Diagnostic("headeroktime", "Header Assembly Completed Timestamp", (!this.headerIsAssembled()) ? "N/A" : hasmcomplete.toLocaleString()));
        if (!this.headerIsAssembled()) {
            diag.add(new Diagnostic("headertimeunasm", "Amount of Time Header Is Still Unassembled", (this.headerIsAssembled()) ? "N/A" : this.getTimeHeaderIsNotAssembledMs() + "ms"));
        } else {
            diag.add(new Diagnostic("headertimeasmed", "Amount of Time Header Has Been Assembled", (!this.headerIsAssembled()) ? "N/A" : this.getTimeHeaderIsAssembledMs() + "ms"));
        }
        return diag;
    }
}
