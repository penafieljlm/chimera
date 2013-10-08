/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.client.admin.messages;

import java.io.PrintStream;
import java.util.ArrayList;
import ph.edu.dlsu.chimera.core.NICData;
import ph.edu.dlsu.chimera.core.NICDataAddress;
import ph.edu.dlsu.chimera.core.admin.messages.MessageFinished;
import ph.edu.dlsu.chimera.server.admin.messages.ServerMessage;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class MessageInterfaces implements ClientShellMessage {

    public final ArrayList<NICData> interfaces;

    public MessageInterfaces(ArrayList<NICData> interfaces) {
        this.interfaces = interfaces;
    }

    /**
     * Allows a shell client program to handle a Message object.
     * @param outStream - the output stream where to print reports.
     * @return the appropriate response ServerMessage object.
     */
    public ServerMessage handleShellMessage(PrintStream outStream) {
        outStream.println("CHIMERA Network Interfaces:");
        int intctr = 0;
        for(NICData nic : this.interfaces) {
            outStream.println("\nInterface................ " + ++intctr);
            outStream.println("    Name................. " + nic.name);
            outStream.println("    Description.......... " + nic.description);
            outStream.println("    Hardware Address..... " + nic.hardwareAddress);
            int addrctr = 0;
            for(NICDataAddress addr : nic.addresses) {
                outStream.println("        Interface Address " + ++addrctr);
                outStream.println("            IP Address... " + addr.address);
                outStream.println("            Subnet Mask.. " + addr.netmask);
            }
        }
        outStream.println();
        return new MessageFinished();
    }

}
