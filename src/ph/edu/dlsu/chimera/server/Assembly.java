/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import ph.edu.dlsu.chimera.server.deployment.Deployment;
import ph.edu.dlsu.chimera.server.admin.AdministrativeModule;
import ph.edu.dlsu.chimera.server.admin.UserBase;
import ph.edu.dlsu.chimera.server.core.Criteria;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class Assembly {

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
    public Config config;

    public Assembly(int port) throws Exception {
        this.admin = new AdministrativeModule(this, port);
        this.users = new UserBase();

        //config file
        this.config = null;
        File aConfigFile = new File("chimera.config");
        JsonReader aConfigReader = null;
        try {
            aConfigReader = new JsonReader(new FileInputStream(aConfigFile));
            this.config = (Config) aConfigReader.readObject();
        } catch (FileNotFoundException ex) {
            this.config = new Config();
            if (!aConfigFile.exists()) {
                try {
                    if (aConfigFile.createNewFile()) {
                        JsonWriter aConfigWriter = new JsonWriter(new FileOutputStream(aConfigFile));
                        aConfigWriter.write(this.config);
                    }
                } catch (IOException ex1) {
                }
            }
        } catch (IOException ex) {
            throw new Exception("Config file 'chimera.config' is corrupted!");
        }
        if (aConfigReader != null) {
            aConfigReader.close();
        }

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
                try (FileWriter cConfigFileWriter = new FileWriter(cConfigFile)) {
                    for (String exp : cExpressions) {
                        cConfigFileWriter.write(exp + ";\r\n");
                    }
                }
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
}
