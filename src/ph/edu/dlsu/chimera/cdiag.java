/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera;

import java.util.HashMap;
import ph.edu.dlsu.chimera.client.admin.Client;
import ph.edu.dlsu.chimera.core.Config;
import ph.edu.dlsu.chimera.server.admin.messages.CommandDiagnose;
import ph.edu.dlsu.chimera.util.ToolsParse;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class cdiag {

    public static final String USAGE = ""
            + "\nCHIMERA Diagnose Command Usage (cdiag)"
            + "\nDescription:"
            + "\n    The cdiag command prints the state of a specified component."
            + "\n    The command will only work if there is an ongoing phase."
            + "\nSyntax:"
            + "\n    cdiag (((-<varname> <value>) | (/<flag>))[ ])*(((-<varname> <value>) | (/<flag>)))"
            + "\nParameters"
            + "\n    -component"
            + "\n        DESCRIPTION"
            + "\n            The name of the component to be diagnosed."
            + "\n        REQUIRED.. Yes";

    public static void main(String[] args) {
        try {
            //help
            if (args.length == 1) {
                if (args[0].equals("/help")) {
                    System.out.println(cgather.USAGE);
                    return;
                }
            }
            
            //load config
            Config config = Config.loadConfig();

            //parse args
            HashMap<String, String> _args = ToolsParse.parseArgs(args);
            if (!_args.containsKey("-component")) {
                throw new Exception("The argument '-component' must be provided.");
            }
            String component = _args.get("-component");
            
            //run command
            Client client = new Client(config.controlPort, System.out, new CommandDiagnose(component));
            client.run();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.out.println("Type 'cdiag /help' to see usage.");
            return;
        }
    }
}
