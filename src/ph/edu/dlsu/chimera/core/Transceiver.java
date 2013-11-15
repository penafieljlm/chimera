package ph.edu.dlsu.chimera.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import ph.edu.dlsu.chimera.messages.Message;

/**
 * An instance of this class constitutes a transceiver component, which allows for exchange of Message objects between hosts.
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class Transceiver {

    /**
     * The Socket object that this Transceiver operates on.
     */
    private final Socket clientSocket;

    /**
     * Constructs a new Transceiver object which uses an existing socket.
     * @param clientSocket
     */
    public Transceiver(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    /**
     * Sends a Message object to the server application as specified in the constructor parameters.
     * @param data - the Message object to send.
     * @throws IOException
     */
    public void send(Message data) throws IOException {
        ObjectOutputStream objectOutStream = new ObjectOutputStream(this.clientSocket.getOutputStream());
        objectOutStream.writeObject(data);
    }

    /**
     * Performs a blocking receive.
     * Receives a Message object from the server application as specified in the constructor parameters.
     * @return if successful: the received Message object.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Message receive() throws IOException, ClassNotFoundException {
        ObjectInputStream objectInStream = new ObjectInputStream(this.clientSocket.getInputStream());
        return (Message) objectInStream.readObject();
    }

    /**
     * Closes the client socket.
     * @throws IOException
     */
    public void close() throws IOException {
        this.clientSocket.close();
    }
    
}
