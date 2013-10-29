/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentAssembler;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentSniffer;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentStateTable;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentStateTracker;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentStatisticsTable;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentStatisticsTracker;
import ph.edu.dlsu.chimera.server.core.SocketPair;
import ph.edu.dlsu.chimera.server.core.Connection;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PDUAtomic;
import ph.edu.dlsu.chimera.server.deployment.components.assembler.AssemblerTcp;
import ph.edu.dlsu.chimera.server.deployment.components.assembler.AssemblerTcpHttp;
import ph.edu.dlsu.chimera.server.core.CriteriaInstance;
import ph.edu.dlsu.chimera.server.core.Statistics;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class DeploymentDebug extends Deployment {

    public DeploymentDebug(Assembly assembly,
            int ifExternal,
            long statsTimeoutMs,
            long stateTimeoutMs) throws Exception {
        super("Debug");
        StringBuilder err = new StringBuilder();
        PcapIf inPcapIf = null;
        try {
            inPcapIf = assembly.getInterfaces().get(ifExternal);
        } catch (IndexOutOfBoundsException ex) {
            throw new Exception("Interface Index '" + ifExternal + "' does not exist.");
        }
        Pcap inPcap = Pcap.openLive(inPcapIf.getName(), Pcap.DEFAULT_SNAPLEN, Pcap.MODE_PROMISCUOUS, Pcap.DEFAULT_TIMEOUT, err);
        ConcurrentLinkedQueue<PDUAtomic> sniffOut = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<PDUAtomic> statsOut = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<PDUAtomic> stateOut = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<PDUAtomic> assemblerOut = new ConcurrentLinkedQueue<>();

        ConcurrentHashMap<CriteriaInstance, Statistics> statsTableAtomic = new ConcurrentHashMap<>();
        ConcurrentHashMap<SocketPair, Connection> stateTable = new ConcurrentHashMap<>();

        ConcurrentHashMap<Integer, AssemblerTcp> tcpPortProtocolLookup = new ConcurrentHashMap<>();
        tcpPortProtocolLookup.put(80, new AssemblerTcpHttp());

        super.components.put("stats", new ComponentStatisticsTable(assembly, assembly.getCriterias(), statsTableAtomic, statsTimeoutMs));
        super.components.put("states", new ComponentStateTable(assembly, stateTable, stateTimeoutMs));

        super.components.put("in-sniff", new ComponentSniffer(assembly, inPcap, sniffOut, true));
        super.components.put("in-stats", new ComponentStatisticsTracker(assembly, sniffOut, statsOut, assembly.getCriterias(), statsTableAtomic));
        super.components.put("in-states", new ComponentStateTracker(assembly, statsOut, stateOut, stateTable));
        super.components.put("in-assembler", new ComponentAssembler(assembly, stateOut, assemblerOut, tcpPortProtocolLookup, null, stateTable));
        //super.components.put("in-debugger", new ComponentDebugger<PduComposite>(assembly, assemblerCompositeOut, null));
    }
}
