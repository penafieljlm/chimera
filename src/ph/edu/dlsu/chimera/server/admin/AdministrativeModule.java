package ph.edu.dlsu.chimera.server.admin;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import ph.edu.dlsu.chimera.server.Component;
import ph.edu.dlsu.chimera.server.Assembly;

/**
 * An instance of this class constitutes a module which listens for connections creates handlers which handle those connections.
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class AdministrativeModule extends Component {

    /**
     * The port which this AdministrativeModule is running on.
     */
    public final int port;

    /**
     * Constructs a new AdministrativeModule object.
     * @param assembly - the assembly which this component belongs to.
     * @param port - the port number to listen for connections.
     */
    public AdministrativeModule(Assembly assembly, int port) {
        super(assembly);
        this.port = port;
    }

    /**
     * Listens for incoming connections.
     */
    @Override
    public void componentRun() {
        try {
//            SSLServerSocket server = (SSLServerSocket) SSLServerSocketFactory.getDefault().createServerSocket(this.port);
            ServerSocket server = new ServerSocket(this.port);
            while(super.running) {
                Socket client = server.accept();
                Session session = new Session(client, this.assembly);
                session.start();
            }
        } catch (IOException ex) {
            Logger.getLogger(AdministrativeModule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
