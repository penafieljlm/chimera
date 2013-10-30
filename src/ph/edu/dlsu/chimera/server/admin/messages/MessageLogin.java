/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server.admin.messages;

import ph.edu.dlsu.chimera.client.admin.messages.ClientShellMessage;
import ph.edu.dlsu.chimera.client.admin.messages.MessageText;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.admin.Session;

/**
 * An instance of this class constitutes a message which establishes a session and delivers user credentials.
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class MessageLogin implements ServerMessage {

    /**
     * Username.
     */
    public final String username;

    /**
     * Password, MD5 hash.
     */
    public final String passwordHash;

    /**
     * Constructs a new MessageLogin object.
     * @param username - username.
     * @param passwordHash - password MD5 hash.
     */
    public MessageLogin(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    /**
     * Allows a server program to handle a Message object.
     * @param session - the session this Message object belongs to.
     * @param assembly - contains the components of the server.
     * @return the appropriate response.
     * @throws Exception
     */
    public ClientShellMessage handleMessage(Session session, Assembly assembly) throws Exception {
        if(assembly.users.lookup(this.username, this.passwordHash))
            return new MessageText("Authentication Successful! Username: " + this.username);
        else
            throw new Exception("Authetication Failed! Username and Password do not match!");
    }

}
