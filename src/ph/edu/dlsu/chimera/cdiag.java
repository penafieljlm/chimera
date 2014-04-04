package ph.edu.dlsu.chimera;

import java.util.ArrayList;
import java.util.HashMap;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.util.UtilsParse;
import ph.edu.dlsu.chimera.util.UtilsPrinting;

/**
 * The cdiag class contains the entry point for the cdiag command.
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
                    System.out.println(USAGE);
                    return;
                }
            }

            //parse args
            HashMap<String, String> _args = UtilsParse.parseArgs(args);

            //get parameter
            String component = _args.get("-component");

            //execute
            ArrayList<Diagnostic> diag = Chimera.cdiag(component);

            if (diag == null) {
                throw new Exception("The component: '" + component + "' was not found or is not active!");
            }
            System.out.println("Diagnostics: " + component);
            UtilsPrinting.printDiagnostics(diag, "    ");

        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.out.println("Type 'cdiag /help' to see usage.");
        }
    }
}
