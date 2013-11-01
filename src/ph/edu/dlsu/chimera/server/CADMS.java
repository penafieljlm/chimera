package ph.edu.dlsu.chimera.server;

import java.text.ParseException;

/**
 * 
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class CADMS {

    public static String PASSWORD_HASH_ALGO = "MD5";
    public static String USAGE = ""
            + "\nCHIMERA APP-DOS MITIGATION SYSTEM USAGE"
            + "\nCommand Line Format:"
            + "\n    cadms [-port <integer>] [-external <integer>] [-internal <integer>]"
            + "\nParameters:"
            + "\n    -port"
            + "\n        The port NUMBER where the chimera administrative module will run on."
            + "\n    -external"
            + "\n        The index of the interface facing the external network."
            + "\n    -internal"
            + "\n        The index of the interface facing the internal network."
            + "\n";

    public static void main(String[] args) {
        //parse values
        int port = -1;
        int external = -1;
        int internal = -1;
        try {
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "-port":
                        if (port < 0) {
                            port = Integer.parseInt(args[i + 1]);
                        } else {
                            throw new IllegalStateException();
                        }
                        break;
                    case "-external":
                        if (external < 0) {
                            external = Integer.parseInt(args[i + 1]);
                        } else {
                            throw new IllegalStateException();
                        }
                        break;
                    case "-internal":
                        if (internal < 0) {
                            internal = Integer.parseInt(args[i + 1]);
                        } else {
                            throw new IllegalStateException();
                        }
                        break;
                }
            }
        } catch (Exception ex) {
            System.out.print(CADMS.USAGE);
            return;
        }

        Assembly assembly = null;
        try {
            assembly = new Assembly(port, external, internal);
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            System.out.print(CADMS.USAGE);
            //ex.printStackTrace();
            return;
        }
        //start admin service
        assembly.startAdmin();
    }
}
