/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.messages;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import ph.edu.dlsu.chimera.messages.Response;
import ph.edu.dlsu.chimera.messages.ResponseText;
import ph.edu.dlsu.chimera.core.Session;
import ph.edu.dlsu.chimera.components.Component;
import ph.edu.dlsu.chimera.components.ComponentActive;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class CommandException implements Command {

    public final String component;
    public final boolean printStackTrace;
    public final boolean pollErrors;

    public CommandException(String component, boolean printStackTrace, boolean pollErrors) {
        this.component = component;
        this.printStackTrace = printStackTrace;
        this.pollErrors = pollErrors;
    }

    public Response handleCommand(Session session, HashMap<String, Component> assembly) throws Exception {
        if (assembly == null) {
            throw new Exception("A deployment phase is not being executed!");
        }
        Component comp = assembly.get(this.component);
        if (comp instanceof ComponentActive) {
            ComponentActive active = (ComponentActive) comp;
            ArrayList<Exception> errors;
            if (this.pollErrors) {
                errors = active.pollErrors();
            } else {
                errors = new ArrayList<Exception>(active.errors);
            }
            StringBuilder errprint = new StringBuilder();
            for (Exception err : errors) {
                String errstr;
                if (this.printStackTrace) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    err.printStackTrace(pw);
                    errstr = sw.toString();
                } else {
                    errstr = err.getMessage();
                }
                errprint.append(errstr).append("\n");
            }
            return new ResponseText(errprint.toString());
        } else {
            throw new Exception("Component '" + this.component + "' is not an Active Component!");
        }
    }
}
