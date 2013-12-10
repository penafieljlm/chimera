/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.core.nic;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jnetpcap.PcapAddr;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class NicDataAddress implements Serializable {

    public final String address;
    public final String netmask;

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
