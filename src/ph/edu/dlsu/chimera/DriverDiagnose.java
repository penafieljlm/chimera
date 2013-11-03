/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera;

import java.util.HashMap;
import ph.edu.dlsu.chimera.client.admin.Client;
import ph.edu.dlsu.chimera.server.Config;
import ph.edu.dlsu.chimera.server.admin.messages.CommandDiagnose;
import ph.edu.dlsu.chimera.util.ToolsParse;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class DriverDiagnose {

    public static final String USAGE = ""
            + "\nCHIMERA Diagnose Command Usage"
            + "\nDescription:"
            + "\n    The cdiag command prints the state of a specified component."
            + "\n    The command will only work if there is an ongoing phase."
            + "\nSyntax:"
            + "\n    cdiag <parameter 0> <value 0> ... <parameter n> <value n>"
            + "\nParameters"
            + "\n    -component"
            + "\n        [REQUIRED] The name of the component to be diagnosed.";

    public static void main(String[] args) {
        try {
            //load config
            Config config = Config.loadConfig();

            //parse args
            HashMap<String, String> _args = ToolsParse.parseArgs(args);
            if (!_args.containsKey("-component")) {
                throw new Exception("The argument '-component' must be provided.");
            }
            String component = _args.get("-component");
            
            //run command
            Client client = new Client(config.adminPort, System.out, new CommandDiagnose(component));
            client.run();
        } catch (Exception ex) {
            System.out.println();
            System.err.println(ex.getMessage());
            System.out.println(DriverDiagnose.USAGE);
            return;
        }
    }
}
