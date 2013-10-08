/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.client.admin.convert;

import ph.edu.dlsu.chimera.server.admin.messages.MessageDeployDebugger;
import ph.edu.dlsu.chimera.client.admin.Command;
import ph.edu.dlsu.chimera.core.admin.messages.Message;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ConverterDeployDebugger extends CommandConverter {

    public ConverterDeployDebugger() {
        super("Starts the Debug deployment.");
        super.params.put("ifInbound", "Name of interface facing the external network.");
    }

    @Override
    public Message toMessage(Command command) throws Exception {
        if(command.getKeys().size() != 1)
            throw new Exception("The command needs 1 parameter: 'ifInbound'!");
        String component = command.getParameterValue("ifInbound");
        if(component == null)
            throw new Exception("The parameter: 'ifInbound' is missing!");
        return new MessageDeployDebugger(component);
    }

}
