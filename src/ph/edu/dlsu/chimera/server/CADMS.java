package ph.edu.dlsu.chimera.server;

import java.util.HashMap;
import ph.edu.dlsu.chimera.server.deployment.components.data.net.HTTPPacket;

/**
 * 
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class CADMS {

    public static String PASSWORD_HASH_ALGO = "MD5";

    public static String USAGE = ""
            + "\nCHIMERA APP-DOS MITIGATION SYSTEM USAGE"
            + "\nCommand Line Format: cadms <port>"
            + "\nParameters:"
            + "\n    port - the port NUMBER where the chimera administrative module will run on";

    public static void main(String[] args) {
        //create default port protocol map
        PortProtocolMap portProtocolMap = new PortProtocolMap();
        //create assembly
        int adminPortNum;
        try {
            adminPortNum = Integer.parseInt(args[0]);
        } catch(NumberFormatException ex) {
            System.out.print(CADMS.USAGE);
            return;
        }
        Assembly assembly = new Assembly(adminPortNum, portProtocolMap);
        //start admin service
        assembly.startAdmin();
    }

}
