/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.util;

import java.util.ArrayList;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class UtilsPcap {

    public static ArrayList<PcapIf> getInterfaces() throws Exception {
        StringBuilder pcapStrBldr = new StringBuilder();
        ArrayList<PcapIf> interfaces = new ArrayList<PcapIf>();
        int result = Pcap.findAllDevs(interfaces, pcapStrBldr);
        if (result != 0 || interfaces == null) {
            throw new Exception("No network interfaces detected.");
        }
        return interfaces;
    }
}
