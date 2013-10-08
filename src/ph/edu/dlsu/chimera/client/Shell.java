package ph.edu.dlsu.chimera.client;

import java.io.InputStreamReader;
import java.util.Scanner;
import org.jnetpcap.Pcap;
import ph.edu.dlsu.chimera.client.admin.Client;
import ph.edu.dlsu.chimera.client.admin.Command;

/**
 * Command line client program.
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class Shell extends Client {

    public static String USAGE = ""
            + "\nCHIMERA SHELL USAGE"
            + "\nCommand Line Format: chimera <hostname/ip> <port> <username> <password>"
            + "\nParameters:"
            + "\n    hostname/ip - the hostname or the ip address of the device running chimera"
            + "\n    port        - the port NUMBER where the chimera administrative module is running on"
            + "\n    username    - the username to use to log in"
            + "\n    password    - the password to use to log in";

    private final Scanner cmdIn;

    public Shell(String servername, int serverport, String username, String password) {
        super(servername, serverport, username, password, System.out);
        this.cmdIn = new Scanner(new InputStreamReader(System.in));
    }

    public static void main(String[] args) {
        //length check
        if (args.length != 4) {
            System.out.println(Shell.USAGE);
            return;
        }

        //parameter check
        int serverPort = 0;
        try {
            serverPort = Integer.parseInt(args[1]);
        } catch (Exception ex) {
            System.out.print(Shell.USAGE);
            return;
        }

        Shell sh = new Shell(args[0], serverPort, args[2], args[3]);
        sh.start();
    }

    @Override
    public Command pollCommand() {
        Command cmd = null;
        while(cmd == null) {
            try {
                System.out.print(super.username + "@" + super.servername + ":" + super.serverport + ">>> ");
                cmd = new Command(this.cmdIn.nextLine());
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
        return cmd;
    }

    @Override
    public void eventConnecting() {
        super.outStream.println("Connecting to " + this.servername + ":" + this.serverport + "...");
    }

    @Override
    public void eventLogin() {
        super.outStream.println("Logging in as " + this.username + "...");
    }

    @Override
    public void eventLoggedOut() {
        super.outStream.println("Successfuly logged out!");
    }

    @Override
    public void eventReady() {
        super.outStream.println("CHIMERA is Ready...");
        super.outStream.println("Hint: Type 'help' to show all commands and their descriptions.");
    }

    @Override
    public void eventError(Exception ex) {
        this.outStream.println(ex.getMessage());
    }
}
