package ph.edu.dlsu.chimera.components;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import ph.edu.dlsu.chimera.core.Session;

/**
 * An instance of this class constitutes a module which listens for connections
 * creates handlers which handle those connections.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ComponentController extends ComponentActive {

    /**
     * The assembly which the ComponentController handles
     */
    public final HashMap<String, Component> assembly;
    /**
     * The port which this ComponentController is running on
     */
    public final int port;

    /**
     * Constructs a new ComponentController object.
     *
     * @param assembly The assembly which this component belongs to.
     * @param port The port number to listen for connections.
     */
    public ComponentController(HashMap<String, Component> assembly, int port) {
        this.assembly = assembly;
        this.port = port;
    }

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
