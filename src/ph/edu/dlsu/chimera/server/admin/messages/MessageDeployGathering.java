/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.admin.messages;

import ph.edu.dlsu.chimera.client.admin.messages.ClientShellMessage;
import ph.edu.dlsu.chimera.client.admin.messages.MessageText;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.admin.Session;
import ph.edu.dlsu.chimera.server.deployment.DeploymentGathering;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class MessageDeployGathering extends MessageDeploy {

    public final int ifExternal;
    public final int ifInternal;
    public final long statsTimeoutMs;
    public final long stateTimeoutMs;
    public final String dumpFileName;
    public final boolean gatherAttacks;

    public MessageDeployGathering(int ifExternal,
            int ifInternal,
            String dumpFileName,
            boolean gatherAttacks,
            long statsTimeoutMs,
            long stateTimeoutMs) {
        this.ifExternal = ifExternal;
        this.ifInternal = ifInternal;
        this.statsTimeoutMs = statsTimeoutMs;
        this.stateTimeoutMs = stateTimeoutMs;
        this.dumpFileName = dumpFileName;
        this.gatherAttacks = gatherAttacks;
    }

    @Override
    public ClientShellMessage handleMessage(Session session, Assembly assembly) throws Exception {
        StringBuilder report = new StringBuilder(((MessageText)(super.handleMessage(session, assembly))).text);
        assembly.setDeployment(new Gathering(assembly, this.interfaceInbound));
        report = report.append("\nDeployment: '").append(assembly.getDeployment().name).append("', is starting!");
        return new MessageText(report.toString());
    }
}
