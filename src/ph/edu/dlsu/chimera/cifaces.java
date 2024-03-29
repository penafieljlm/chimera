package ph.edu.dlsu.chimera;

import ph.edu.dlsu.chimera.core.nic.NicData;
import ph.edu.dlsu.chimera.util.UtilsPrinting;

/**
 * The cifaces class contains the entry point for the cifaces command.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class cifaces {

    public static final String USAGE = ""
            + "\nCHIMERA Interfaces Command Usage (cifaces)"
            + "\nDescription:"
            + "\n    The cifaces command prints the configuration of all detected interfaces."
            + "\n    The index of an interface is indicated by its position in the output list."
            + "\nSyntax:"
            + "\n    cifaces";

    public static void main(String[] args) {
        try {
            //help
            if (args.length == 1) {
                if (args[0].equals("/help")) {
                    System.out.println(USAGE);
                    return;
                }
            }

            //execute
            NicData[] nics = Chimera.cifaces();

            //print
            UtilsPrinting.printInterfaces(nics);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.out.println("Type 'cifaces /help' to see usage.");
        }
    }
}
