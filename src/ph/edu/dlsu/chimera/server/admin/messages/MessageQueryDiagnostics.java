/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.admin.messages;

import ph.edu.dlsu.chimera.client.admin.messages.ClientShellMessage;
import ph.edu.dlsu.chimera.client.admin.messages.MessageDiagnostics;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.admin.Session;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class MessageQueryDiagnostics implements ServerMessage {

    public final String component;

    public MessageQueryDiagnostics(String component) {
        this.component = component;
    }

    public ClientShellMessage handleMessage(Session session, Assembly assembly) throws Exception {
        if(assembly.getDeployment() == null)
            throw new Exception("A deployment phase is not being executed!");
        return new MessageDiagnostics(this.component, assembly.getDeployment().getComponent(this.component).getDiagnostics());
    }

}
