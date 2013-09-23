/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.client.admin;

import java.util.HashMap;
import java.util.Set;
import ph.edu.dlsu.chimera.client.admin.convert.CommandConverter;
import ph.edu.dlsu.chimera.client.admin.convert.ConverterDeploy;
import ph.edu.dlsu.chimera.client.admin.convert.ConverterDeployGathering;
import ph.edu.dlsu.chimera.client.admin.convert.ConverterQueryDiagnostics;
import ph.edu.dlsu.chimera.client.admin.convert.ConverterHelp;
import ph.edu.dlsu.chimera.client.admin.convert.ConverterLogout;
import ph.edu.dlsu.chimera.client.admin.convert.ConverterQueryInterfaces;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class CommandHandlerLookup {

    private HashMap<String, CommandConverter> lookup;

    public CommandHandlerLookup() {
        this.lookup = new HashMap<String, CommandConverter>();
        this.lookup.put("abort", new ConverterDeploy());
        this.lookup.put("deploy-gathering", new ConverterDeployGathering());
        this.lookup.put("diag", new ConverterQueryDiagnostics());
        this.lookup.put("help", new ConverterHelp(this));
        this.lookup.put("ifconfig", new ConverterQueryInterfaces());
        this.lookup.put("logout", new ConverterLogout());
    }

    public Set<String> getCommandsList() {
        return this.lookup.keySet();
    }

    public CommandConverter lookup(String command) throws Exception {
        CommandConverter conv = this.lookup.get(command);
        if(conv == null)
            throw new Exception("The string: '" + command + "' is not a command!");
        return conv;
    }

}
