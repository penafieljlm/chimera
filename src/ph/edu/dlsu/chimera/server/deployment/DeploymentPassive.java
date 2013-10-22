/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentInjector;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentSniffer;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class DeploymentPassive extends Deployment {

    public DeploymentPassive(Assembly assembly) {
        super("Passive");
        List<PcapIf> interfaces = assembly.getInterfaces();
        for (int i = 0; i < interfaces.size(); i++) {
            PcapIf srcIface = interfaces.get(i);
            for (int o = 0; o < interfaces.size(); o++) {
                PcapIf dstIface = interfaces.get(o);
                if (srcIface != dstIface) {
                    StringBuilder inErr = new StringBuilder();
                    StringBuilder outErr = new StringBuilder();
                    Pcap inPcap = Pcap.openLive(srcIface.getName(), Pcap.DEFAULT_SNAPLEN, Pcap.MODE_PROMISCUOUS, Pcap.DEFAULT_TIMEOUT, inErr);
                    Pcap outPcap = Pcap.openLive(dstIface.getName(), Pcap.DEFAULT_SNAPLEN, Pcap.MODE_PROMISCUOUS, Pcap.DEFAULT_TIMEOUT, outErr);
                    ConcurrentLinkedQueue<PduAtomic> snifferOut = new ConcurrentLinkedQueue<>();
                    super.components.put("sniffer[" + i + "][" + o + "]", new ComponentSniffer(assembly, inPcap, snifferOut, true));
                    super.components.put("injector[" + i + "][" + o + "]", new ComponentInjector(assembly, snifferOut, outPcap));
                }
            }
        }
    }
}
