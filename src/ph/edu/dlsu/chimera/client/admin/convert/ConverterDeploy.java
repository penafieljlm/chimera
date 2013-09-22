/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.client.admin.convert;

import ph.edu.dlsu.chimera.client.admin.Command;
import ph.edu.dlsu.chimera.admin.messages.Message;
import ph.edu.dlsu.chimera.server.admin.messages.MessageDeploy;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ConverterDeploy extends CommandConverter {

    public ConverterDeploy() {
        super("Aborts any deployment being executed by the system.");
    }

    public Message toMessage(Command command) throws Exception {
        if(command.getKeys().isEmpty())
            return new MessageDeploy();
        else
            throw new Exception("The command has no parameters!");
    }

}
