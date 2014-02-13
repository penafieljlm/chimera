package ph.edu.dlsu.chimera.messages;

import java.util.HashMap;
import ph.edu.dlsu.chimera.Chimera;
import ph.edu.dlsu.chimera.components.Component;
import ph.edu.dlsu.chimera.components.ComponentActive;
import ph.edu.dlsu.chimera.core.Session;

public class CommandQuit implements Command {

    public CommandQuit() {
    }

    @Override
    public Response handleCommand(Session session, HashMap<String, Component> assembly) throws Exception {
        Chimera.cquit(assembly);
        return new ResponseQuit();
    }
}
