/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.handler;

import java.util.concurrent.ConcurrentLinkedQueue;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.tcpip.Tcp;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PDUHTTP;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class AssemblerTCPHTTP extends AssemblerTCP {

    public static String TOKEN_ATTR_CONTENT_LEN = "content-length:";
    public static String TOKEN_ATTR_VALUE_KEEP_ALIVE = "connection: keep-alive";
    public static String TOKEN_DIV = "\r\n";
    public static String TOKEN_HEADER_END = AssemblerTCPHTTP.TOKEN_DIV + AssemblerTCPHTTP.TOKEN_DIV;
    private ConcurrentLinkedQueue<PcapPacket> httpPackets;
    private StringBuilder headerBuilder;
    private StringBuilder bodyBuilder;
    private boolean headerOk;
    private boolean keepAlive;
    private int bodyLength;

    public AssemblerTCPHTTP() {
        this.resetHttp();
    }

    @Override
    protected void appendTCP(Tcp tcp, PcapPacket pkt) {
        this.httpPackets.add(pkt);
        String data = new String(tcp.getPayload());
        if (!this.headerOk) {
            //build header
            if (data.contains(AssemblerTCPHTTP.TOKEN_HEADER_END)) {
                //end header and append body
                this.headerOk = true;
                int dataStart = data.indexOf(AssemblerTCPHTTP.TOKEN_HEADER_END) + AssemblerTCPHTTP.TOKEN_HEADER_END.length();
                String header = data.substring(0, dataStart);
                String body = data.substring(dataStart);
                this.headerBuilder.append(header);
                this.bodyBuilder.append(body);
                //determine content length
                String okHeader = this.headerBuilder.toString().toLowerCase();
                if (okHeader.contains(AssemblerTCPHTTP.TOKEN_ATTR_VALUE_KEEP_ALIVE)) {
                    //is keep alive
                    this.keepAlive = true;
                    this.bodyLength = 0;
                    if (okHeader.contains(AssemblerTCPHTTP.TOKEN_ATTR_CONTENT_LEN)) {
                        int lenStart = okHeader.indexOf(AssemblerTCPHTTP.TOKEN_ATTR_CONTENT_LEN) + AssemblerTCPHTTP.TOKEN_HEADER_END.length();
                        int lenEnd = okHeader.indexOf(AssemblerTCPHTTP.TOKEN_DIV, lenStart);
                        String contentLenAttr = okHeader.substring(lenStart, lenEnd);
                        contentLenAttr = contentLenAttr.trim();
                        try {
                            this.bodyLength = Integer.valueOf(contentLenAttr);
                        } catch (NumberFormatException ex) {
                        }
                    }
                    if (this.bodyLength == 0) {
                        this.finishHttp();
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
                    this.finishHttp();
                }
            } else {
                //wait until fin flag
                if (tcp.flags_FIN()) {
                    this.finishHttp();
                }
            }
        }
    }

    @Override
    public Assembler copyHandlerType() {
        return new AssemblerTCPHTTP();
    }

    private void resetHttp() {
        this.httpPackets = new ConcurrentLinkedQueue<PcapPacket>();
        this.headerBuilder = new StringBuilder();
        this.bodyBuilder = new StringBuilder();
        this.headerOk = false;
        this.keepAlive = false;
        this.bodyLength = -1;
    }

    private void finishHttp() {
        PDUHTTP http = new PDUHTTP(this.httpPackets, this.headerBuilder.toString(), this.bodyBuilder.toString());
        super.outputPDU(http);
        this.resetHttp();
    }
    
}
