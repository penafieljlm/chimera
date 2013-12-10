package ph.edu.dlsu.chimera.messages;

import java.util.HashMap;
import ph.edu.dlsu.chimera.messages.Response;
import ph.edu.dlsu.chimera.components.Component;
import ph.edu.dlsu.chimera.core.Session;

public class CommandQuit implements Command {

    public CommandQuit() {
    }

    public Response handleMessage(Session session, HashMap<String, Component> assembly) throws Exception {
        return null;
    }
}
