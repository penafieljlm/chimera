/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.client.admin.convert;

import ph.edu.dlsu.chimera.client.admin.Command;
import ph.edu.dlsu.chimera.core.admin.messages.Message;
import ph.edu.dlsu.chimera.server.admin.messages.MessageQueryDiagnostics;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ConverterQueryDiagnostics extends CommandConverter {

    public ConverterQueryDiagnostics() {
        super("Retrieves useful information about the specified component.");
        super.params.put("component", "Name of component to diagnose.");
    }

    public Message toMessage(Command command) throws Exception {
        if(command.getKeys().size() < 1)
            throw new Exception("The command needs 1 parameter: 'component'!");
        String component = command.getParameterValue("component");
        if(component == null)
            throw new Exception("The parameter: 'component' is missing!");
        return new MessageQueryDiagnostics(component);
    }
    
}
