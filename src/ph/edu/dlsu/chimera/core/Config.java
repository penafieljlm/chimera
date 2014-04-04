package ph.edu.dlsu.chimera.core;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * An instance of this class constitutes an object which describes the system
 * configuration.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class Config {

    /**
     * The port to listen for control messages during deployment
     */
    public int controlPort;
    /**
     * The name of the interface facing the protected network
     */
    public String ifProtected;
    /**
     * The amount of time that a criteria instance is allowed to be idle
     */
    public long statsTimeoutMs;
    /**
     * The amount of time that a TCP state is allowed to be idle
     */
    public long stateTimeoutMs;
    /**
     * The default syslog UDP port number
     */
    public int syslogPort;

    /**
     * Constructs a new Config object with all the default values.
     */
    public Config() {
        this.controlPort = 9999;
        this.ifProtected = null;
        this.statsTimeoutMs = 300000;
        this.stateTimeoutMs = 300000;
        this.syslogPort = 514;
    }

    /**
     * Loads the contents of the system configuration file onto a Config object.
     * Creates a new system configuration file with default values if none is
     * found.
     *
     * @return The Config object loaded
     * @throws Exception
     */
    public static Config loadConfig() throws Exception {
        //config file
        Config _config = null;
        File configFile = new File("chimera.config");
        JsonReader aConfigReader = null;
        try {
            aConfigReader = new JsonReader(new FileInputStream(configFile));
            _config = (Config) aConfigReader.readObject();
        } catch (FileNotFoundException ex) {
            _config = new Config();
            Config.saveConfig(_config);
        } catch (IOException ex) {
            throw new Exception("Config file 'chimera.config' is corrupted.");
        }
        if (aConfigReader != null) {
            aConfigReader.close();
        }
        return _config;
    }

    /**
     * Saves the contents of the provided Config object onto the system
     * configuration file. Creates a new file with the provided values if no old
     * system configuration file is found.
     *
     * @param config The Config object to be saved
     * @throws Exception
     */
    public static void saveConfig(Config config) throws Exception {
        File configFile = new File("chimera.config");
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
}
