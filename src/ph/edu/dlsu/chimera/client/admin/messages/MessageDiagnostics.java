/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.client.admin.messages;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.client.admin.Client;
import ph.edu.dlsu.chimera.admin.messages.MessageFinished;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.server.admin.messages.ServerMessage;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class MessageDiagnostics implements ClientShellMessage {

    public final String componentName;
    public final ArrayList<Diagnostic> diagnostics;

    public MessageDiagnostics(String componentName, ArrayList<Diagnostic> diagnostics) {
        this.componentName = componentName;
        this.diagnostics = diagnostics;
    }

    /**
     * Allows a shell client program to handle a Message object.
     * @param outStream - the output stream where to print reports.
     * @return the appropriate response ServerMessage object.
     */
    public ServerMessage handleShellMessage(PrintStream outStream) {
        if(this.diagnostics == null)
            outStream.println("The component: '" + this.componentName + "' was not found or is not active!");
        outStream.println("Diagnostics: " + this.componentName);
        for(Diagnostic diag : this.diagnostics) {
            outStream.println("    " + diag.getName() + " : " + diag.getValue());
        }
        return new MessageFinished();
    }

}
