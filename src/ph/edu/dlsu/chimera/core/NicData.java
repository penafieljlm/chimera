/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class NicData implements Serializable {
    
    public final String name;
    public final String description;
    public final String hardwareAddress;
    public final ArrayList<NicDataAddress> addresses;

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
        this.addresses = new ArrayList<>();
        for (int i = 0; i < pcapif.getAddresses().size(); i++) {
            this.addresses.add(new NicDataAddress(pcapif.getAddresses().get(i)));
        }
    }
}
