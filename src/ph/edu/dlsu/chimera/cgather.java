/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import ph.edu.dlsu.chimera.core.Config;
import ph.edu.dlsu.chimera.core.Connection;
import ph.edu.dlsu.chimera.core.Criteria;
import ph.edu.dlsu.chimera.core.CriteriaInstance;
import ph.edu.dlsu.chimera.core.TcpSocketPair;
import ph.edu.dlsu.chimera.core.Statistics;
import ph.edu.dlsu.chimera.reflection.PacketFilter;
import ph.edu.dlsu.chimera.components.Component;
import ph.edu.dlsu.chimera.components.ComponentActive;
import ph.edu.dlsu.chimera.components.ComponentController;
import ph.edu.dlsu.chimera.components.ComponentDumper;
import ph.edu.dlsu.chimera.components.ComponentSniffer;
import ph.edu.dlsu.chimera.components.ComponentStateTable;
import ph.edu.dlsu.chimera.components.ComponentStateTracker;
import ph.edu.dlsu.chimera.components.ComponentStatisticsTable;
import ph.edu.dlsu.chimera.components.ComponentStatisticsTracker;
import ph.edu.dlsu.chimera.core.TrafficDirection;
import ph.edu.dlsu.chimera.core.tools.IntermodulePipe;
import ph.edu.dlsu.chimera.pdu.PduAtomic;
import ph.edu.dlsu.chimera.util.UtilsPcap;
import ph.edu.dlsu.chimera.util.UtilsParse;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class cgather {

    public static final String USAGE = ""
            + "\nCHIMERA Gather Command Usage (cgather)"
            + "\nDescription:"
            + "\n    The cgather command starts the CHIMERA's Data Gathering phase."
            + "\n    The training set used in the Training Phase is compiled in this phase."
            + "\n    This phase produces the said training set using the traffic captured."
            + "\n    Training set produced is stored on a .csv file."
            + "\nSyntax:"
            + "\n    cgather (((-<varname> <value>) | (/<flag>))[ ])*(((-<varname> <value>) | (/<flag>)))"
            + "\nParameters"
            + "\n    -output"
            + "\n        DESCRIPTION"
            + "\n            The output file name of the training set to be produced."
            + "\n            Automatically ends with '.ctset'."
            + "\n        REQUIRED........ Yes"
            + "\n    -protected"
            + "\n        DESCRIPTION"
            + "\n            The index of the interface facing the protected network."
            + "\n            Refer to the output of the 'cifaces' command."
            + "\n        REQUIRED........ No"
            + "\n        DEFAULT VALUE... as specified in the 'chimera.config' file"
            + "\n    -access"
            + "\n            A CHIMERA-JNetPcap Packet Filter Expression."
            + "\n            If provided, the following apply:"
            + "\n                If the /allow flag is set, the following apply:"
            + "\n                    Matching packets are included in the training set."
            + "\n                    Non matching packets are excluded from the training set."
            + "\n                If the /allow flag is not set, the following apply:"
            + "\n                    Matching packets are excluded from the training set."
            + "\n                    Non matching packets are included in the training set."
            + "\n            If not provided, the following apply:"
            + "\n                If the /allow flag is set, the following apply:"
            + "\n                    All packets are included in the training set."
            + "\n                If the /allow flag is not set, the following apply:"
            + "\n                    All packets are excluded from the training set."
            + "\n        REQUIRED........ No"
            + "\n        DEFAULT VALUE... N/A"
            + "\n    /allow"
            + "\n        DESCRIPTION"
            + "\n            If set, the following apply:"
            + "\n                If -access is provided, the following apply:"
            + "\n                    Packets matching -access are included in the training set."
            + "\n                    Packets not matching -access are not included in the training set."
            + "\n                If -access is not provided, the following apply:"
            + "\n                    All packets are included in the training set."
            + "\n            If not set, the following apply:"
            + "\n                If -access is provided, the following apply:"
            + "\n                    Packets matching -access are not included in the training set."
            + "\n                    Packets not matching -access are included in the training set."
            + "\n                If -access is not provided, the following apply:"
            + "\n                    All packets are not included in the training set."
            + "\n        REQUIRED........ No"
            + "\n        DEFAULT VALUE... N/A"
            + "\n    -training"
            + "\n        DESCRIPTION"
            + "\n            A CHIMERA-JNetPcap Packet Filter Expression."
            + "\n            If provided, the following apply:"
            + "\n                If the /attack flag is set, the following apply:"
            + "\n                    Matching packets are flagged as attacks."
            + "\n                    Non matching packets are flagged as normal."
            + "\n                If the /attack flag is not set, the following apply:"
            + "\n                    Matching packets are flagged as normal."
            + "\n                    Non matching packets are flagged as attacks."
            + "\n            If not provided, the following apply:"
            + "\n                If the /attack flag is set, the following apply:"
            + "\n                    All packets are flagged as attacks."
            + "\n                If the /attack flag is not set, the following apply:"
            + "\n                    All packets are flagged as normal."
            + "\n        REQUIRED........ No"
            + "\n        DEFAULT VALUE... N/A"
            + "\n    /attack"
            + "\n        DESCRIPTION"
            + "\n            If set, the following apply:"
            + "\n                If -training is provided, the following apply:"
            + "\n                    Packets matching -training are flagged as attacks."
            + "\n                    Packets not matching -training are flagged as normal."
            + "\n                If -training is not provided, the following apply:"
            + "\n                    All packets are flagged as attacks."
            + "\n            If not set, the following apply:"
            + "\n                If -training is provided, the following apply:"
            + "\n                    Packets matching -training are flagged as normal."
            + "\n                    Packets not matching -training are flagged as attacks."
            + "\n                If -training is not provided, the following apply:"
            + "\n                    All packets are flagged as normal."
            + "\n        REQUIRED........ No"
            + "\n        DEFAULT VALUE... N/A"
            + "\n    /verbose"
            + "\n        DESCRIPTION"
            + "\n            If set, the following apply:"
            + "\n                Output messages are printed on the screen."
            + "\n            If not set, the following apply:"
            + "\n                Output messages are not printed on the screen."
            + "\n        REQUIRED........ No"
            + "\n        DEFAULT VALUE... N/A";

    public static void main(String[] args) {
        try {
            //help
            if (args.length == 1) {
                if (args[0].equals("/help")) {
                    System.out.println(USAGE);
                    return;
                }
            }

            //load config
            Config config = Config.loadConfig();

            //load criterias
            Criteria[] criterias = Criteria.loadCriterias();

            //parse args
            HashMap<String, String> _args = UtilsParse.parseArgs(args);

            //verbose
            boolean verbose = false;
            if (_args.containsKey("/verbose")) {
                verbose = Boolean.parseBoolean(_args.get("/verbose"));
            }
            if (!verbose) {
                System.out.close();
            }

            //load dump file
            if (!_args.containsKey("-output")) {
                throw new Exception("The argument '-output' must be provided.");
            }
            File trainingDumpFile = new File(_args.get("-output") + ".ctset");

            //load interfaces
            int ifProtectedIdx = -1;
            try {
                if (_args.containsKey("-protected")) {
                    ifProtectedIdx = Integer.parseInt(_args.get("-protected"));
                }
            } catch (Exception ex) {
                throw new Exception("The argument '-protected' must provide a numerical value.");
            }
            ArrayList<PcapIf> interfaces = UtilsPcap.getInterfaces();
            String ifProtected = null;
            try {
                ifProtected = interfaces.get(ifProtectedIdx).getName();
            } catch (Exception ex) {
                throw new Exception("Interface index '" + ifProtectedIdx + "' is invalid.");
            }

            //exclude filter
            PacketFilter accessFilter = null;
            if (_args.containsKey("-access")) {
                accessFilter = PacketFilter.parseExpression(_args.get("-access"));
            }

            //gather access flag
            boolean allowFiltered = false;
            if (_args.containsKey("/allow")) {
                allowFiltered = Boolean.parseBoolean(_args.get("/allow"));
            }

            //filter
            PacketFilter trainingFilter = null;
            if (_args.containsKey("-training")) {
                trainingFilter = PacketFilter.parseExpression(_args.get("-training"));
            }

            //gather attacks flag
            boolean tagFilteredAsAttacks = false;
            if (_args.containsKey("/attack")) {
                tagFilteredAsAttacks = Boolean.parseBoolean(_args.get("/attack"));
            }

            //ingress queues
            IntermodulePipe<PduAtomic> inGatherSniffOut = new IntermodulePipe<PduAtomic>();
            IntermodulePipe<PduAtomic> inGatherStatsOut = new IntermodulePipe<PduAtomic>();
            IntermodulePipe<PduAtomic> inGatherStateOut = new IntermodulePipe<PduAtomic>();

            //egress queues
            IntermodulePipe<PduAtomic> egGatherSniffOut = new IntermodulePipe<PduAtomic>();

            //shared resources
            ConcurrentHashMap<CriteriaInstance, Statistics> statsTableAtomic = new ConcurrentHashMap<CriteriaInstance, Statistics>();
            ConcurrentHashMap<TcpSocketPair, Connection> stateTable = new ConcurrentHashMap<TcpSocketPair, Connection>();

            //component holder
            HashMap<String, Component> components = new HashMap<String, Component>();

            //daemons
            components.put("stats", new ComponentStatisticsTable(criterias, statsTableAtomic, config.statsTimeoutMs));
            components.put("states", new ComponentStateTable(stateTable, config.stateTimeoutMs));

            //ingress path
            components.put("gather.in.sniff", new ComponentSniffer(inGatherSniffOut, ifProtected, accessFilter, allowFiltered, TrafficDirection.Egress, TrafficDirection.Ingress));
            components.put("gather.in.stats", new ComponentStatisticsTracker(inGatherSniffOut, inGatherStatsOut, criterias, statsTableAtomic));
            components.put("gather.in.states", new ComponentStateTracker(inGatherStatsOut, inGatherStateOut, stateTable));
            components.put("gather.in.dumper", new ComponentDumper(inGatherStateOut, ifProtected, criterias, trainingDumpFile, trainingFilter, tagFilteredAsAttacks));
            inGatherSniffOut.setWriter((ComponentActive) components.get("gather.in.sniff"));
            inGatherSniffOut.setReader((ComponentActive) components.get("gather.in.stats"));
            inGatherStatsOut.setWriter((ComponentActive) components.get("gather.in.stats"));
            inGatherStatsOut.setReader((ComponentActive) components.get("gather.in.states"));
            inGatherStateOut.setWriter((ComponentActive) components.get("gather.in.states"));
            inGatherStateOut.setReader((ComponentActive) components.get("gather.in.dumper"));

            //egress path
            components.put("gather.eg.sniff", new ComponentSniffer(egGatherSniffOut, ifProtected, accessFilter, allowFiltered, TrafficDirection.Ingress, TrafficDirection.Egress));
            components.put("gather.eg.states", new ComponentStateTracker(egGatherSniffOut, null, stateTable));
            egGatherSniffOut.setWriter((ComponentActive) components.get("gather.eg.sniff"));
            egGatherSniffOut.setReader((ComponentActive) components.get("gather.eg.states"));

            //controller
            ComponentController controller = new ComponentController(components, config.controlPort);

            //start components
            for (String c : components.keySet()) {
                Component _c = components.get(c);
                if (_c instanceof ComponentActive) {
                    ((ComponentActive) (_c)).start();
                }
            }
            controller.start();

            //print dump count if verbose
            if (verbose) {
                ComponentDumper dumper = (ComponentDumper) components.get("gather.in.dumper");
                while (dumper.isAlive()) {
                    Thread.sleep(100);
                    System.out.print("Training Data Instances Gathered: " + dumper.getProcessed() + "\r");
                }
            }

            //join threads
            for (String c : components.keySet()) {
                Component _c = components.get(c);
                if (_c instanceof ComponentActive) {
                    ((ComponentActive) (_c)).join();
                }
            }
            controller.join();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(ex.getMessage());
            System.out.println("Type 'cgather /help' to see usage.");
        }
    }
}
