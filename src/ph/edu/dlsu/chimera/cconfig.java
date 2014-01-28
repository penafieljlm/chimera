/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera;

import java.util.HashMap;
import ph.edu.dlsu.chimera.core.Config;
import ph.edu.dlsu.chimera.util.UtilsParse;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class cconfig {

    public static final String USAGE = ""
            + "\nCHIMERA Configure Command Usage (cconfig)"
            + "\nDescription:"
            + "\n    The cdiag command prints the state of a specified component."
            + "\n    The command will only work if there is an ongoing phase."
            + "\nSyntax:"
            + "\n    cconfig (((-<varname> <value>) | (/<flag>))[ ])*(((-<varname> <value>) | (/<flag>)))"
            + "\nParameters"
            + "\n    -port"
            + "\n        DESCRIPTION"
            + "\n            The port to listen for control messages during deployment."
            + "\n        REQUIRED........ No"
            + "\n        DEFAULT VALUE... N/A"
            + "\n        DEFAULT CONFIG.. 9999"
            + "\n    -protected"
            + "\n        DESCRIPTION"
            + "\n            The name of the interface facing the protected network."
            + "\n            Refer to the output of the 'cifaces' command."
            + "\n        REQUIRED........ No"
            + "\n        DEFAULT VALUE... N/A"
            + "\n        DEFAULT CONFIG.. null"
            + "\n    -statetimeout"
            + "\n        DESCRIPTION"
            + "\n            The amount of time before a TCP state is allowed to be idle."
            + "\n        REQUIRED........ No"
            + "\n        DEFAULT VALUE... N/A"
            + "\n        DEFAULT CONFIG.. 300000"
            + "\n    -statstimeout"
            + "\n        DESCRIPTION"
            + "\n             The amount of time before a criteria instance is allowed to be idle."
            + "\n        REQUIRED........ No"
            + "\n        DEFAULT VALUE... N/A"
            + "\n        DEFAULT CONFIG.. 300000"
            + "\n    /show"
            + "\n        DESCRIPTION"
            + "\n            Print the configuration after modification are made."
            + "\n            Do not provide any parameters to see current configuration. "
            + "\n        REQUIRED........ No";

    public static void main(String[] args) {
        try {
            //help
            if (args.length == 1) {
                if (args[0].equals("/help")) {
                    System.out.println(USAGE);
                    return;
                }
            }

            //parse args
            HashMap<String, String> _args = UtilsParse.parseArgs(args);

            //control port
            Integer _port = null;
            try {
                if (_args.containsKey("-port")) {
                    _port = Integer.parseInt(_args.get("-port"));
                }
            } catch (NumberFormatException ex) {
                throw new Exception("The argument '-port' must provide a numerical value.");
            }

            //interface
            String _protected = _args.get("-protected");

            //state timout
            Long _statetimeout = null;
            try {
                if (_args.containsKey("-statetimeout")) {
                    _statetimeout = Long.parseLong(_args.get("-statetimeout"));
                }
            } catch (NumberFormatException ex) {
                throw new Exception("The argument '-statetimeout' must provide a numerical value.");
            }

            //stats timeout
            Long _statstimeout = null;
            try {
                if (_args.containsKey("-statstimeout")) {
                    _statstimeout = Long.parseLong(_args.get("-statstimeout"));
                }
            } catch (NumberFormatException ex) {
                throw new Exception("The argument '-statstimeout' must provide a numerical value.");
            }

            //execute
            Config config = Chimera.cconfig(_port, _protected, _statetimeout, _statstimeout);

            //show
            boolean show = false;
            if (_args.containsKey("/show")) {
                show = Boolean.parseBoolean(_args.get("/show"));
            }

            if (show) {
                System.out.println("CHIMERA Configuration:");
                System.out.println("    config.controlPort....." + config.controlPort);
                System.out.println("    config.ifProtected....." + config.ifProtected);
                System.out.println("    config.stateTimeoutMs.." + config.stateTimeoutMs);
                System.out.println("    config.statsTimeoutMs.." + config.statsTimeoutMs);
            }

        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.out.println("Type 'cconfig /help' to see usage.");
        }
    }
}
