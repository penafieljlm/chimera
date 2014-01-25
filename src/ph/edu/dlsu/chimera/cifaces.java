/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera;

import java.util.ArrayList;
import org.jnetpcap.PcapIf;
import ph.edu.dlsu.chimera.core.nic.NicData;
import ph.edu.dlsu.chimera.core.nic.NicDataAddress;
import ph.edu.dlsu.chimera.util.UtilsPcap;
import ph.edu.dlsu.chimera.util.UtilsPrinting;

/**
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
