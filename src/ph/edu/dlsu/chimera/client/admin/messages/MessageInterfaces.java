/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.client.admin.messages;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.client.admin.Client;
import ph.edu.dlsu.chimera.core.NICData;
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
        for(NICData nic : this.interfaces) {
            outStream.println("    " + nic.name);
            outStream.println("         IP Address: " + nic.netaddress);
            outStream.println("        Subnet Mask: " + nic.netmask);
        }
        return new MessageFinished();
    }

}
