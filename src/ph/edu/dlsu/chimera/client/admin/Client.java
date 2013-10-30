/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.client.admin;

import java.io.PrintStream;
import java.net.Socket;
import java.security.MessageDigest;
import ph.edu.dlsu.chimera.client.admin.messages.ClientShellMessage;
import ph.edu.dlsu.chimera.client.admin.messages.MessageException;
import ph.edu.dlsu.chimera.core.Transceiver;
import ph.edu.dlsu.chimera.core.admin.messages.Message;
import ph.edu.dlsu.chimera.core.admin.messages.MessageFinished;
import ph.edu.dlsu.chimera.server.CADMS;
import ph.edu.dlsu.chimera.server.admin.messages.MessageLogin;
import ph.edu.dlsu.chimera.server.admin.messages.MessageLogout;
import ph.edu.dlsu.chimera.server.admin.messages.ServerMessage;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class Client extends Thread {

    public final String servername;
    public final int serverport;
    public final String username;
    public final String password;
    protected PrintStream outStream;

    public Client(String servername, int serverport, String username, String password, PrintStream outStream) {
        this.servername = servername;
        this.serverport = serverport;
        this.username = username;
        this.password = password;
        this.outStream = outStream;
    }

    @Override
    public void run() {
        try {
            String passwordHash = new String(MessageDigest.getInstance(CADMS.PASSWORD_HASH_ALGO).digest(password.getBytes()));

            //connect
            this.eventConnecting();
            //Socket client = SSLSocketFactory.getDefault().createSocket(serverName, serverPort);
            Socket client = new Socket(this.servername, this.serverport);
            Transceiver transc = new Transceiver(client);

            //login
            this.eventLogin();
            transc.send(new MessageLogin(this.username, passwordHash));
            ClientShellMessage loginresponse = (ClientShellMessage) transc.receive();
            transc.send(loginresponse.handleShellMessage(this.outStream));
            if(loginresponse instanceof MessageException)
                return;

            //read input
            this.eventReady();
            CommandHandlerLookup lookup = new CommandHandlerLookup();
            while(client.isConnected()) {
                //read command and convert to message
                ServerMessage command = null;
                try {
                    Command cmd = this.pollCommand();
                    Message convert = lookup.lookup(cmd.getCommand()).toMessage(cmd);
                    if(convert instanceof ClientShellMessage) {
                        ClientShellMessage local = (ClientShellMessage) convert;
                        local.handleShellMessage(this.outStream);
                        continue;
                    }
                    if(convert instanceof ServerMessage)
                        command = (ServerMessage) convert;
                }
                catch(Exception ex) {
                    this.eventError(ex);
                    continue;
                }

                //send command message
                transc.send(command);
                if(command instanceof MessageLogout) {
                    this.eventLoggedOut();
                    return;
                }

                //receive response
                while(true) {
                    ClientShellMessage resp = (ClientShellMessage) transc.receive();
                    ServerMessage handle = resp.handleShellMessage(this.outStream);
                    transc.send(handle);
                    if(handle instanceof MessageFinished)
                        break;
                }

            }
        } catch (Exception ex) {
            this.eventError(ex);
        }
    }

    /**
     * Polls a command from the user.
     * @return the result of the polling.
     */
    public abstract Command pollCommand();

    public abstract void eventConnecting();

    public abstract void eventLogin();

    public abstract void eventReady();

    public abstract void eventLoggedOut();

    public abstract void eventError(Exception ex);

}
