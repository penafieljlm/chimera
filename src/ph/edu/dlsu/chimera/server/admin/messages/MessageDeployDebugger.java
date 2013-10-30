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

    public final int ifExternal;
    public final long statsTimeout;
    public final long stateTimeout;

    public MessageDeployDebugger(int ifExternal, long statsTimeout, long stateTimeout) {
        this.ifExternal = ifExternal;
        this.statsTimeout = statsTimeout;
        this.stateTimeout = stateTimeout;
    }

    @Override
    public ClientShellMessage handleMessage(Session session, Assembly assembly) throws Exception {
        StringBuilder report = new StringBuilder(((MessageText)(super.handleMessage(session, assembly))).text);
        assembly.setDeployment(new DeploymentDebug(assembly, this.ifExternal, this.statsTimeout, this.stateTimeout));
        report = report.append("\nDeployment: '").append(assembly.getDeployment().name).append("', is starting!");
        report = report.append("\nComponents: ");
        for(String c : assembly.getDeployment().getComponentNames()) {
            report = report.append("\n    - ").append(c);
        }
        return new MessageText(report.toString());
    }

}
