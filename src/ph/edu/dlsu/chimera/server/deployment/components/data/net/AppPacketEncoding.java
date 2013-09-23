package ph.edu.dlsu.chimera.server.deployment.components.data.net;

import net.sourceforge.jpcap.net.EthernetPacket;

/**
 * Contains utility methods for decoding application layer packets.
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class AppPacketEncoding {

    /**
     * Transforms the given Ethernet packet to a byte array.
     * @param eth - the Ethernet packet to transform.
     * @return the byte array form of the given Ethernet packet.
     */
    public static byte[] toByteArray(EthernetPacket eth) {
        byte[] header = eth.getEthernetHeader();
        byte[] data = eth.getEthernetData();
        byte[] bytes = new byte[header.length + data.length];
        System.arraycopy(header, 0, bytes, 0, header.length);
        System.arraycopy(data, 0, bytes, header.length, data.length);
        return bytes;
    }

}
