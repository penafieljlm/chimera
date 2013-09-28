/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.assembler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sourceforge.jpcap.net.TCPPacket;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PDU;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PDUHTTP;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class AssemblerHTTP extends AssemblerTCP {

    private byte[] header;
    private byte[] data;
    private boolean headerDone;
    private int contentLength;
    private boolean keepAlive;
    private boolean done;

    public AssemblerHTTP() {
        this.reset();
    }

    @Override
    public boolean isDone() {
        return this.done;
    }

    @Override
    protected boolean assembleTCP(TCPPacket tcp) {
        byte[] pktdat = tcp.getTCPData();
        if (!this.headerDone) {
            String textData = new String(pktdat);
            if (textData.contains("\r\n\r\n")) {
                //append header if end found
                int hEnd = textData.indexOf("\r\n\r\n");
                byte[] nHeader = new byte[this.header.length + hEnd + 1];
                System.arraycopy(this.header, 0, nHeader, 0, this.header.length);
                System.arraycopy(pktdat, 0, nHeader, this.header.length, hEnd + 1);
                this.header = nHeader;
                //append remaining data
                if (hEnd + 1 < pktdat.length) {
                    byte[] nData = new byte[this.data.length + (pktdat.length - (hEnd + 1))];
                    System.arraycopy(this.data, 0, nData, 0, this.data.length);
                    System.arraycopy(pktdat, hEnd + 1, nData, this.data.length, (pktdat.length - (hEnd + 1)));
                    this.data = nData;
                }
                //determine length and keep alive
                String headerText = new String(this.header);
                if (headerText.toLowerCase().contains("Connection: Keep-Alive\r\n".toLowerCase())) {
                    Pattern p = Pattern.compile("Content-Length: [0-9]+[\r\n]", Pattern.CASE_INSENSITIVE);
                    Matcher m = p.matcher(headerText);
                    if (m.find()) {
                        String contentLenStr = headerText.substring(m.start() + 15, m.end() - 2);
                        contentLenStr = contentLenStr.trim();
                        this.contentLength = Integer.valueOf(contentLenStr);
                    } else {
                        this.contentLength = 0;
                    }
                    this.keepAlive = true;
                }
                //set header done flag
                this.headerDone = true;
                //no content, set done flag
                if (this.contentLength <= this.data.length) {
                    this.done = true;
                }
            } else {
                //append header if end not found
                byte[] nHeader = new byte[this.header.length + pktdat.length];
                System.arraycopy(this.header, 0, nHeader, 0, this.header.length);
                System.arraycopy(pktdat, 0, nHeader, this.header.length, pktdat.length);
                this.header = nHeader;
            }
        } else {
            //append all data
            byte[] nData = new byte[this.data.length + pktdat.length];
            System.arraycopy(this.data, 0, nData, 0, this.data.length);
            System.arraycopy(pktdat, 0, nData, this.data.length, pktdat.length);
            this.data = nData;
            if (!this.keepAlive) {
                if (tcp.isFin()) {
                    //done
                    this.done = true;
                }
            } else {
                if (this.contentLength <= this.data.length) {
                    //done
                    this.done = true;
                }
            }
        }
        return false;
    }

    @Override
    public Assembler duplicate() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void reset() {
        super.reset();
        this.header = new byte[0];
        this.data = new byte[0];
        this.headerDone = false;
        this.keepAlive = false;
        this.contentLength = 0;
        this.done = false;
    }

    @Override
    protected PDU producePDU() {
        return new PDUHTTP(this.header, this.data);
    }
}
