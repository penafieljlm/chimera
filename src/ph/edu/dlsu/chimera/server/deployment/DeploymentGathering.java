/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment;

import com.gremwell.jnetbridge.PcapPort;
import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.core.Connection;
import ph.edu.dlsu.chimera.server.core.CriteriaInstance;
import ph.edu.dlsu.chimera.server.core.SocketPair;
import ph.edu.dlsu.chimera.server.core.Statistics;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentBridge;
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

    public final PcapPort externalGatherPcapPort;
    public final PcapPort externalBridgePcapPort;
    public final PcapPort internalGatherPcapPort;
    public final PcapPort internalBridgePcapPort;

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
        String ifExternalName = assembly.getConfig().ifExternal;
        if (ifExternal >= 0) {
            try {
                ifExternalName = assembly.getInterfaces().get(ifExternal).getName();
            } catch (IndexOutOfBoundsException ex) {
                throw new Exception("Interface Index '" + ifExternal + "' does not exist.");
            }
        }

        //get internal pcap interface
        String ifInternalName = assembly.getConfig().ifInternal;
        if (ifInternal >= 0) {
            try {
                ifInternalName = assembly.getInterfaces().get(ifInternal).getName();
            } catch (IndexOutOfBoundsException ex) {
                throw new Exception("Interface Index '" + ifInternal + "' does not exist.");
            }
        }

        //generic resources
        File trainingDumpFile = new File(dumpFileName);
        StringBuilder inErr = new StringBuilder();
        StringBuilder outErr = new StringBuilder();

        try {
            this.externalGatherPcapPort = new PcapPort(ifExternalName);
            this.externalBridgePcapPort = new PcapPort(ifExternalName);
        } catch (Exception ex) {
            throw new Exception("External interface cannot be opened.");
        }
        try {
            this.internalGatherPcapPort = new PcapPort(ifInternalName);
            this.internalBridgePcapPort = new PcapPort(ifInternalName);
        } catch (Exception ex) {
            throw new Exception("Internal interface cannot be opened.");
        }

        //inbound queues
        IntermodulePipe<PduAtomic> exGatherSniffOut = new IntermodulePipe<>();
        IntermodulePipe<PduAtomic> exGatherStatsOut = new IntermodulePipe<>();
        IntermodulePipe<PduAtomic> exGatherPrePrcOut = new IntermodulePipe<>();
        IntermodulePipe<PduAtomic> exGatherStateOut = new IntermodulePipe<>();

        //outbound queues
        IntermodulePipe<PduAtomic> inGatherSniffOut = new IntermodulePipe<>();

        //shared resources
        ConcurrentHashMap<CriteriaInstance, Statistics> statsTableAtomic = new ConcurrentHashMap<>();
        ConcurrentHashMap<SocketPair, Connection> stateTable = new ConcurrentHashMap<>();

        //daemons
        super.addComponent("stats", new ComponentStatisticsTable(assembly, assembly.getCriterias(), statsTableAtomic, statsTimeoutMs));
        super.addComponent("states", new ComponentStateTable(assembly, stateTable, stateTimeoutMs));

        //inbound path
        //path 1 = sniffer  ->  injector
        //path 2 = sniffer  ->  stats   ->  states  ->  preprc  ->  dumper
        super.addComponent("ex.gather.sniff", new ComponentSniffer(assembly, this.externalGatherPcapPort, exGatherSniffOut, true));
        super.addComponent("ex.gather.stats", new ComponentStatisticsTracker(assembly, exGatherSniffOut, exGatherStatsOut, assembly.getCriterias(), statsTableAtomic));
        super.addComponent("ex.gather.states", new ComponentStateTracker(assembly, exGatherStatsOut, exGatherStateOut, stateTable));
        super.addComponent("ex.gather.preprc", new ComponentInstancePreprocessor(assembly, exGatherStateOut, exGatherPrePrcOut, assembly.getCriterias(), gatherAttacks));
        super.addComponent("ex.gather.dumper", new ComponentInstanceDumper(assembly, exGatherPrePrcOut, assembly.getCriterias(), trainingDumpFile));

        super.addComponent("ex.bridge", new ComponentBridge(assembly, this.externalBridgePcapPort, this.internalBridgePcapPort));

        //outbound path
        super.addComponent("in.gather.sniff", new ComponentSniffer(assembly, this.internalGatherPcapPort, inGatherSniffOut, false));
        super.addComponent("in.gather.states", new ComponentStateTracker(assembly, inGatherSniffOut, null, stateTable));

        super.addComponent("in.bridge", new ComponentBridge(assembly, this.internalBridgePcapPort, this.externalBridgePcapPort));
    }

    @Override
    public void startDeployment() {
        super.startDeployment();
        this.externalGatherPcapPort.start();
        this.externalBridgePcapPort.start();
        this.internalGatherPcapPort.start();
        this.internalBridgePcapPort.start();
    }

    @Override
    public synchronized void killDeployment() {
        super.killDeployment();
        this.externalGatherPcapPort.stop();
        this.externalBridgePcapPort.stop();
        this.internalGatherPcapPort.stop();
        this.internalBridgePcapPort.stop();
    }
}
