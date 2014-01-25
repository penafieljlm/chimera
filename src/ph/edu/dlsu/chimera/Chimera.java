/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.jnetpcap.PcapIf;
import ph.edu.dlsu.chimera.components.Component;
import ph.edu.dlsu.chimera.components.ComponentActive;
import ph.edu.dlsu.chimera.components.ComponentController;
import ph.edu.dlsu.chimera.components.ComponentDetector;
import ph.edu.dlsu.chimera.components.ComponentDumper;
import ph.edu.dlsu.chimera.components.ComponentSniffer;
import ph.edu.dlsu.chimera.components.ComponentStateTable;
import ph.edu.dlsu.chimera.components.ComponentStateTracker;
import ph.edu.dlsu.chimera.components.ComponentStatisticsTable;
import ph.edu.dlsu.chimera.components.ComponentStatisticsTracker;
import ph.edu.dlsu.chimera.core.Config;
import ph.edu.dlsu.chimera.core.Connection;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.core.Statistics;
import ph.edu.dlsu.chimera.core.TcpSocketPair;
import ph.edu.dlsu.chimera.core.TrafficDirection;
import ph.edu.dlsu.chimera.core.TrainingResult;
import ph.edu.dlsu.chimera.core.criteria.Criteria;
import ph.edu.dlsu.chimera.core.criteria.CriteriaInstance;
import ph.edu.dlsu.chimera.core.logs.Log;
import ph.edu.dlsu.chimera.core.model.ModelLive;
import ph.edu.dlsu.chimera.core.model.ModelSerializable;
import ph.edu.dlsu.chimera.core.nic.NicData;
import ph.edu.dlsu.chimera.monitors.PhaseMonitorGathering;
import ph.edu.dlsu.chimera.core.tools.IntermodulePipe;
import ph.edu.dlsu.chimera.messages.CommandDiagnose;
import ph.edu.dlsu.chimera.monitors.PhaseMonitorProduction;
import ph.edu.dlsu.chimera.pdu.PduAtomic;
import ph.edu.dlsu.chimera.reflection.PacketFilter;
import ph.edu.dlsu.chimera.util.UtilsCommand;
import ph.edu.dlsu.chimera.util.UtilsPcap;
import ph.edu.dlsu.chimera.util.UtilsTraining;

/**
 *
 * @author AMD
 */
public class Chimera {

    public static Config cconfig(Integer _port, String _protected, Long _statetimeout, Long _statstimeout) throws Exception {
        //load config
        Config config = Config.loadConfig();

        if (_port != null) {
            config.controlPort = _port;
        }
        if (_protected != null) {
            config.ifProtected = _protected;
        }
        if (_statetimeout != null) {
            config.stateTimeoutMs = _statetimeout;
        }
        if (_statstimeout != null) {
            config.statsTimeoutMs = _statstimeout;
        }

        //save config
        Config.saveConfig(config);

        //return config
        return config;
    }

    public static ArrayList<Diagnostic> cdiag(String _component) throws Exception {
        //check parameters
        if (_component == null) {
            throw new Exception("The argument '-component' must be provided.");
        }

        //load config
        Config config = Config.loadConfig();

        //run command
        Object result = UtilsCommand.send(config.controlPort, new CommandDiagnose(_component), System.out);

        //return
        if (result instanceof ArrayList) {
            return (ArrayList<Diagnostic>) result;
        }

        //exception
        throw new Exception("Returned object is not ArrayList<Diagnostic>");
    }

    public static NicData[] cifaces() throws Exception {
        ArrayList<NicData> nics = new ArrayList<NicData>();
        for (PcapIf nic : UtilsPcap.getInterfaces()) {
            nics.add(new NicData(nic));
        }
        return nics.toArray(new NicData[0]);
    }

    public static long cgather(PhaseMonitorGathering _monitor, String _output, String _protected, String _access, boolean _allow, String _training, boolean _attack) throws Exception {
        //check parameters
        if (_output == null) {
            throw new Exception("The argument '-output' must be provided.");
        }

        //load config
        Config config = Config.loadConfig();

        //protected interface
        String ifProtected = _protected;
        if (ifProtected == null) {
            ifProtected = config.ifProtected;
        }

        //load criterias
        Criteria[] criterias = Criteria.loadCriterias();

        //load dump file
        File trainingDumpFile = new File(_output + ".ctset");

        //access filter
        PacketFilter accessFilter = (_access != null) ? (!_access.trim().isEmpty()) ? PacketFilter.parseExpression(_access) : null : null;

        //training filter
        PacketFilter trainingFilter = (_training != null) ? (!_training.trim().isEmpty()) ? PacketFilter.parseExpression(_training) : null : null;

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
        components.put("gather.in.sniff", new ComponentSniffer(inGatherSniffOut, ifProtected, accessFilter, _allow, TrafficDirection.Egress, TrafficDirection.Ingress));
        components.put("gather.in.stats", new ComponentStatisticsTracker(inGatherSniffOut, inGatherStatsOut, criterias, statsTableAtomic));
        components.put("gather.in.states", new ComponentStateTracker(inGatherStatsOut, inGatherStateOut, stateTable));
        components.put("gather.in.dumper", new ComponentDumper(inGatherStateOut, ifProtected, criterias, trainingDumpFile, trainingFilter, _attack));
        inGatherSniffOut.setWriter((ComponentActive) components.get("gather.in.sniff"));
        inGatherSniffOut.setReader((ComponentActive) components.get("gather.in.stats"));
        inGatherStatsOut.setWriter((ComponentActive) components.get("gather.in.stats"));
        inGatherStatsOut.setReader((ComponentActive) components.get("gather.in.states"));
        inGatherStateOut.setWriter((ComponentActive) components.get("gather.in.states"));
        inGatherStateOut.setReader((ComponentActive) components.get("gather.in.dumper"));

        //egress path
        components.put("gather.eg.sniff", new ComponentSniffer(egGatherSniffOut, ifProtected, accessFilter, _allow, TrafficDirection.Ingress, TrafficDirection.Egress));
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

        //get dumper
        ComponentDumper dumper = (ComponentDumper) components.get("gather.in.dumper");

        //update monitor
        if (_monitor != null) {
            while (dumper.isAlive()) {
                Thread.sleep(_monitor.updateInterval);
                if (_monitor.getInstancesGathered() != dumper.getProcessed()) {
                    _monitor.setInstancesGathered(dumper.getProcessed());
                }
            }
        }

        //join threads
        for (String c : components.keySet()) {
            Component _c = components.get(c);
            if (_c instanceof ComponentActive) {
                ((ComponentActive) (_c)).join();
            }
        }

        //return monitor
        return dumper.getProcessed();
    }

    public static TrainingResult ctrain(String _input, String _output, String _filter, boolean _exclude) throws Exception {
        //check parameters
        if (_input == null) {
            throw new Exception("The argument '-input' must be provided.");
        }
        if (_output == null) {
            throw new Exception("The argument '-output' must be provided.");
        }

        //training file
        File trainingFile = new File(_input + ".ctset");

        //model file
        File modelFile = new File(_output + ".cmodel");

        //filter
        String filter = (_filter != null) ? _filter : "";

        //create model
        TrainingResult result = UtilsTraining.train(trainingFile, filter, _exclude);

        //wrap into serializable model
        ModelSerializable modelSerializable = new ModelSerializable(result.model);

        //open output stream
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(modelFile));

        //write
        oos.writeObject(modelSerializable);

        //flush
        oos.flush();

        //close
        oos.close();

        //return
        return result;
    }

    public static Log[] cproduce(PhaseMonitorProduction _monitor, String _input, String _syslog, boolean _active) throws Exception {
        //check parameters
        if (_input == null) {
            throw new Exception("The argument '-input' must be provided.");
        }

        //load config
        Config config = Config.loadConfig();

        //load criterias
        Criteria[] criterias = Criteria.loadCriterias();

        //load model file
        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(_input + ".cmodel"));
        ModelSerializable modelSerializable = (ModelSerializable) objectInputStream.readObject();
        ModelLive modelLive = new ModelLive(modelSerializable);

        //syslog server
        InetAddress syslogServ = (_syslog != null) ? InetAddress.getByName(_syslog) : null;

        //ingress queues
        IntermodulePipe<PduAtomic> inProduceSniffOut = new IntermodulePipe<PduAtomic>();
        IntermodulePipe<PduAtomic> inProduceStatsOut = new IntermodulePipe<PduAtomic>();
        IntermodulePipe<PduAtomic> inProduceStateOut = new IntermodulePipe<PduAtomic>();

        //egress queues
        IntermodulePipe<PduAtomic> egProduceSniffOut = new IntermodulePipe<PduAtomic>();

        //shared resources
        ConcurrentHashMap<CriteriaInstance, Statistics> statsTableAtomic = new ConcurrentHashMap<CriteriaInstance, Statistics>();
        ConcurrentHashMap<TcpSocketPair, Connection> stateTable = new ConcurrentHashMap<TcpSocketPair, Connection>();
        List<Object> rulesMap = (_active) ? Collections.synchronizedList(Collections.EMPTY_LIST) : null;

        //component holder
        HashMap<String, Component> components = new HashMap<String, Component>();

        //daemons
        components.put("stats", new ComponentStatisticsTable(criterias, statsTableAtomic, config.statsTimeoutMs));
        components.put("states", new ComponentStateTable(stateTable, config.stateTimeoutMs));

        //ingress path
        components.put("produce.in.sniff", new ComponentSniffer(inProduceStateOut, modelLive.protectedInterface, TrafficDirection.Egress, TrafficDirection.Ingress));
        components.put("produce.in.stats", new ComponentStatisticsTracker(inProduceSniffOut, inProduceStatsOut, criterias, statsTableAtomic));
        components.put("produce.in.states", new ComponentStateTracker(inProduceStatsOut, inProduceStateOut, stateTable));
        components.put("produce.in.detector", new ComponentDetector(inProduceStateOut, modelLive, rulesMap, syslogServ, _active));
        inProduceSniffOut.setWriter((ComponentActive) components.get("produce.in.sniff"));
        inProduceSniffOut.setReader((ComponentActive) components.get("produce.in.stats"));
        inProduceStatsOut.setWriter((ComponentActive) components.get("produce.in.stats"));
        inProduceStatsOut.setReader((ComponentActive) components.get("produce.in.states"));
        inProduceStateOut.setWriter((ComponentActive) components.get("produce.in.states"));
        inProduceStateOut.setReader((ComponentActive) components.get("produce.in.detector"));

        //egress path
        components.put("produce.eg.sniff", new ComponentSniffer(egProduceSniffOut, modelLive.protectedInterface, TrafficDirection.Ingress, TrafficDirection.Egress));
        components.put("produce.eg.states", new ComponentStateTracker(egProduceSniffOut, null, stateTable));
        egProduceSniffOut.setWriter((ComponentActive) components.get("produce.eg.sniff"));
        egProduceSniffOut.setReader((ComponentActive) components.get("produce.eg.states"));

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

        //get detector
        ComponentDetector detector = (ComponentDetector) components.get("produce.in.detector");

        //update monitor
        if (_monitor != null) {
            while (detector.isAlive()) {
                Thread.sleep(_monitor.updateInterval);
                _monitor.setLogs(detector.logs);
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

        return detector.logs.toArray(new Log[0]);
    }

}
