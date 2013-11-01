/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import ph.edu.dlsu.chimera.client.admin.messages.MessageInterfaces;
import ph.edu.dlsu.chimera.core.NicData;
import ph.edu.dlsu.chimera.server.deployment.Deployment;
import ph.edu.dlsu.chimera.server.admin.AdministrativeModule;
import ph.edu.dlsu.chimera.server.admin.UserBase;
import ph.edu.dlsu.chimera.server.core.Criteria;
import ph.edu.dlsu.chimera.server.deployment.DeploymentPassive;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class Assembly {

    public final AdministrativeModule admin;
    public final UserBase users;
    /**
     * The assembly of current active modules.
     */
    private Deployment deployment;
    /**
     * The criteriasAtomic that the system uses for measuring statistics.
     */
    private Criteria[] criterias;
    private Config config;
    private ArrayList<PcapIf> interfaces;

    public Assembly(int port, int external, int internal) throws Exception {
        //interfaces
        StringBuilder pcapStrBldr = new StringBuilder();
        this.interfaces = new ArrayList<>();
        int result = Pcap.findAllDevs(this.interfaces, pcapStrBldr);
        if (result != 0 || this.interfaces == null) {
            throw new Exception("Error detecting network interfaces.");
        }

        //config file
        Config _config = null;
        File aConfigFile = new File("chimera.config");
        JsonReader aConfigReader = null;
        try {
            aConfigReader = new JsonReader(new FileInputStream(aConfigFile));
            _config = (Config) aConfigReader.readObject();
            if (port > 0) {
                _config.adminPort = port;
            }
            if (external > 0) {
                try {
                    _config.ifInternal = this.getInterfaces().get(internal).getName();
                } catch (Exception ex) {
                    String msg = "Invalid interface index: " + internal + ". Refer to the list below.";
                    MessageInterfaces m = new MessageInterfaces(this.getInterfacesData());
                    ByteArrayOutputStream ba = new ByteArrayOutputStream();
                    m.handleShellMessage(new PrintStream(ba));
                    throw new Exception(msg + "\n" + ba.toString());
                }
            }
            if (internal > 0) {
                try {
                    _config.ifExternal = this.getInterfaces().get(external).getName();
                } catch (Exception ex) {
                    String msg = "Invalid interface index: " + external + ". Refer to the list below.";
                    MessageInterfaces m = new MessageInterfaces(this.getInterfacesData());
                    ByteArrayOutputStream ba = new ByteArrayOutputStream();
                    m.handleShellMessage(new PrintStream(ba));
                    throw new Exception(msg + "\n" + ba.toString());
                }
            }
        } catch (FileNotFoundException ex) {
            _config = new Config();
            if (port < 0) {
                throw new Exception("Config file 'chimera.config' not found, please provide default administrative port.");
            }
            _config.adminPort = port;
            try {
                _config.ifExternal = this.interfaces.get(external).getName();
            } catch (Exception ex1) {
                String msg = null;
                if (external < 0) {
                    msg = "Config file 'chimera.config' not found, please provide default external interface index. Refer to the list below.";
                } else {
                    msg = "Invalid interface index: " + external + ". Refer to the list below.";
                }
                MessageInterfaces m = new MessageInterfaces(this.getInterfacesData());
                ByteArrayOutputStream ba = new ByteArrayOutputStream();
                m.handleShellMessage(new PrintStream(ba));
                throw new Exception(msg + "\n" + ba.toString());
            }
            try {
                _config.ifInternal = this.interfaces.get(internal).getName();
            } catch (Exception ex1) {
                String msg = null;
                if (internal < 0) {
                    msg = "Config file 'chimera.config' not found, please provide default internal interface index. Refer to the list below.";
                } else {
                    msg = "Invalid interface index: " + internal + ". Refer to the list below.";
                }
                MessageInterfaces m = new MessageInterfaces(this.getInterfacesData());
                ByteArrayOutputStream ba = new ByteArrayOutputStream();
                m.handleShellMessage(new PrintStream(ba));
                throw new Exception(msg + "\n" + ba.toString());
            }
        } catch (IOException ex) {
            throw new Exception("Config file 'chimera.config' is corrupted.");
        }
        if (aConfigReader != null) {
            aConfigReader.close();
        }
        this.applyConfig(_config, aConfigFile);

        //administrative module
        int _port = port;
        if (port < 0) {
            _port = this.config.adminPort;
        }
        this.admin = new AdministrativeModule(this, _port);

        //userbase
        this.users = new UserBase();

        File cConfigFile = new File("criterias.config");
        ArrayList<String> cExpressions = new ArrayList<>();
        if (!cConfigFile.exists()) {
            cExpressions.add("subject(org.jnetpcap.protocol.network.Ip4.source)");
            cExpressions.add("subject(org.jnetpcap.protocol.network.Ip4.source, org.jnetpcap.protocol.tcpip.Tcp.source)");
            cExpressions.add("subject(org.jnetpcap.protocol.network.Ip4.source, org.jnetpcap.protocol.tcpip.Tcp.source) filter(org.jnetpcap.protocol.tcpip.Tcp.flags=hex:02)");

            cExpressions.add("subject(org.jnetpcap.protocol.network.Ip4.destination)");
            cExpressions.add("subject(org.jnetpcap.protocol.network.Ip4.destination, org.jnetpcap.protocol.tcpip.Tcp.destination)");
            cExpressions.add("subject(org.jnetpcap.protocol.network.Ip4.destination, org.jnetpcap.protocol.tcpip.Tcp.destination) filter(org.jnetpcap.protocol.tcpip.Tcp.flags=hex:02)");

            cExpressions.add("subject(org.jnetpcap.protocol.network.Ip4.source, org.jnetpcap.protocol.network.Ip4.destination)");
            cExpressions.add("subject(org.jnetpcap.protocol.network.Ip4.source, org.jnetpcap.protocol.tcpip.Tcp.source, org.jnetpcap.protocol.network.Ip4.destination, org.jnetpcap.protocol.tcpip.Tcp.destination)");
            cExpressions.add("subject(org.jnetpcap.protocol.network.Ip4.source, org.jnetpcap.protocol.tcpip.Tcp.source, org.jnetpcap.protocol.network.Ip4.destination, org.jnetpcap.protocol.tcpip.Tcp.destination) filter(org.jnetpcap.protocol.tcpip.Tcp.flags=hex:02)");

            if (cConfigFile.createNewFile()) {
                FileWriter cConfigFileWriter = new FileWriter(cConfigFile);
                for (String exp : cExpressions) {
                    cConfigFileWriter.write(exp + ";\r\n");
                }
                cConfigFileWriter.close();
            }
        } else {
            Scanner cConfigFileScanner = new Scanner(cConfigFile);
            cConfigFileScanner = cConfigFileScanner.useDelimiter(";");
            while (cConfigFileScanner.hasNext()) {
                String exp = cConfigFileScanner.next().trim();
                if (exp.length() > 0) {
                    cExpressions.add(exp);
                }
            }
        }
        this.criterias = new Criteria[cExpressions.size()];
        for (int i = 0; i < this.criterias.length; i++) {
            this.criterias[i] = new Criteria(cExpressions.get(i));
        }

        this.setDeployment(new DeploymentPassive(this));
    }

    public void applyConfig(Config config, File configFile) throws Exception {
        this.config = config;
        if (configFile.exists()) {
            try {
                configFile.delete();
            } catch (Exception ex) {
                throw new Exception("Unable to overwrite 'chimera.config'.");
            }
        }
        try {
            if (configFile.createNewFile()) {
                JsonWriter aConfigWriter = new JsonWriter(new FileOutputStream(configFile));
                aConfigWriter.write(config);
            }
        } catch (IOException ex) {
            throw new Exception("Unable to write 'chimera.config'.");
        }
    }

    public Config getConfig() {
        return this.config;
    }

    /**
     * @return the set of Criteria used by this Assembly.
     */
    public Criteria[] getCriterias() {
        return this.criterias;
    }

    /**
     * Aborts the current deployment and starts a new one.
     *
     * @param deployment
     */
    public void setDeployment(Deployment deployment) {
        if (this.deployment != null) {
            this.deployment.killDeployment();
        }
        this.deployment = deployment;
        if (this.deployment != null) {
            this.deployment.startDeployment();
        }
    }

    /**
     * @return the current deployment; null if there is no current deployment
     */
    public Deployment getDeployment() {
        return this.deployment;
    }

    public void startAdmin() {
        this.admin.start();
    }

    public ArrayList<PcapIf> getInterfaces() {
        return this.interfaces;
    }

    public ArrayList<NicData> getInterfacesData() {
        ArrayList<NicData> ifaces = new ArrayList<>();
        for (PcapIf iface : this.getInterfaces()) {
            ifaces.add(new NicData(iface));
        }
        return ifaces;
    }
}
