/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.deployment;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.jnetpcap.Pcap;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.deployment.components.Component;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentAssembler;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentDebugger;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentSniffer;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentStateTable;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentStateTracker;
import ph.edu.dlsu.chimera.server.deployment.components.data.Connection;
import ph.edu.dlsu.chimera.server.deployment.components.data.ConnectionData;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PDU;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PDUAtomic;
import ph.edu.dlsu.chimera.server.deployment.components.handler.AssemblerTCP;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class DeploymentDebug extends Deployment {

    public DeploymentDebug(Assembly assembly, String ifInbound) {
        super("Debug");
        StringBuilder err = new StringBuilder();
        Pcap inPcap = Pcap.openLive(ifInbound, Pcap.DEFAULT_SNAPLEN, Pcap.MODE_PROMISCUOUS, Pcap.DEFAULT_TIMEOUT, err);
        ConcurrentLinkedQueue<PDUAtomic> snifferOut = new ConcurrentLinkedQueue<PDUAtomic>();
        ConcurrentLinkedQueue<PDUAtomic> stateOut = new ConcurrentLinkedQueue<PDUAtomic>();
        ConcurrentLinkedQueue<PDU> assemblerOut = new ConcurrentLinkedQueue<PDU>();
        ConcurrentHashMap<Connection, ConnectionData> stateTable = new ConcurrentHashMap<Connection, ConnectionData>();
        ConcurrentHashMap<Integer, AssemblerTCP> tcpPortProtocolLookup = new ConcurrentHashMap<Integer, AssemblerTCP>();
        super.components.put("statetable", new ComponentStateTable(stateTable));
        super.components.put("in-sniffer", new ComponentSniffer(assembly, inPcap, snifferOut, true));
        super.components.put("in-statetracker", new ComponentStateTracker(assembly, snifferOut, stateOut, stateTable, true));
        super.components.put("in-assembler", new ComponentAssembler(assembly, stateOut, assemblerOut, tcpPortProtocolLookup, null, stateTable));
        super.components.put("in-debugger", new ComponentDebugger(assembly, assemblerOut, null));
    }

}
