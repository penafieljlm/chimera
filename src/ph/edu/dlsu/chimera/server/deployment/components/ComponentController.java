package ph.edu.dlsu.chimera.server.deployment.components;

import java.net.ServerSocket;
import java.net.Socket;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.admin.Session;

/**
 * An instance of this class constitutes a module which listens for connections creates handlers which handle those connections.
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ComponentController extends ComponentActive {

    public final Assembly assembly;

    /**
     * The port which this ComponentController is running on.
     */
    public final int port;

    /**
     * Constructs a new ComponentController object.
     * @param assembly - the assembly which this component belongs to.
     * @param port - the port number to listen for connections.
     */
    public ComponentController(Assembly assembly, int port) {
        this.assembly = assembly;
        this.port = port;
    }

    /**
     * Listens for incoming connections.
     */
    @Override
    public void componentRun() throws Exception {
        ServerSocket server = new ServerSocket(this.port);
        while (super.running) {
            Socket client = server.accept();
            Session session = new Session(client, this.assembly);
            session.start();
        }
    }
}
