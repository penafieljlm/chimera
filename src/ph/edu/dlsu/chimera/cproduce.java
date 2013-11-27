/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.jnetpcap.Pcap.Direction;
import ph.edu.dlsu.chimera.components.Component;
import ph.edu.dlsu.chimera.components.ComponentActive;
import ph.edu.dlsu.chimera.components.ComponentController;
import ph.edu.dlsu.chimera.components.ComponentDecision;
import ph.edu.dlsu.chimera.components.ComponentSniffer;
import ph.edu.dlsu.chimera.components.ComponentStateTable;
import ph.edu.dlsu.chimera.components.ComponentStateTracker;
import ph.edu.dlsu.chimera.components.ComponentStatisticsTable;
import ph.edu.dlsu.chimera.components.ComponentStatisticsTracker;
import ph.edu.dlsu.chimera.core.Config;
import ph.edu.dlsu.chimera.core.Connection;
import ph.edu.dlsu.chimera.core.TcpSocketPair;
import ph.edu.dlsu.chimera.core.Statistics;
import ph.edu.dlsu.chimera.core.criteria.Criteria;
import ph.edu.dlsu.chimera.core.criteria.CriteriaInstance;
import ph.edu.dlsu.chimera.core.model.ModelLive;
import ph.edu.dlsu.chimera.core.model.ModelSerializable;
import ph.edu.dlsu.chimera.core.tools.IntermodulePipe;
import ph.edu.dlsu.chimera.pdu.PduAtomic;
import ph.edu.dlsu.chimera.util.UtilsParse;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class cproduce {

    public static final String USAGE = ""
            + "\nCHIMERA Training Command Usage (ctrain)"
            + "\nDescription:"
            + "\n    The cproduce command starts the CHIMERA's Production phase."
            + "\n    The 'normal' model produced in the Training Phase is used in this phase."
            + "\n    This phase checks network traffic for possible Denial-of-Service attacks."
            + "\n    Upon the discovery of an attack, logs and rules can optionally be created."
            + "\nSyntax:"
            + "\n    cproduce (((-<varname> <value>) | (/<flag>))[ ])*(((-<varname> <value>) | (/<flag>)))"
            + "\nParameters"
            + "\n    -input"
            + "\n        DESCRIPTION"
            + "\n            The input file name of the model to be used."
            + "\n            Automatically ends with '.cmodel'."
            + "\n        REQUIRED........ Yes"
            + "\n    -syslog"
            + "\n        DESCRIPTION"
            + "\n            Hostname or IP Address of the Syslog server to send logs to."
            + "\n        REQUIRED........ No"
            + "\n        DEFAULT VALUE... N/A"
            + "\n    /active"
            + "\n        DESCRIPTION"
            + "\n            If set, the following apply:"
            + "\n                Linux kernel firewall rules are created to block detected DoS attacks."
            + "\n            If not set, the following apply:"
            + "\n                Linux kernel firewall rules are not created to block detected DoS attacks."
            + "\n        REQUIRED........ No"
            + "\n        DEFAULT VALUE... N/A";

    public static void main(String[] args) throws Exception {
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

            //load model file
            if (!_args.containsKey("-input")) {
                throw new Exception("The argument '-input' must be provided.");
            }
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(_args.get("-input") + ".cmodel"));
            ModelSerializable modelSerializable = (ModelSerializable) objectInputStream.readObject();
            ModelLive modelLive = new ModelLive(modelSerializable);

            //syslog server
            String syslog = null;
            if (_args.containsKey("-syslog")) {
                syslog = _args.get("-syslog");
            }
            InetAddress syslogServ = (syslog != null) ? InetAddress.getByName(syslog) : null;

            //gather access flag
            boolean active = false;
            if (_args.containsKey("/active")) {
                active = Boolean.parseBoolean(_args.get("/active"));
            }

            //ingress queues
            IntermodulePipe<PduAtomic> exGatherSniffOut = new IntermodulePipe<>();
            IntermodulePipe<PduAtomic> exGatherStatsOut = new IntermodulePipe<>();
            IntermodulePipe<PduAtomic> exGatherStateOut = new IntermodulePipe<>();

            //egress queues
            IntermodulePipe<PduAtomic> inGatherSniffOut = new IntermodulePipe<>();

            //shared resources
            ConcurrentHashMap<CriteriaInstance, Statistics> statsTableAtomic = new ConcurrentHashMap<>();
            ConcurrentHashMap<TcpSocketPair, Connection> stateTable = new ConcurrentHashMap<>();
            List<Object> rulesMap = (active) ? Collections.synchronizedList(Collections.EMPTY_LIST) : null;

            //component holder
            HashMap<String, Component> components = new HashMap<>();

            //daemons
            components.put("stats", new ComponentStatisticsTable(criterias, statsTableAtomic, config.statsTimeoutMs));
            components.put("states", new ComponentStateTable(stateTable, config.stateTimeoutMs));

            //ingress path
            components.put("in.gather.sniff", new ComponentSniffer(exGatherSniffOut, modelLive.protectedInterface, true, Direction.OUT));
            components.put("in.gather.stats", new ComponentStatisticsTracker(exGatherSniffOut, exGatherStatsOut, criterias, statsTableAtomic));
            components.put("in.gather.states", new ComponentStateTracker(exGatherStatsOut, exGatherStateOut, stateTable));
            components.put("in.gather.decision", new ComponentDecision(exGatherStateOut, modelLive, rulesMap, syslogServ));

            //egress path
            components.put("eg.gather.sniff", new ComponentSniffer(exGatherSniffOut, modelLive.protectedInterface, false, Direction.IN));
            components.put("eg.gather.states", new ComponentStateTracker(inGatherSniffOut, null, stateTable));

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

            //join threads
            for (String c : components.keySet()) {
                Component _c = components.get(c);
                if (_c instanceof ComponentActive) {
                    ((ComponentActive) (_c)).join();
                }
            }
            controller.join();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.out.println("Type 'cgather /help' to see usage.");
            return;
        }
    }
}
