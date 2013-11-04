/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera;

import com.gremwell.jnetbridge.PcapPort;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.jnetpcap.PcapIf;
import ph.edu.dlsu.chimera.server.Config;
import ph.edu.dlsu.chimera.server.core.Criteria;
import ph.edu.dlsu.chimera.server.assembly.AssemblyGathering;
import ph.edu.dlsu.chimera.util.ToolsInterface;
import ph.edu.dlsu.chimera.util.ToolsParse;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class cgather {

    public static final String USAGE = ""
            + "\nCHIMERA Gather Command Usage"
            + "\nDescription:"
            + "\n    The cgather command starts the CHIMERA's data gathering phase."
            + "\n    The training set used in the Training Phase is compiled in this phase."
            + "\n    This phase produces the said training set using the traffic captured."
            + "\n    Training sets produced are stored on .csv files."
            + "\nSyntax:"
            + "\n    cgather (((-<varname> <value>) | (/<flag>))[ ])*(((-<varname> <value>) | (/<flag>)))"
            + "\nParameters"
            + "\n    -output"
            + "\n        [REQUIRED] The output file name of the training set to be produced."
            + "\n    /attacks"
            + "\n        [OPTIONAL] Mark the data in the produced training set as attacks."
            + "\n        Provide the value 'true' (case insensitive) to enable."
            + "\n        [DEFAULT VALUE : 'false']"
            + "\n    -external"
            + "\n        [OPTIONAL] The index of the external interface."
            + "\n        Refer to the output of the 'cifaces' command."
            + "\n        [DEFAULT VALUE : as specified in the 'chimera.config' file]"
            + "\n    -internal"
            + "\n        [OPTIONAL] The index of the internal interface."
            + "\n        Refer to the output of the 'cifaces' command."
            + "\n        [DEFAULT VALUE : as specified in the 'chimera.config' file]";

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

            //load criterias
            Criteria[] criterias = Criteria.loadCriterias();

            //parse args
            HashMap<String, String> _args = ToolsParse.parseArgs(args);

            //load interfaces
            int ifExternalIdx = -1;
            int ifInternalIdx = -1;
            try {
                if (_args.containsKey("-external")) {
                    ifExternalIdx = Integer.parseInt(_args.get("-external"));
                }
            } catch (Exception ex) {
                throw new Exception("The argument '-external' must provide a numerical value.");
            }
            try {
                if (_args.containsKey("-internal")) {
                    ifInternalIdx = Integer.parseInt(_args.get("-internal"));
                }
            } catch (Exception ex) {
                throw new Exception("The argument '-internal' must provide a numerical value.");
            }
            ArrayList<PcapIf> interfaces = ToolsInterface.getInterfaces();
            String ifExternalName = null;
            String ifInternalName = null;
            try {
                ifExternalName = (ifExternalIdx < 0) ? config.ifExternal : interfaces.get(ifExternalIdx).getName();
            } catch (Exception ex) {
                throw new Exception("Interface index '" + ifExternalIdx + "' is invalid.");
            }
            try {
                ifInternalName = (ifInternalIdx < 0) ? config.ifInternal : interfaces.get(ifInternalIdx).getName();
            } catch (Exception ex) {
                throw new Exception("Interface index '" + ifInternalIdx + "' is invalid.");
            }
            PcapPort ifExternalPort = null;
            PcapPort ifInternalPort = null;
            try {
                ifExternalPort = new PcapPort(ifExternalName);
            } catch (Exception ex) {
                throw new Exception("Cannot open external interface.");
            }
            try {
                ifInternalPort = new PcapPort(ifInternalName);
            } catch (Exception ex) {
                throw new Exception("Cannot open internal interface.");
            }

            //load dump file
            if (!_args.containsKey("-output")) {
                throw new Exception("The argument '-output' must be provided.");
            }
            File trainingDumpFile = new File(_args.get("-output"));

            //gather attacks flag
            boolean gatherAttacks = false;
            if (_args.containsKey("/attacks")) {
                gatherAttacks = Boolean.parseBoolean(_args.get("/attacks"));
            }

            //prepare assembly
            AssemblyGathering assembly = null;
            assembly = new AssemblyGathering(config.controlPort,
                    ifExternalPort,
                    ifInternalPort,
                    criterias,
                    trainingDumpFile,
                    gatherAttacks,
                    config.statsTimeoutMs,
                    config.stateTimeoutMs);
            assembly.run();

            //end
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.out.println("Type 'cgather /help' to see usage.");
            return;
        }
    }
}
