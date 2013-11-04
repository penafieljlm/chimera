/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.admin.messages;

import ph.edu.dlsu.chimera.client.admin.messages.Response;
import ph.edu.dlsu.chimera.client.admin.messages.ResponseDiagnose;
import ph.edu.dlsu.chimera.server.admin.Session;
import ph.edu.dlsu.chimera.server.assembly.Assembly;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class CommandDiagnose implements Command {

    public final String component;

    public CommandDiagnose(String component) {
        this.component = component;
    }

    public Response handleMessage(Session session, Assembly assembly) throws Exception {
        if(assembly == null)
            throw new Exception("A deployment phase is not being executed!");
        return new ResponseDiagnose(this.component, assembly.getComponent(this.component).getDiagnostics());
    }

}
