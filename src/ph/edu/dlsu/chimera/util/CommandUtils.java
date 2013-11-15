/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.util;

import java.io.PrintStream;
import java.net.Socket;
import ph.edu.dlsu.chimera.core.Transceiver;
import ph.edu.dlsu.chimera.messages.Response;
import ph.edu.dlsu.chimera.messages.MessageFinished;
import ph.edu.dlsu.chimera.messages.Command;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class CommandUtils {

    public static void send(int serverport, Command command, PrintStream out) throws Exception {
        //connect
        Socket client;
        try {
            client = new Socket("localhost", serverport);
        } catch (Exception ex) {
            throw new Exception("No deployment detected.");
        }
        Transceiver transc = new Transceiver(client);

        //start communication
        if (client.isConnected()) {
            //send command
            transc.send(command);

            //receive / send loop
            while (true) {
                Response resp = (Response) transc.receive();
                Command handle = resp.handleShellMessage(out);
                transc.send(handle);
                if (handle instanceof MessageFinished) {
                    return;
                }
            }
        } else {
            throw new Exception("No deployment detected.");
        }
    }
}
