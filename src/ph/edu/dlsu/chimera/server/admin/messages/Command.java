package ph.edu.dlsu.chimera.server.admin.messages;

import ph.edu.dlsu.chimera.client.admin.messages.Response;
import ph.edu.dlsu.chimera.core.admin.messages.Message;
import ph.edu.dlsu.chimera.server.admin.Session;
import ph.edu.dlsu.chimera.server.assembly.Assembly;

public interface Command extends Message {

    public Response handleMessage(Session session, Assembly assembly) throws Exception;
    
}
