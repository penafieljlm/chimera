/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.client.admin.convert;

import java.util.HashMap;
import ph.edu.dlsu.chimera.client.admin.Command;
import ph.edu.dlsu.chimera.core.admin.messages.Message;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class CommandConverter {

    protected final HashMap<String, String> params;
    public final String description;

    public CommandConverter(String description) {
        this.params = new HashMap<String, String>();
        this.description = description;
    }

    public abstract Message toMessage(Command command) throws Exception;

    public HashMap<String, String> getParameters() {
        return this.params;
    }

}
