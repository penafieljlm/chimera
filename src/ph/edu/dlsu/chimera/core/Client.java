/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.core;

import java.io.PrintStream;
import java.net.Socket;
import ph.edu.dlsu.chimera.messages.Response;
import ph.edu.dlsu.chimera.messages.MessageFinished;
import ph.edu.dlsu.chimera.messages.Command;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class Client implements Runnable {

    public final int assemblyPort;
    public final PrintStream outStream;
    public final Command command;

    public Client(int serverport, PrintStream outStream, Command command) {
        this.assemblyPort = serverport;
        this.outStream = outStream;
        this.command = command;
    }

    @Override
    public void run() {
        try {
            //trigger event: connecting
            this.eventConnecting();
            //connect
            Socket client = new Socket("localhost", this.assemblyPort);
            Transceiver transc = new Transceiver(client);

            //start communication
            if (client.isConnected()) {
                //trigger event: connect success
                this.eventConnectSuccess();

                //send command
                transc.send(this.command);

                //receive / send loop
                while (true) {
                    Response resp = (Response) transc.receive();
                    Command handle = resp.handleShellMessage(this.outStream);
                    transc.send(handle);
                    if (handle instanceof MessageFinished) {
                        this.eventFinished();
                        return;
                    }
                }
            } else {
                this.eventConnectFailed();
            }
        } catch (Exception ex) {
            this.eventError(ex);
        }
    }

    public void eventConnecting() {
    }

    public void eventConnectSuccess() {
    }

    public void eventConnectFailed() {
    }

    public void eventFinished() {
    }

    public void eventError(Exception ex) {
    }
}
