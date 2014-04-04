package ph.edu.dlsu.chimera.messages;

import java.util.HashMap;
import ph.edu.dlsu.chimera.components.Component;
import ph.edu.dlsu.chimera.core.ReturnParameter;
import ph.edu.dlsu.chimera.core.Session;

/**
 * An instance of this class constitutes a signal meaning that a host is
 * finished communicating with its partner.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class MessageFinished implements Response, Command {

    /**
     * Constructs a new MessageFinished object.
     */
    public MessageFinished() {
    }

    @Override
    public Command handleResponse(ReturnParameter returned) {
        return new MessageBlank();
    }

    @Override
    public Response handleCommand(Session session, HashMap<String, Component> assembly) throws Exception {
        return new MessageBlank();
    }
}
