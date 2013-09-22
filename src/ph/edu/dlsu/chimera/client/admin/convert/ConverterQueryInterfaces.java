package ph.edu.dlsu.chimera.client.admin.convert;

import ph.edu.dlsu.chimera.client.admin.Command;
import ph.edu.dlsu.chimera.admin.messages.Message;
import ph.edu.dlsu.chimera.server.admin.messages.MessageQueryInterfaces;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ConverterQueryInterfaces extends CommandConverter {

    public ConverterQueryInterfaces() {
        super("Retrieves information about CHIMERA's network interfaces.");
    }

    @Override
    public Message toMessage(Command command) throws Exception {
        if(command.getKeys().isEmpty())
            return new MessageQueryInterfaces();
        else
            throw new Exception("The command has no parameters!");
    }

}
