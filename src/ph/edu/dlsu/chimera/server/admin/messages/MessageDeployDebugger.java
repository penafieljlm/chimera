/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.admin.messages;

import ph.edu.dlsu.chimera.client.admin.messages.ClientShellMessage;
import ph.edu.dlsu.chimera.client.admin.messages.MessageText;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.admin.Session;
import ph.edu.dlsu.chimera.server.deployment.DeploymentDebug;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class MessageDeployDebugger extends MessageDeploy {

    public final String interfaceInbound;
    public final long statsTimeout;
    public final long stateTimeout;

    public MessageDeployDebugger(String ifInbound, long statsTimeout, long stateTimeout) {
        this.interfaceInbound = ifInbound;
        this.statsTimeout = statsTimeout;
        this.stateTimeout = stateTimeout;
    }

    @Override
    public ClientShellMessage handleMessage(Session session, Assembly assembly) throws Exception {
        StringBuilder report = new StringBuilder(((MessageText)(super.handleMessage(session, assembly))).text);
        assembly.setDeployment(new DeploymentDebug(assembly, this.interfaceInbound, assembly.criteriasAtomic, this.statsTimeout, this.stateTimeout));
        report = report.append("\nDeployment: '").append(assembly.getDeployment().name).append("', is starting!");
        return new MessageText(report.toString());
    }

}
