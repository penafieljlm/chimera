/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera;

import com.cedarsoftware.util.io.JsonWriter;
import java.util.HashMap;
import ph.edu.dlsu.chimera.core.logs.Log;
import ph.edu.dlsu.chimera.monitors.PhaseMonitorProduction;
import ph.edu.dlsu.chimera.util.UtilsParse;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class cproduce {

    public static final String USAGE = ""
            + "\nCHIMERA Produce Command Usage (ctrain)"
            + "\nDescription:"
            + "\n    The cproduce command starts the CHIMERA's Production phase."
            + "\n    The 'normal' model produced in the Training Phase is used in this phase."
            + "\n    This phase checks network traffic for possible Denial-of-Service attacks."
            + "\n    Upon the discovery of an attack, logs and rules can optionally be created."
            + "\nSyntax:"
            + "\n    cproduce (((-<varname> <value>) | (/<flag>))[ ])*(((-<varname> <value>) | (/<flag>)))"
            + "\nParameters"
            + "\n    -input"
            + "\n        DESCRIPTION"
            + "\n            The input file name of the model to be used."
            + "\n            Automatically ends with '.cmodel'."
            + "\n        REQUIRED........ Yes"
            + "\n    -syslog"
            + "\n        DESCRIPTION"
            + "\n            Hostname or IP Address of the Syslog server to send logs to."
            + "\n        REQUIRED........ No"
            + "\n        DEFAULT VALUE... N/A"
            + "\n    /active"
            + "\n        DESCRIPTION"
            + "\n            If set, the following apply:"
            + "\n                Linux kernel firewall rules are created to block detected DoS attacks."
            + "\n            If not set, the following apply:"
            + "\n                Linux kernel firewall rules are not created to block detected DoS attacks."
            + "\n        REQUIRED........ No"
            + "\n        DEFAULT VALUE... N/A"
            + "\n    /verbose"
            + "\n        DESCRIPTION"
            + "\n            If set, the following apply:"
            + "\n                Output messages are printed on the screen."
            + "\n            If not set, the following apply:"
            + "\n                Output messages are not printed on the screen."
            + "\n        REQUIRED........ No"
            + "\n        DEFAULT VALUE... N/A";

    public static void main(String[] args) throws Exception {
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

            //verbose
            boolean verbose = false;
            if (_args.containsKey("/verbose")) {
                verbose = Boolean.parseBoolean(_args.get("/verbose"));
            }
            if (!verbose) {
                System.out.close();
            }

            //load model file
            String modelFile = _args.get("-input");

            //syslog server
            String syslog = _args.get("-syslog");

            //gather access flag
            boolean active = false;
            if (_args.containsKey("/active")) {
                active = Boolean.parseBoolean(_args.get("/active"));
            }

            //monitor
            PhaseMonitorProduction monitorProduction = (verbose) ? new PhaseMonitorProduction(200) {

                @Override
                protected void update() {
                    while (!this.getLogs().isEmpty()) {
                        Log log = this.getLogs().poll();
                        System.out.println(JsonWriter.toJson(log));
                        System.out.println();
                    }
                }
            } : new PhaseMonitorProduction(200) {

                @Override
                protected void update() {
                    this.getLogs().clear();
                }
            };

            //execute
            Chimera.cproduce(monitorProduction, modelFile, syslog, active);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.out.println("Type 'cgather /help' to see usage.");
        }
    }
}
