package ph.edu.dlsu.chimera.server.admin.messages;

import ph.edu.dlsu.chimera.client.admin.messages.Response;
import ph.edu.dlsu.chimera.server.admin.Session;
import ph.edu.dlsu.chimera.server.assembly.Assembly;

public class CommandQuit implements Command {

    public CommandQuit() {
    }

    public Response handleMessage(Session session, Assembly assembly) throws Exception {
        return null;
    }

}
