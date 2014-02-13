/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.util;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import ph.edu.dlsu.chimera.core.ReturnParameter;
import ph.edu.dlsu.chimera.core.tools.Transceiver;
import ph.edu.dlsu.chimera.messages.Response;
import ph.edu.dlsu.chimera.messages.MessageFinished;
import ph.edu.dlsu.chimera.messages.Command;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class UtilsCommand {

    public static Object send(int serverport, Command command) throws Exception {
        //connect
        Socket client;
        try {
            client = new Socket("localhost", serverport);
        } catch (IOException ex) {
            throw new Exception("No deployment detected.");
        }
        Transceiver transc = new Transceiver(client);

        //start communication
        if (client.isConnected()) {
            //send command
            transc.send(command);

            //receive / send loop
            ReturnParameter rp = new ReturnParameter();
            while (true) {
                Response resp = (Response) transc.receive();
                Command handle = resp.handleResponse(rp);
                transc.send(handle);
                if (handle instanceof MessageFinished) {
                    return rp.getReturnedObject();
                }
            }
        } else {
            throw new Exception("No deployment detected.");
        }
    }
}
