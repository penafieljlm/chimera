/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.client.admin.messages;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import ph.edu.dlsu.chimera.core.admin.messages.MessageFinished;
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
        if (this.diagnostics == null) {
            outStream.println("The component: '" + this.componentName + "' was not found or is not active!");
        }
        outStream.println("Diagnostics: " + this.componentName);
        this.handleDiagsShell(outStream, this.diagnostics, "    ");
        return new MessageFinished();
    }

    private void handleDiagsShell(PrintStream outStream, List diags, String prefix) {
        int maxNameLen = 0;
        for (Object o : diags) {
            if (o instanceof Diagnostic) {
                Diagnostic diag = (Diagnostic) o;
                if (diag.getName().length() > maxNameLen) {
                    maxNameLen = diag.getName().length();
                }
            }
        }
        maxNameLen += 2;
        for (Object o : diags) {
            if (o instanceof Diagnostic) {
                Diagnostic diag = (Diagnostic) o;
                if (diag.getValue() instanceof List) {
                    outStream.println(prefix + diag.getName());
                    this.handleDiagsShell(outStream, (List<Diagnostic>) diag.getValue(), prefix + "    ");
                } else {
                    int dotCount = maxNameLen - diag.getName().length();
                    String dots = "";
                    for (int i = 0; i < dotCount; i++) {
                        dots += ".";
                    }
                    outStream.println(prefix + diag.getName() + dots + " " + diag.getValue());
                }
            }
        }
    }
}
