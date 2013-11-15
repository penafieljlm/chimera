/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.messages;

import java.util.HashMap;
import ph.edu.dlsu.chimera.messages.Response;
import ph.edu.dlsu.chimera.messages.ResponseDiagnose;
import ph.edu.dlsu.chimera.components.Component;
import ph.edu.dlsu.chimera.core.Session;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class CommandDiagnose implements Command {

    public final String component;

    public CommandDiagnose(String component) {
        this.component = component;
    }

    public Response handleMessage(Session session, HashMap<String, Component> assembly) throws Exception {
        if (assembly == null) {
            throw new Exception("A deployment phase is not being executed!");
        }
        return new ResponseDiagnose(this.component, assembly.get(this.component).getDiagnostics());
    }
}
