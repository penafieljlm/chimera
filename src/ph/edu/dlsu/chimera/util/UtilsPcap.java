package ph.edu.dlsu.chimera.util;

import java.util.ArrayList;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

/**
 * This utility class provide functions dealing with summarizing JNetPcap
 * functions.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class UtilsPcap {

    /**
     *
     * @return The interfaces of the local machine
     * @throws Exception
     */
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
