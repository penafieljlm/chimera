package ph.edu.dlsu.chimera.messages;

import java.util.HashMap;
import ph.edu.dlsu.chimera.Chimera;
import ph.edu.dlsu.chimera.components.Component;
import ph.edu.dlsu.chimera.core.Session;

/**
 * An instance of this class constitutes a Command which stops the deployment of
 * the system.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class CommandQuit implements Command {

    /**
     * Constructs a new CommandQuit object
     */
    public CommandQuit() {
    }

    @Override
    public Response handleCommand(Session session, HashMap<String, Component> assembly) throws Exception {
        Chimera.cquit(assembly);
        return new ResponseQuit();
    }
}
