/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.admin.messages;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import ph.edu.dlsu.chimera.client.admin.messages.ClientShellMessage;
import ph.edu.dlsu.chimera.client.admin.messages.MessageText;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.admin.Session;
import ph.edu.dlsu.chimera.server.deployment.components.Component;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentActive;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class MessageQueryError implements ServerMessage {

    public final String component;
    public final boolean printStackTrace;
    public final boolean pollErrors;

    public MessageQueryError(String component, boolean printStackTrace, boolean pollErrors) {
        this.component = component;
        this.printStackTrace = printStackTrace;
        this.pollErrors = pollErrors;
    }

    public ClientShellMessage handleMessage(Session session, Assembly assembly) throws Exception {
        if (assembly.getDeployment() == null) {
            throw new Exception("A deployment phase is not being executed!");
        }
        Component comp = assembly.getDeployment().getComponent(this.component);
        if (comp instanceof ComponentActive) {
            ComponentActive active = (ComponentActive) comp;
            ArrayList<Exception> errors;
            if(this.pollErrors) {
                errors = active.pollErrors();
            } else {
                errors = new ArrayList<Exception>(active.errors);
            }
            StringBuilder errprint = new StringBuilder();
            for(Exception err : errors) {
                String errstr;
                if(this.printStackTrace) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    err.printStackTrace(pw);
                    errstr = sw.toString();
                } else {
                    errstr = err.getMessage();
                }
                errprint.append(errstr).append("\n");
            }
            return new MessageText(errprint.toString());
        } else {
            throw new Exception("Component '" + this.component + "' is not an Active Component!");
        }
    }
}
