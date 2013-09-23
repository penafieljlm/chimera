/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.client.admin.convert;

import ph.edu.dlsu.chimera.client.admin.Command;
import ph.edu.dlsu.chimera.core.admin.messages.Message;
import ph.edu.dlsu.chimera.server.admin.messages.MessageLogout;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ConverterLogout extends CommandConverter {

    public ConverterLogout() {
        super("Terminates the session and exits the shell program.");
    }

    public Message toMessage(Command command) throws Exception {
        if(command.getKeys().isEmpty())
            return new MessageLogout();
        else
            throw new Exception("The command has no parameters!");
    }

}
