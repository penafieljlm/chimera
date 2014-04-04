package ph.edu.dlsu.chimera.messages;

import java.util.HashMap;
import ph.edu.dlsu.chimera.components.Component;
import ph.edu.dlsu.chimera.core.ReturnParameter;
import ph.edu.dlsu.chimera.core.Session;

/**
 * An instance of this class constitutes a signal which will not be sent.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class MessageBlank implements Response, Command {

    /**
     * Constructs a new MessageBlank object.
     */
    public MessageBlank() {
    }

    @Override
    public Command handleResponse(ReturnParameter returned) {
        return this;
    }

    @Override
    public Response handleCommand(Session session, HashMap<String, Component> assembly) throws Exception {
        return this;
    }
}
