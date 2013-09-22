/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.admin.messages;

import ph.edu.dlsu.chimera.client.admin.messages.ClientShellMessage;
import ph.edu.dlsu.chimera.client.admin.messages.MessageText;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.admin.Session;
import ph.edu.dlsu.chimera.server.deployment.Gathering;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class MessageDeployGathering extends MessageDeploy {

    public final String interfaceInbound;

    public MessageDeployGathering(String ifInbound) {
        this.interfaceInbound = ifInbound;
    }

    @Override
    public ClientShellMessage handleMessage(Session session, Assembly assembly) throws Exception {
        StringBuilder report = new StringBuilder(((MessageText)(super.handleMessage(session, assembly))).text);
        assembly.setDeployment(new Gathering(assembly, this.interfaceInbound));
        report = report.append("\nDeployment: '").append(assembly.getDeployment().getName()).append("', is starting!");
        return new MessageText(report.toString());
    }

}
