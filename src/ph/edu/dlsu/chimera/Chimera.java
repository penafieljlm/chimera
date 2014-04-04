package ph.edu.dlsu.chimera;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.jnetpcap.PcapIf;
import ph.edu.dlsu.chimera.components.Component;
import ph.edu.dlsu.chimera.components.ComponentActive;
import ph.edu.dlsu.chimera.components.ComponentController;
import ph.edu.dlsu.chimera.components.ComponentDetector;
import ph.edu.dlsu.chimera.components.ComponentDumper;
import ph.edu.dlsu.chimera.components.ComponentSniffer;
import ph.edu.dlsu.chimera.components.ComponentStateDaemon;
import ph.edu.dlsu.chimera.components.ComponentStateTracker;
import ph.edu.dlsu.chimera.components.ComponentStatisticsDaemon;
import ph.edu.dlsu.chimera.components.ComponentStatisticsCalculator;
import ph.edu.dlsu.chimera.core.Config;
import ph.edu.dlsu.chimera.core.Connection;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.core.Statistics;
import ph.edu.dlsu.chimera.core.TcpSocketPair;
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
import ph.edu.dlsu.chimera.core.PduAtomic;
import ph.edu.dlsu.chimera.core.TrainingOutputResult;
import ph.edu.dlsu.chimera.messages.CommandQuit;
import ph.edu.dlsu.chimera.monitors.PhaseMonitorTraining;
import ph.edu.dlsu.chimera.reflection.PacketFilter;
import ph.edu.dlsu.chimera.rules.RulesManager;
import ph.edu.dlsu.chimera.util.UtilsCommand;
import ph.edu.dlsu.chimera.util.UtilsPcap;
import ph.edu.dlsu.chimera.util.UtilsTraining;

/**
 * The Chimera class contains the general functionalities that the CHIMERA
 * system offers.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class Chimera {

    /**
     * The cconfig function returns the state of a specified component. The
     * command will only work if there is an ongoing phase.
     *
     * @param _port The port to listen for control messages during deployment
     * @param _protected The name of the interface facing the protected network
     * @param _statetimeout The amount of time before a TCP state is allowed to
     * be idle
     * @param _statstimeout The amount of time before a criteria instance is
     * allowed to be idle
     * @param _syslogport The UDP port number of default syslog server
     * applications to contact
     * @return An object which contains information regarding the applied
     * configuration
     * @throws Exception
     */
    public static Config cconfig(Integer _port, String _protected, Long _statetimeout, Long _statstimeout, Integer _syslogport) throws Exception {
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
        if (_syslogport != null) {
            config.syslogPort = _syslogport;
        }

        //save config
        Config.saveConfig(config);

        //return config
        return config;
    }

    /**
     * The cdiag function returns the state of a specified component. The
     * command will only work if there is an ongoing phase.
     *
     * @param _component The name of the component to be diagnosed
     * @return An ArrayList containing the Diagnostics information for the
     * specified component
     * @throws Exception
     */
    public static ArrayList<Diagnostic> cdiag(String _component) throws Exception {
        //check parameters
        if (_component == null) {
            throw new Exception("The argument '-component' must be provided.");
        }

        //load config
        Config config = Config.loadConfig();

        //run command
        Object result = UtilsCommand.send(config.controlPort, new CommandDiagnose(_component));

        //return
        if (result instanceof ArrayList) {
            return (ArrayList<Diagnostic>) result;
        }

        //exception
        throw new Exception("Returned object is not ArrayList<Diagnostic>");
    }

    /**
     * The cifaces function returns the configuration of all detected
     * interfaces.
     *
     * @return The configuration of all detected interfaces
     * @throws Exception
     */
    public static NicData[] cifaces() throws Exception {
        ArrayList<NicData> nics = new ArrayList<NicData>();
        for (PcapIf nic : UtilsPcap.getInterfaces()) {
            nics.add(new NicData(nic));
        }
        return nics.toArray(new NicData[0]);
    }

    /**
     * The cgather function performs the CHIMERA's Data Gathering phase. The
     * training set used in the Training Phase is compiled in this phase. This
     * phase produces the said training set using the traffic captured. Training
     * set produced is stored on a .csv file.
     *
     * @param _monitor An object which the cgather function can notify regarding
     * its progress
     * @param _output The output file name of the training set to be produced.
     * Automatically ends with '.ctset'.
     * @param _protected The name of the interface facing the protected network.
     * Refer to the output of the 'cifaces' command.
     * @param _access A CHIMERA-JNetPcap Packet Filter Expression. If provided,
     * the following apply: If the /allow flag is set, the following apply:
     * Matching packets are included in the training set. Non matching packets
     * are excluded from the training set. If the /allow flag is not set, the
     * following apply: Matching packets are excluded from the training set. Non
     * matching packets are included in the training set. If not provided, the
     * following apply: If the /allow flag is set, the following apply: All
     * packets are included in the training set. If the /allow flag is not set,
     * the following apply: All packets are excluded from the training set.
     * @param _allow If true, the following apply: If -access is provided, the
     * following apply: Packets matching -access are included in the training
     * set. Packets not matching -access are not included in the training set.
     * If -access is not provided, the following apply: All packets are included
     * in the training set. If not set, the following apply: If -access is
     * provided, the following apply: Packets matching -access are not included
     * in the training set. Packets not matching -access are included in the
     * training set. If -access is not provided, the following apply: All
     * packets are not included in the training set.
     * @param _training A CHIMERA-JNetPcap Packet Filter Expression. If
     * provided, the following apply: If the /attack flag is set, the following
     * apply: Matching packets are flagged as attacks. Non matching packets are
     * flagged as normal. If the /attack flag is not set, the following apply:
     * Matching packets are flagged as normal. Non matching packets are flagged
     * as attacks. If not provided, the following apply: If the /attack flag is
     * set, the following apply: All packets are flagged as attacks. If the
     * /attack flag is not set, the following apply: All packets are flagged as
     * normal.
     * @param _attack If true, the following apply: If -training is provided,
     * the following apply: Packets matching -training are flagged as attacks.
     * Packets not matching -training are flagged as normal. If -training is not
     * provided, the following apply: All packets are flagged as attacks. If not
     * set, the following apply: If -training is provided, the following apply:
     * Packets matching -training are flagged as normal. Packets not matching
     * -training are flagged as attacks. If -training is not provided, the
     * following apply: All packets are flagged as normal.
     * @return A File object pointing to the file containing the gathered data
     * set
     * @throws Exception
     */
    public static File cgather(PhaseMonitorGathering _monitor, String _output, String _protected, String _access, boolean _allow, String _training, boolean _attack) throws Exception {
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

        //queues
        IntermodulePipe<PduAtomic> gatherSniffOut = new IntermodulePipe<PduAtomic>();
        IntermodulePipe<PduAtomic> gatherStateOut = new IntermodulePipe<PduAtomic>();
        IntermodulePipe<PduAtomic> gatherStatsOut = new IntermodulePipe<PduAtomic>();

        //shared resources
        ConcurrentHashMap<CriteriaInstance, Statistics> statsTableAtomic = new ConcurrentHashMap<CriteriaInstance, Statistics>();
        ConcurrentHashMap<TcpSocketPair, Connection> stateTable = new ConcurrentHashMap<TcpSocketPair, Connection>();

        //component holder
        HashMap<String, Component> components = new HashMap<String, Component>();

        //daemons
        components.put(cgather.MODULE_STATSD, new ComponentStatisticsDaemon(criterias, statsTableAtomic, config.statsTimeoutMs));
        components.put(cgather.MODULE_STATESD, new ComponentStateDaemon(stateTable, config.stateTimeoutMs));

        //pipeline
        components.put(cgather.MODULE_SNIFF, new ComponentSniffer(gatherSniffOut, ifProtected, accessFilter, _allow));
        components.put(cgather.MODULE_STATES, new ComponentStateTracker(gatherSniffOut, gatherStateOut, stateTable));
        components.put(cgather.MODULE_STATS, new ComponentStatisticsCalculator(gatherStateOut, gatherStatsOut, criterias, statsTableAtomic));
        components.put(cgather.MODULE_DUMPER, new ComponentDumper(gatherStatsOut, ifProtected, criterias, trainingDumpFile, trainingFilter, _attack));
        gatherSniffOut.setWriter((ComponentActive) components.get(cgather.MODULE_SNIFF));
        gatherSniffOut.setReader((ComponentActive) components.get(cgather.MODULE_STATES));
        gatherStateOut.setWriter((ComponentActive) components.get(cgather.MODULE_STATES));
        gatherStateOut.setReader((ComponentActive) components.get(cgather.MODULE_STATS));
        gatherStatsOut.setWriter((ComponentActive) components.get(cgather.MODULE_STATS));
        gatherStatsOut.setReader((ComponentActive) components.get(cgather.MODULE_DUMPER));

        //controller
        if (_monitor != null) {
            _monitor.setComponents(components);
        }
        ComponentController controller = new ComponentController(components, config.controlPort);

        //set monitor stats
        if (_monitor != null) {
            ComponentSniffer sniffer = (ComponentSniffer) components.get(cgather.MODULE_SNIFF);
            _monitor.setStatsMonitor(sniffer.ingressStats);
        }

        //start components
        for (String c : components.keySet()) {
            Component _c = components.get(c);
            if (_c instanceof ComponentActive) {
                ((ComponentActive) (_c)).start();
            }
        }
        controller.start();

        //get dumper
        ComponentDumper dumper = (ComponentDumper) components.get(cgather.MODULE_DUMPER);

        //update monitor
        if (_monitor != null) {
            while (dumper.isAlive()) {
                Thread.sleep(_monitor.updateInterval);
                if (_monitor.getInstancesGathered() != dumper.ingressStats.getTotalEncounters()) {
                    _monitor.setInstancesGathered(dumper.ingressStats.getTotalEncounters());
                }
                if (_monitor.isTerminated()) {
                    Chimera.cquit(components);
                    break;
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
        return trainingDumpFile;
    }

    /**
     * The ctrain function performs the CHIMERA's model building phase. The
     * 'normal' model used in the Production Phase is built in this phase. This
     * phase produces the said model using the training set captured during Data
     * Gathering. The model produced is stored on a .cmodel file.
     *
     * @param _monitor An object which the ctrain function can notify regarding
     * its progress
     * @param _input The input file name of the training set file. Automatically
     * ends with '.ctset'.
     * @param _output The output file name of the model to be produced.
     * Automatically ends with '.cmodel'.
     * @param _filter Attribute filter regular expression. May be used to
     * exclude certain attributes from the training set. If provided, the
     * following apply: If the /exclude flag is set, the following apply:
     * Matching attributes are excluded. Non matching attributes are included.
     * If the /exclude flag is not set, the following apply: Matching attributes
     * are not included. Non matching attributes are excluded. If not provided,
     * the following apply: If the /exclude flag is set, the following apply:
     * All attributes are excluded. If the /exclude flag is not set, the
     * following apply: All attributes are included.
     * @param _exclude If set, the following apply:" If -filter is provided, the
     * following apply:" Attributes matching -filter are excluded." Attributes
     * not matching -filter are included." If -filter is not provided, the
     * following apply:" If not set, the following apply:" If -filter is
     * provided, the following apply:" Attributes matching -filter are
     * included." Attributes not matching -filter are excluded." If -filter is
     * not provided, the following apply:" All attributes are included."
     * @return An object containing the results of the training process
     * @throws Exception
     */
    public static TrainingOutputResult ctrain(PhaseMonitorTraining _monitor, String _input, String _output, String _filter, boolean _exclude) throws Exception {
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

        //create model
        TrainingResult result = UtilsTraining.train(_monitor, trainingFile, _filter, _exclude);

        //try write
        if (result != null) {
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
        }

        //return
        return new TrainingOutputResult(result, modelFile);
    }

    /**
     * The cproduce function performs the CHIMERA's Production phase. The
     * 'normal' model produced in the Training Phase is used in this phase. This
     * phase checks network traffic for possible Denial-of-Service attacks. Upon
     * the discovery of an attack, logs and rules can optionally be created.
     *
     * @param _monitor An object which the cproduce function can notify
     * regarding its progress
     * @param _input The input file name of the model to be used. Automatically
     * ends with '.cmodel'.
     * @param _syslog Hostname or IP Address of the Syslog server to send logs
     * to.
     * @param _syslogport UDP port number of the Syslog server application.
     * @param _active If set, the following apply: Linux kernel firewall rules
     * are created to block detected DoS attacks. If not set, the following
     * apply: Linux kernel firewall rules are not created to block detected DoS
     * attacks.
     * @return An array of logs generated during the production process
     * @throws Exception
     */
    public static Log[] cproduce(PhaseMonitorProduction _monitor, String _input, String _syslog, Integer _syslogport, boolean _active) throws Exception {
        //check parameters
        if (_input == null) {
            throw new Exception("The argument '-input' must be provided.");
        }
        //load config
        Config config = Config.loadConfig();

        //load model file
        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(_input + ".cmodel"));
        ModelSerializable modelSerializable = (ModelSerializable) objectInputStream.readObject();
        ModelLive modelLive = new ModelLive(modelSerializable);

        //load criterias
        Criteria[] criterias = modelLive.criteriaSubModels.keySet().toArray(new Criteria[0]);

        //syslog server
        InetAddress syslogServ = (_syslog != null) ? InetAddress.getByName(_syslog) : null;
        int syslogPort = (_syslogport != null) ? _syslogport : config.syslogPort;

        //queues
        IntermodulePipe<PduAtomic> produceSniffOut = new IntermodulePipe<PduAtomic>();
        IntermodulePipe<PduAtomic> produceStateOut = new IntermodulePipe<PduAtomic>();
        IntermodulePipe<PduAtomic> produceStatsOut = new IntermodulePipe<PduAtomic>();

        //shared resources
        ConcurrentHashMap<CriteriaInstance, Statistics> statsTableAtomic = new ConcurrentHashMap<CriteriaInstance, Statistics>();
        ConcurrentHashMap<TcpSocketPair, Connection> stateTable = new ConcurrentHashMap<TcpSocketPair, Connection>();
        RulesManager rulesManager = (_active) ? new RulesManager() : null;

        //component holder
        HashMap<String, Component> components = new HashMap<String, Component>();

        //daemons
        components.put(cproduce.MODULE_STATSD, new ComponentStatisticsDaemon(criterias, statsTableAtomic, config.statsTimeoutMs));
        components.put(cproduce.MODULE_STATESD, new ComponentStateDaemon(stateTable, config.stateTimeoutMs));

        //pipeline
        components.put(cproduce.MODULE_SNIFF, new ComponentSniffer(produceSniffOut, modelLive.protectedInterface));
        components.put(cproduce.MODULE_STATES, new ComponentStateTracker(produceSniffOut, produceStateOut, stateTable));
        components.put(cproduce.MODULE_STATS, new ComponentStatisticsCalculator(produceStateOut, produceStatsOut, criterias, statsTableAtomic));
        components.put(cproduce.MODULE_DETECT, new ComponentDetector(produceStatsOut, modelLive, rulesManager, syslogServ, syslogPort));
        produceSniffOut.setWriter((ComponentActive) components.get(cproduce.MODULE_SNIFF));
        produceSniffOut.setReader((ComponentActive) components.get(cproduce.MODULE_STATES));
        produceStateOut.setWriter((ComponentActive) components.get(cproduce.MODULE_STATES));
        produceStateOut.setReader((ComponentActive) components.get(cproduce.MODULE_STATS));
        produceStatsOut.setWriter((ComponentActive) components.get(cproduce.MODULE_STATS));
        produceStatsOut.setReader((ComponentActive) components.get(cproduce.MODULE_DETECT));

        //controller
        if (_monitor != null) {
            _monitor.setComponents(components);
        }
        ComponentController controller = new ComponentController(components, config.controlPort);

        //set monitor stats
        if (_monitor != null) {
            ComponentSniffer sniffer = (ComponentSniffer) components.get(cproduce.MODULE_SNIFF);
            _monitor.setStatsMonitor(sniffer.ingressStats);
        }

        //start components
        for (String c : components.keySet()) {
            Component _c = components.get(c);
            if (_c instanceof ComponentActive) {
                ((ComponentActive) (_c)).start();
            }
        }
        controller.start();

        //get detector
        ComponentDetector detector = (ComponentDetector) components.get(cproduce.MODULE_DETECT);

        //update monitor
        if (_monitor != null) {
            while (detector.isAlive()) {
                Thread.sleep(_monitor.updateInterval);
                _monitor.setLogs(detector.logs);
                if (_monitor.isTerminated()) {
                    Chimera.cquit(components);
                    break;
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
        controller.join();

        return detector.logs.toArray(new Log[0]);
    }

    /**
     * The cquit function stops any running phases. The command will only work
     * if there is an ongoing phase.
     *
     * @throws Exception
     */
    public static void cquit() throws Exception {
        //load config
        Config config = Config.loadConfig();

        //run command
        Object result = UtilsCommand.send(config.controlPort, new CommandQuit());
    }

    /**
     * The cquit function stops any running phases. The command will only work
     * if there is an ongoing phase.
     *
     * @param assembly The set of components to be stopped
     * @throws Exception
     */
    public static void cquit(HashMap<String, Component> assembly) throws Exception {
        for (String c : assembly.keySet()) {
            Component _c = assembly.get(c);
            if (_c instanceof ComponentActive) {
                ComponentActive _ca = (ComponentActive) _c;
                _ca.kill();
                _ca.join();
            }
        }
    }
}
