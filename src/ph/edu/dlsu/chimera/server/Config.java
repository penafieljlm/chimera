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
import java.io.IOException;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class Config {

    public int adminPort;
    public String ifExternal;
    public String ifInternal;
    public long statsTimeoutMs;
    public long stateTimeoutMs;

    public Config() {
        this.adminPort = -1;
        this.ifExternal = null;
        this.ifInternal = null;
        this.statsTimeoutMs = 300000;
        this.stateTimeoutMs = 300000;
    }

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
