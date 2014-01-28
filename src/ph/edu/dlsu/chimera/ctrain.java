/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera;

import java.util.HashMap;
import ph.edu.dlsu.chimera.core.TrainingResult;
import ph.edu.dlsu.chimera.util.UtilsParse;
import ph.edu.dlsu.chimera.util.UtilsPrinting;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ctrain {

    public static final String USAGE = ""
            + "\nCHIMERA Training Command Usage (ctrain)"
            + "\nDescription:"
            + "\n    The ctrain command starts the CHIMERA's model building phase."
            + "\n    The 'normal' model used in the Production Phase is built in this phase."
            + "\n    This phase produces the said model using the training set captured during Data Gathering."
            + "\n    The model produced is stored on a .cmodel file."
            + "\nSyntax:"
            + "\n    ctrain (((-<varname> <value>) | (/<flag>))[ ])*(((-<varname> <value>) | (/<flag>)))"
            + "\nParameters"
            + "\n    -input"
            + "\n        DESCRIPTION"
            + "\n            The input file name of the training set file."
            + "\n            Automatically ends with '.ctset'."
            + "\n        REQUIRED........ Yes"
            + "\n    -output"
            + "\n        DESCRIPTION"
            + "\n            The output file name of the model to be produced."
            + "\n            Automatically ends with '.cmodel'."
            + "\n        REQUIRED........ Yes"
            + "\n    -filter"
            + "\n        DESCRIPTION"
            + "\n            Attribute filter regular expression."
            + "\n            May be used to exclude certain attributesd from the training set."
            + "\n            If provided, the following apply:"
            + "\n                If the /exclude flag is set, the following apply:"
            + "\n                    Matching attributes are excluded."
            + "\n                    Non matching attributes are included."
            + "\n                If the /exclude flag is not set, the following apply:"
            + "\n                    Matching attributes are not included."
            + "\n                    Non matching attrbitues are excluded."
            + "\n            If not provided, the following apply:"
            + "\n                If the /exclude flag is set, the following apply:"
            + "\n                    All attributes are excluded."
            + "\n                If the /exclude flag is not set, the following apply:"
            + "\n                    All attributes are included."
            + "\n        REQUIRED........ No"
            + "\n        DEFAULT VALUE... N/A"
            + "\n    /exclude"
            + "\n        DESCRIPTION"
            + "\n            If set, the following apply:"
            + "\n                If -filter is provided, the following apply:"
            + "\n                    Attributes matching -filter are excluded."
            + "\n                    Attributes not matching -filter are included."
            + "\n                If -filter is not provided, the following apply:"
            + "\n                    All attributes are excluded."
            + "\n            If not set, the following apply:"
            + "\n                If -filter is provided, the following apply:"
            + "\n                    Attributes matching -filter are included."
            + "\n                    Attributes not matching -filter are excluded."
            + "\n                If -filter is not provided, the following apply:"
            + "\n                    All attributes are included."
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

            //load training file
            String trainingFile = _args.get("-input");

            //load model file
            String modelFile = _args.get("-output");

            //filter
            String filter = _args.get("-filter");

            //exclude
            boolean exclude = false;
            if (_args.containsKey("/exclude")) {
                exclude = Boolean.parseBoolean(_args.get("/exclude"));
            }

            //execute
            TrainingResult result = Chimera.ctrain(trainingFile, modelFile, filter, exclude);

            //print results
            if (verbose) {
                UtilsPrinting.printTrainingResult(result);
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.out.println("Type 'ctrain /help' to see usage.");
        }
    }
}
