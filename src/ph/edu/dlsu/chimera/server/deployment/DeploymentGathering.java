/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment;

import com.gremwell.jnetbridge.PcapPort;
import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import org.jnetpcap.PcapIf;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.core.Connection;
import ph.edu.dlsu.chimera.server.core.CriteriaInstance;
import ph.edu.dlsu.chimera.server.core.SocketPair;
import ph.edu.dlsu.chimera.server.core.Statistics;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentInjector;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentInstanceDumper;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentInstancePreprocessor;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentSniffer;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentStateTable;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentStateTracker;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentStatisticsTable;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentStatisticsTracker;
import ph.edu.dlsu.chimera.server.deployment.components.data.IntermodulePipe;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class DeploymentGathering extends Deployment {

    public final PcapPort externalPcapPort;
    public final PcapPort internalPcapPort;

    public DeploymentGathering(Assembly assembly,
            int ifExternal,
            int ifInternal,
            String dumpFileName,
            boolean gatherAttacks,
            long statsTimeoutMs,
            long stateTimeoutMs) throws Exception {
        //deployment name
        super("Data Gathering");

        //get external pcap interface
        PcapIf exPcapIf = null;
        try {
            exPcapIf = assembly.getInterfaces().get(ifExternal);
        } catch (IndexOutOfBoundsException ex) {
            throw new Exception("Interface Index '" + ifExternal + "' does not exist.");
        }

        //get internal pcap interface
        PcapIf inPcapIf = null;
        try {
            inPcapIf = assembly.getInterfaces().get(ifInternal);
        } catch (IndexOutOfBoundsException ex) {
            throw new Exception("Interface Index '" + ifInternal + "' does not exist.");
        }

        //generic resources
        File trainingDumpFile = new File(dumpFileName);
        StringBuilder inErr = new StringBuilder();
        StringBuilder outErr = new StringBuilder();

        //packet capture objects
        this.externalPcapPort = new PcapPort(exPcapIf.getName());
        this.internalPcapPort = new PcapPort(inPcapIf.getName());

        //inbound queues
        IntermodulePipe<PduAtomic> exSniffOut = new IntermodulePipe<>();
        IntermodulePipe<PduAtomic> exInjectOut = new IntermodulePipe<>();
        IntermodulePipe<PduAtomic> exStatsOut = new IntermodulePipe<>();
        IntermodulePipe<PduAtomic> exStateOut = new IntermodulePipe<>();
        IntermodulePipe<PduAtomic> exPrePrcOut = new IntermodulePipe<>();

        //outbound queues
        IntermodulePipe<PduAtomic> inSniffOut = new IntermodulePipe<>();
        IntermodulePipe<PduAtomic> inInjectOut = new IntermodulePipe<>();

        //shared resources
        ConcurrentHashMap<CriteriaInstance, Statistics> statsTableAtomic = new ConcurrentHashMap<>();
        ConcurrentHashMap<SocketPair, Connection> stateTable = new ConcurrentHashMap<>();

        //daemons
        super.addComponent("stats", new ComponentStatisticsTable(assembly, assembly.getCriterias(), statsTableAtomic, statsTimeoutMs));
        super.addComponent("states", new ComponentStateTable(assembly, stateTable, stateTimeoutMs));

        //inbound path
        //path 1 = sniffer  ->  injector
        //path 2 = sniffer  ->  stats   ->  states  ->  preprc  ->  dumper
//        super.addComponent("ex-sniff", new ComponentSniffer(assembly, this.externalPcapPort, exSniffOut, true));
//        super.addComponent("ex-inject", new ComponentInjector(assembly, exSniffOut, this.internalPcapPort));
//        super.addComponent("ex-sniff", new ComponentSniffer(assembly, this.externalPcapPort, exSniffOut, true));
//        super.addComponent("ex-fork", new ComponentFork(assembly, exSniffOut, exForkOutInject));
//        super.addComponent("ex-inject", new ComponentInjector(assembly, exForkOutInject, this.internalPcapPort));
        super.addComponent("ex-sniff", new ComponentSniffer(assembly, this.externalPcapPort, exSniffOut, true));
        super.addComponent("ex-inject", new ComponentInjector(assembly, exSniffOut, exInjectOut, this.internalPcapPort));
        super.addComponent("ex-stats", new ComponentStatisticsTracker(assembly, exInjectOut, exStatsOut, assembly.getCriterias(), statsTableAtomic));
        super.addComponent("ex-states", new ComponentStateTracker(assembly, exStatsOut, exStateOut, stateTable));
        super.addComponent("ex-preprc", new ComponentInstancePreprocessor(assembly, exStateOut, null, assembly.getCriterias(), gatherAttacks));
//        super.addComponent("ex-dumper", new ComponentInstanceDumper(assembly, exPrePrcOut, assembly.getCriterias(), trainingDumpFile));

        //outbound path
//        super.addComponent("in-sniff", new ComponentSniffer(assembly, this.internalPcapPort, inSniffOut, false));
//        super.addComponent("in-inject", new ComponentInjector(assembly, inSniffOut, this.externalPcapPort));
//        super.addComponent("in-sniff", new ComponentSniffer(assembly, this.internalPcapPort, inSniffOut, false));
//        super.addComponent("in-fork", new ComponentFork(assembly, inSniffOut, inForkOutInject));
//        super.addComponent("in-inject", new ComponentInjector(assembly, inForkOutInject, this.externalPcapPort));
        super.addComponent("in-sniff", new ComponentSniffer(assembly, this.internalPcapPort, inSniffOut, false));
        super.addComponent("in-inject", new ComponentInjector(assembly, inSniffOut, inInjectOut, this.externalPcapPort));
        super.addComponent("in-states", new ComponentStateTracker(assembly, inInjectOut, null, stateTable));
    }

    @Override
    public void startDeployment() {
        super.startDeployment();
        this.externalPcapPort.start();
        this.internalPcapPort.start();
    }

    @Override
    public synchronized void killDeployment() {
        super.killDeployment();
        this.externalPcapPort.stop();
        this.internalPcapPort.stop();
    }

}
