/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera;

import java.util.ArrayList;
import java.util.HashMap;
import org.jnetpcap.PcapIf;
import ph.edu.dlsu.chimera.server.Config;
import ph.edu.dlsu.chimera.util.ToolsInterface;
import ph.edu.dlsu.chimera.util.ToolsParse;


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
            + "\n        [OPTIONAL] The port to listen for control messages during deployment."
            + "\n        [DEFAULT VALUE : N/A]"
            + "\n        [DEFAULT CONFIG : 9999]"
            + "\n    -external"
            + "\n        [OPTIONAL] The index of the external interface."
            + "\n        Refer to the output of the 'cifaces' command."
            + "\n        [DEFAULT VALUE : N/A]"
            + "\n        [DEFAULT CONFIG : N/A]"
            + "\n    -internal"
            + "\n        [OPTIONAL] The index of the internal interface."
            + "\n        Refer to the output of the 'cifaces' command."
            + "\n        [DEFAULT VALUE : N/A]"
            + "\n        [DEFAULT CONFIG : null]"
            + "\n    -statetimeout"
            + "\n        [OPTIONAL] The amount of time before a TCP state is allowed to be idle."
            + "\n        [DEFAULT VALUE : N/A]"
            + "\n        [DEFAULT CONFIG : 300000]"
            + "\n    -statstimeout"
            + "\n        [OPTIONAL] The amount of time before a criteria instance is allowed to be idle."
            + "\n        [DEFAULT VALUE : N/A]"
            + "\n        [DEFAULT CONFIG : 300000]"
            + "\n    /show"
            + "\n        [OPTIONAL] Print the configuration after modification are made."
            + "\n        To see the current configuration, do not provide any parameters to cconfig. ";

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

            //control port
            try {
                if (_args.containsKey("-port")) {
                    config.controlPort = Integer.parseInt(_args.get("-port"));
                }
            } catch (Exception ex) {
                throw new Exception("The argument '-port' must provide a numerical value.");
            }

            //interfaces
            ArrayList<PcapIf> interfaces = ToolsInterface.getInterfaces();
            if (_args.containsKey("-external")) {
                int ifExternalIdx = -1;
                try {
                    ifExternalIdx = Integer.parseInt(_args.get("-external"));
                } catch (Exception ex) {
                    throw new Exception("The argument '-external' must provide a numerical value.");
                }
                String ifExternalName = null;
                try {
                    ifExternalName = (ifExternalIdx < 0) ? config.ifExternal : interfaces.get(ifExternalIdx).getName();
                } catch (Exception ex) {
                    throw new Exception("Interface index '" + ifExternalIdx + "' is invalid.");
                }
                config.ifExternal = ifExternalName;
            }
            if (_args.containsKey("-internal")) {
                int ifInternalIdx = -1;
                try {
                    ifInternalIdx = Integer.parseInt(_args.get("-internal"));
                } catch (Exception ex) {
                    throw new Exception("The argument '-internal' must provide a numerical value.");
                }
                String ifInternalName = null;
                try {
                    ifInternalName = (ifInternalIdx < 0) ? config.ifInternal : interfaces.get(ifInternalIdx).getName();
                } catch (Exception ex) {
                    throw new Exception("Interface index '" + ifInternalIdx + "' is invalid.");
                }
                config.ifInternal = ifInternalName;
            }

            //state timout
            try {
                if (_args.containsKey("-statetimeout")) {
                    config.stateTimeoutMs = Integer.parseInt(_args.get("-statetimeout"));
                }
            } catch (Exception ex) {
                throw new Exception("The argument '-statetimeout' must provide a numerical value.");
            }

            //stats timeout
            try {
                if (_args.containsKey("-statstimeout")) {
                    config.statsTimeoutMs = Integer.parseInt(_args.get("-statstimeout"));
                }
            } catch (Exception ex) {
                throw new Exception("The argument '-statstimeout' must provide a numerical value.");
            }

            //show
            boolean show = false;
            if (_args.containsKey("/show")) {
                show = Boolean.parseBoolean(_args.get("/show"));
            }
            if(show) {
                System.out.println("CHIMERA Configuration:");
                System.out.println("    config.controlPort....." + config.controlPort);
                System.out.println("    config.ifExternal......" + config.ifExternal);
                System.out.println("    config.ifInternal......" + config.ifInternal);
                System.out.println("    config.stateTimeoutMs.." + config.stateTimeoutMs);
                System.out.println("    config.statsTimeoutMs.." + config.statsTimeoutMs);
            }

            //save config
            Config.saveConfig(config);

        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.out.println("Type 'cconfig /help' to see usage.");
            return;
        }
    }
}
