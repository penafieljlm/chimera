package ph.edu.dlsu.chimera.core.nic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jnetpcap.PcapIf;

/**
 * An instance of this class constitutes an object containing information
 * regarding a Network Interface Card. Allows information inside a PcapIf object
 * to be serializable.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class NicData implements Serializable {

    /**
     * The name of the NIC
     */
    public final String name;
    /**
     * The description of the NIC
     */
    public final String description;
    /**
     * The MAC address of the NIC
     */
    public final String hardwareAddress;
    /**
     * The list of addresses of the NIC
     */
    public final ArrayList<NicDataAddress> addresses;

    /**
     * Constructs a new NicData object.
     *
     * @param pcapif A PcapIf object from which to extract information from
     */
    public NicData(PcapIf pcapif) {
        this.name = pcapif.getName();
        this.description = pcapif.getDescription();
        StringBuilder sb = new StringBuilder();
        try {
            if (pcapif.getHardwareAddress() != null) {
                for (byte b : pcapif.getHardwareAddress()) {
                    if (sb.length() > 0) {
                        sb.append(':');
                    }
                    sb.append(String.format("%02x", b));
                }
            } else {
                sb = new StringBuilder("N/A");
            }
        } catch (Exception ex) {
            sb = new StringBuilder("N/A");
            Logger.getLogger(NicData.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.hardwareAddress = sb.toString();
        this.addresses = new ArrayList<NicDataAddress>();
        for (int i = 0; i < pcapif.getAddresses().size(); i++) {
            this.addresses.add(new NicDataAddress(pcapif.getAddresses().get(i)));
        }
    }
}
