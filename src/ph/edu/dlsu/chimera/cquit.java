package ph.edu.dlsu.chimera;

/**
 * The cquit class contains the entry point for the cquit command.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class cquit {

    public static final String USAGE = ""
            + "\nCHIMERA Diagnose Command Usage (cdiag)"
            + "\nDescription:"
            + "\n    The cquit command stops any running phases."
            + "\n    The command will only work if there is an ongoing phase."
            + "\nSyntax:"
            + "\n    cquit";

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
            Chimera.cquit();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.out.println("Type 'cquit /help' to see usage.");
        }
    }
}
