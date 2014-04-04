package ph.edu.dlsu.chimera.core.nic;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jnetpcap.PcapAddr;

/**
 * An instance of this class constitutes one of the addresses that a Network
 * Interface Card can have. Allows for human readability and serializability of
 * addresses.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class NicDataAddress implements Serializable {

    /**
     * The IP Address / Host Name
     */
    public final String address;
    /**
     * The subnet mask
     */
    public final String netmask;

    /**
     * Constructs a new NicDataAddress object.
     *
     * @param addr The PcapAddr object to extract values from.
     */
    public NicDataAddress(PcapAddr addr) {
        String _address = "N/A";
        String _netmask = "N/A";
        try {
            _address = InetAddress.getByAddress(addr.getAddr().getData()).getHostAddress();
            _netmask = InetAddress.getByAddress(addr.getNetmask().getData()).getHostAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(NicDataAddress.class.getName()).log(Level.FINE, null, ex);
        }
        this.address = _address;
        this.netmask = _netmask;
    }
}
