package ph.edu.dlsu.chimera.server.admin;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import ph.edu.dlsu.chimera.client.admin.messages.Response;
import ph.edu.dlsu.chimera.client.admin.messages.ResponseException;
import ph.edu.dlsu.chimera.core.Transceiver;
import ph.edu.dlsu.chimera.core.admin.messages.MessageBlank;
import ph.edu.dlsu.chimera.server.admin.messages.Command;
import ph.edu.dlsu.chimera.server.Assembly;

/**
 * An instance of this class constitutes a session between a client and the server.
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class Session extends Thread {

    /**
     * The Transceiver which handles sending and receiving from the Socket given in the constructor.
     */
    private final Transceiver transceiver;
    /**
     * The assembly which the parent AdministrativeModule is a member of.
     */
    private final Assembly assembly;

    /**
     * Constructs a new Session object.
     * @param client - an incoming connection to be handled.
     */
    public Session(Socket client, Assembly assembly) {
        this.transceiver = new Transceiver(client);
        this.assembly = assembly;
    }

    /**
     * Handles received connections.
     */
    @Override
    public void run() {
        while (this.reply(this.handle(this.receive()))) {
        }
    }

    /**
     * Performs a blocking receive.
     * Logs all Transceiver exceptions.
     * @return the received Message object.
     */
    private Command receive() {
        Command recv = null;
        try {
            recv = (Command) this.transceiver.receive();
        } catch (IOException ex) {
            Logger.getLogger(Session.class.getName()).log(Level.WARNING, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Session.class.getName()).log(Level.WARNING, null, ex);
        }
        return recv;
    }

    /**
     * Handles received Message object.
     * Handles exceptions on the handling of the said object.
     * @param received - the received Message object.
     * @return the reply of the handling process.
     */
    private Response handle(Command received) {
        if (received == null) {
            return null;
        }
        try {
            return received.handleMessage(this, this.assembly);
        } catch (Exception ex) {
            return new ResponseException(ex);
        }
    }

    /**
     * Sends the reply Message object.
     * @param reply - the reply Message object.
     * @return true to continue to listen to message, false to terminate session
     */
    private boolean reply(Response reply) {
        if (reply == null) {
            return false;
        }
        if (reply instanceof MessageBlank) {
            return true;
        }
        try {
            this.transceiver.send(reply);
        } catch (IOException ex) {
            Logger.getLogger(Session.class.getName()).log(Level.WARNING, null, ex);
        }
        return true;
    }
}
