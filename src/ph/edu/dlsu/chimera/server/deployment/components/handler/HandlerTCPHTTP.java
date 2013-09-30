/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.handler;

import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.tcpip.Tcp;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PDU;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PDUHTTP;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class HandlerTCPHTTP extends HandlerTCP {

    public static String TOKEN_ATTR_CONTENT_LEN = "content-length:";
    public static String TOKEN_ATTR_VALUE_KEEP_ALIVE = "connection: keep-alive";
    
    public static String TOKEN_DIV = "\r\n";
    public static String TOKEN_HEADER_END = HandlerTCPHTTP.TOKEN_DIV + HandlerTCPHTTP.TOKEN_DIV;

    private StringBuilder headerBuilder;
    private StringBuilder bodyBuilder;
    private boolean headerOk;
    private boolean keepAlive;
    private int bodyLength;
    private boolean done;

    public HandlerTCPHTTP() {
        this.reset();
    }

    @Override
    protected boolean appendTCP(Tcp tcp, PcapPacket pkt) {
        if(super.appendTCP(tcp, pkt))
            return true;
        String data = new String(tcp.getPayload());
        if(!this.headerOk) {
            //build header
            if(data.contains(HandlerTCPHTTP.TOKEN_HEADER_END)) {
                //end header and append body
                this.headerOk = true;
                int dataStart = data.indexOf(HandlerTCPHTTP.TOKEN_HEADER_END) + HandlerTCPHTTP.TOKEN_HEADER_END.length();
                String header = data.substring(0, dataStart);
                String body = data.substring(dataStart);
                this.headerBuilder.append(header);
                this.bodyBuilder.append(body);
                //determine content length
                String okHeader = this.headerBuilder.toString().toLowerCase();
                if(okHeader.contains(HandlerTCPHTTP.TOKEN_ATTR_VALUE_KEEP_ALIVE)) {
                    //is keep alive
                    this.keepAlive = true;
                    this.bodyLength = 0;
                    if(okHeader.contains(HandlerTCPHTTP.TOKEN_ATTR_CONTENT_LEN)) {
                        int lenStart = okHeader.indexOf(HandlerTCPHTTP.TOKEN_ATTR_CONTENT_LEN) + HandlerTCPHTTP.TOKEN_HEADER_END.length();
                        int lenEnd = okHeader.indexOf(HandlerTCPHTTP.TOKEN_DIV, lenStart);
                        String contentLenAttr = okHeader.substring(lenStart, lenEnd);
                        contentLenAttr = contentLenAttr.trim();
                        try {
                            this.bodyLength = Integer.valueOf(contentLenAttr);
                        } catch(NumberFormatException ex) {
                        }
                    }
                    if(this.bodyLength == 0)
                        this.done = true;
                }
            } else {
                //continue header
                this.headerBuilder.append(data);
            }
        } else {
            //build body
            this.bodyBuilder.append(data);
            if(this.keepAlive) {
                //wait until body length reached
                if(this.bodyBuilder.toString().length() >= this.bodyLength)
                    this.done = true;
            } else {
                //wait until fin flag
                if(tcp.flags_FIN())
                    this.done = true;
            }
        }
        return this.done;
    }

    @Override
    public boolean isDone() {
        return this.done;
    }

    @Override
    public ProtocolHandler copyHandlerType() {
        return new HandlerTCPHTTP();
    }

    @Override
    protected PDU producePDU() {
        if(this.isDone()) {
            return new PDUHTTP(super.getMessagePackets(), this.headerBuilder.toString(), this.bodyBuilder.toString());
        }
        return null;
    }

    @Override
    protected void reset() {
        super.reset();
        this.headerBuilder = new StringBuilder();
        this.bodyBuilder = new StringBuilder();
        this.headerOk = false;
        this.keepAlive = false;
        this.bodyLength = -1;
        this.done = false;
    }
    
}
