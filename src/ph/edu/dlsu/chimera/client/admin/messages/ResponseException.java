package ph.edu.dlsu.chimera.client.admin.messages;

import java.io.PrintStream;
import ph.edu.dlsu.chimera.core.admin.messages.MessageFinished;
import ph.edu.dlsu.chimera.server.admin.messages.Command;

/**
 * An instance of this class constitutes a message reporting an exceptions.
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ResponseException implements Response {

    /**
     * The Exception carried by this ResponseException object.
     */
    public final Exception exceptions;

    /**
     * Constructs a new ResponseException object.
     * @param exceptions - the content of the new MessageObject to be created.
     */
    public ResponseException(Exception exception) {
        this.exceptions = exception;
    }

    /**
     * Allows a shell client program to handle a Message object.
     * @param outStream - the output stream where to print reports.
     * @return the appropriate response Command object.
     */
    public Command handleShellMessage(PrintStream outStream) {
        this.exceptions.printStackTrace();
        outStream.println(this.exceptions.getMessage());
        return new MessageFinished();
    }

}
