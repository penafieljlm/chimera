/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.admin.messages;

import ph.edu.dlsu.chimera.client.admin.messages.ClientShellMessage;
import ph.edu.dlsu.chimera.client.admin.messages.MessageText;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.admin.Session;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class MessageDeploy implements ServerMessage {

    public MessageDeploy() {
    }

    public ClientShellMessage handleMessage(Session session, Assembly assembly) throws Exception {
        StringBuilder report = new StringBuilder();
        if(assembly.getDeployment() != null)
            report = report.append("Deployment: '").append(assembly.getDeployment().name).append("', has been aborted!");
        else
            report = report.append("No deployment to abort!");
        return new MessageText(report.toString());
    }

}
