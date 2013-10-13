/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data.stats.composite;

import java.net.InetAddress;
import ph.edu.dlsu.chimera.server.deployment.components.data.SocketPair;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduComposite;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduCompositeTcpHttp;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class CriteriaCompositeIpTcpSrcHttpGet extends CriteriaComposite {

    public final InetAddress source;
    public final int sourcePort;

    public CriteriaCompositeIpTcpSrcHttpGet() {
        this(null, -1);
    }

    public CriteriaCompositeIpTcpSrcHttpGet(InetAddress source, int sourcePort) {
        super("socktcpsrchttp", "Source TCP Socket Http GET Message");
        this.source = source;
        this.sourcePort = sourcePort;
    }

    @Override
    public CriteriaComposite createInstance(PduComposite pdu) {
        SocketPair socks = pdu.connection.sockets;
        if (socks != null && pdu instanceof PduCompositeTcpHttp) {
            PduCompositeTcpHttp http = (PduCompositeTcpHttp) pdu;
            if (http.header.toUpperCase().startsWith("GET")) {
                return new CriteriaCompositeIpTcpSrcHttpGet(socks.source, socks.sourcePort);
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CriteriaCompositeIpTcpSrcHttpGet other = (CriteriaCompositeIpTcpSrcHttpGet) obj;
        if (this.source != other.source && (this.source == null || !this.source.equals(other.source))) {
            return false;
        }
        if (this.sourcePort != other.sourcePort) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.source != null ? this.source.hashCode() : 0);
        hash = 89 * hash + this.sourcePort;
        return hash;
    }

    @Override
    public String getInstanceString() {
        return this.source.getHostAddress() + ":" + this.sourcePort + " -[IP:TCP:HTTP:GET]> any:any";
    }
}
