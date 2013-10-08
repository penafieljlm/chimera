/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.core;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jnetpcap.PcapIf;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class NICData implements Serializable {

    public final String name;
    public final String description;
    public final String hardwareAddress;
    public final ArrayList<NICDataAddress> addresses;

    public NICData(PcapIf pcapif) {
        this.name = pcapif.getName();
        this.description = pcapif.getDescription();
        StringBuilder sb = new StringBuilder();
        try {
            for (byte b : pcapif.getHardwareAddress()) {
                if (sb.length() > 0) {
                    sb.append(':');
                }
                sb.append(String.format("%02x", b));
            }
        } catch (IOException ex) {
            sb = new StringBuilder("N/A");
            Logger.getLogger(NICData.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.hardwareAddress = sb.toString();
        this.addresses = new ArrayList<NICDataAddress>();
        for (int i = 0; i < pcapif.getAddresses().size(); i++) {
            this.addresses.add(new NICDataAddress(pcapif.getAddresses().get(i)));
        }
    }
}
