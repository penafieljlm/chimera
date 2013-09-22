/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.client.admin.convert;

import ph.edu.dlsu.chimera.client.admin.Command;
import ph.edu.dlsu.chimera.client.admin.CommandHandlerLookup;
import ph.edu.dlsu.chimera.client.admin.messages.MessageText;
import ph.edu.dlsu.chimera.admin.messages.Message;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ConverterHelp extends CommandConverter {

    public final CommandHandlerLookup lookup;

    public ConverterHelp(CommandHandlerLookup lookup) {
        super("Displays all shell commands and their descriptions.");
        this.lookup = lookup;
    }

    public Message toMessage(Command command) throws Exception {
        if(command.getKeys().isEmpty()) {
            StringBuilder report = new StringBuilder();
            report = report.append("CHIMERA Shell Commands List:");
            for(String cmd : this.lookup.getCommandsList()) {
                report = report.append("\n    ").append(cmd);
                if(this.lookup.lookup(cmd).description != null)
                    report = report.append("\n        ").append(this.lookup.lookup(cmd).description);
                if(!this.lookup.lookup(cmd).getParameters().isEmpty())
                    report = report.append("\n        Parameters:");
                for(String param : this.lookup.lookup(cmd).getParameters().keySet()) {
                    report = report.append("\n            ").append(param).append(" - ").append(this.lookup.lookup(cmd).getParameters().get(param));
                }
            }
            return new MessageText(report.toString());
        }
        else
            throw new Exception("The command has no parameters!");
    }

}
