/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.server.deployment.components.assembler.Assembler;
import ph.edu.dlsu.chimera.server.core.SocketPair;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ComponentAssemblerTable implements Component {

    public final ConcurrentHashMap<SocketPair, Assembler> assemblertable;

    public ComponentAssemblerTable(ConcurrentHashMap<SocketPair, Assembler> assemblertable) {
        this.assemblertable = assemblertable;
    }

    public ArrayList<Diagnostic> getDiagnostics() {
        //TODO: implement
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
