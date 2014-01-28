/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera;

import java.util.HashMap;
import ph.edu.dlsu.chimera.monitors.PhaseMonitorGathering;
import ph.edu.dlsu.chimera.util.UtilsParse;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class cgather {

    public static final String USAGE = ""
            + "\nCHIMERA Gather Command Usage (cgather)"
            + "\nDescription:"
            + "\n    The cgather command starts the CHIMERA's Data Gathering phase."
            + "\n    The training set used in the Training Phase is compiled in this phase."
            + "\n    This phase produces the said training set using the traffic captured."
            + "\n    Training set produced is stored on a .csv file."
            + "\nSyntax:"
            + "\n    cgather (((-<varname> <value>) | (/<flag>))[ ])*(((-<varname> <value>) | (/<flag>)))"
            + "\nParameters"
            + "\n    -output"
            + "\n        DESCRIPTION"
            + "\n            The output file name of the training set to be produced."
            + "\n            Automatically ends with '.ctset'."
            + "\n        REQUIRED........ Yes"
            + "\n    -protected"
            + "\n        DESCRIPTION"
            + "\n            The name of the interface facing the protected network."
            + "\n            Refer to the output of the 'cifaces' command."
            + "\n        REQUIRED........ No"
            + "\n        DEFAULT VALUE... as specified in the 'chimera.config' file"
            + "\n    -access"
            + "\n            A CHIMERA-JNetPcap Packet Filter Expression."
            + "\n            If provided, the following apply:"
            + "\n                If the /allow flag is set, the following apply:"
            + "\n                    Matching packets are included in the training set."
            + "\n                    Non matching packets are excluded from the training set."
            + "\n                If the /allow flag is not set, the following apply:"
            + "\n                    Matching packets are excluded from the training set."
            + "\n                    Non matching packets are included in the training set."
            + "\n            If not provided, the following apply:"
            + "\n                If the /allow flag is set, the following apply:"
            + "\n                    All packets are included in the training set."
            + "\n                If the /allow flag is not set, the following apply:"
            + "\n                    All packets are excluded from the training set."
            + "\n        REQUIRED........ No"
            + "\n        DEFAULT VALUE... N/A"
            + "\n    /allow"
            + "\n        DESCRIPTION"
            + "\n            If set, the following apply:"
            + "\n                If -access is provided, the following apply:"
            + "\n                    Packets matching -access are included in the training set."
            + "\n                    Packets not matching -access are not included in the training set."
            + "\n                If -access is not provided, the following apply:"
            + "\n                    All packets are included in the training set."
            + "\n            If not set, the following apply:"
            + "\n                If -access is provided, the following apply:"
            + "\n                    Packets matching -access are not included in the training set."
            + "\n                    Packets not matching -access are included in the training set."
            + "\n                If -access is not provided, the following apply:"
            + "\n                    All packets are not included in the training set."
            + "\n        REQUIRED........ No"
            + "\n        DEFAULT VALUE... N/A"
            + "\n    -training"
            + "\n        DESCRIPTION"
            + "\n            A CHIMERA-JNetPcap Packet Filter Expression."
            + "\n            If provided, the following apply:"
            + "\n                If the /attack flag is set, the following apply:"
            + "\n                    Matching packets are flagged as attacks."
            + "\n                    Non matching packets are flagged as normal."
            + "\n                If the /attack flag is not set, the following apply:"
            + "\n                    Matching packets are flagged as normal."
            + "\n                    Non matching packets are flagged as attacks."
            + "\n            If not provided, the following apply:"
            + "\n                If the /attack flag is set, the following apply:"
            + "\n                    All packets are flagged as attacks."
            + "\n                If the /attack flag is not set, the following apply:"
            + "\n                    All packets are flagged as normal."
            + "\n        REQUIRED........ No"
            + "\n        DEFAULT VALUE... N/A"
            + "\n    /attack"
            + "\n        DESCRIPTION"
            + "\n            If set, the following apply:"
            + "\n                If -training is provided, the following apply:"
            + "\n                    Packets matching -training are flagged as attacks."
            + "\n                    Packets not matching -training are flagged as normal."
            + "\n                If -training is not provided, the following apply:"
            + "\n                    All packets are flagged as attacks."
            + "\n            If not set, the following apply:"
            + "\n                If -training is provided, the following apply:"
            + "\n                    Packets matching -training are flagged as normal."
            + "\n                    Packets not matching -training are flagged as attacks."
            + "\n                If -training is not provided, the following apply:"
            + "\n                    All packets are flagged as normal."
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

            //verbose
            boolean verbose = false;
            if (_args.containsKey("/verbose")) {
                verbose = Boolean.parseBoolean(_args.get("/verbose"));
            }
            if (!verbose) {
                System.out.close();
            }

            //output file
            String output = _args.get("-output");

            //interface
            String ifProtected = _args.get("-protected");

            //exclude filter
            String accessFilter = _args.get("-access");

            //gather access flag
            boolean allowFiltered = false;
            if (_args.containsKey("/allow")) {
                allowFiltered = Boolean.parseBoolean(_args.get("/allow"));
            }

            //filter
            String trainingFilter = _args.get("-training");

            //gather attacks flag
            boolean tagFilteredAsAttacks = false;
            if (_args.containsKey("/attack")) {
                tagFilteredAsAttacks = Boolean.parseBoolean(_args.get("/attack"));
            }

            //monitor
            PhaseMonitorGathering monitorGathering = (verbose) ? new PhaseMonitorGathering(200) {

                @Override
                protected void update() {
                    System.out.print("Training Data Instances Gathered: " + this.getInstancesGathered() + "\r");
                }
            } : new PhaseMonitorGathering(200) {

                @Override
                protected void update() {
                }
            };

            //execute
            Chimera.cgather(monitorGathering, output, ifProtected, accessFilter, allowFiltered, trainingFilter, tagFilteredAsAttacks);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.out.println("Type 'cgather /help' to see usage.");
        }
    }
}
