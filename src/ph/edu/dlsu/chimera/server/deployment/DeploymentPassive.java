/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment;

import com.gremwell.jnetbridge.PcapPort;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentBridge;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentInjector;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentSniffer;
import ph.edu.dlsu.chimera.server.deployment.components.data.IntermodulePipe;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class DeploymentPassive extends Deployment {

    public DeploymentPassive(Assembly assembly) throws Exception {
        super("Passive");
        
        //packet capture objects
        PcapPort externalBridgePcapPort = null;
        PcapPort internalBridgePcapPort = null;
        try {
            externalBridgePcapPort = new PcapPort(assembly.getConfig().ifExternal);
        } catch (Exception ex) {
            throw new Exception("External interface cannot be opened.");
        }
        try {
            internalBridgePcapPort = new PcapPort(assembly.getConfig().ifInternal);
        } catch (Exception ex) {
            throw new Exception("Internal interface cannot be opened.");
        }

//        IntermodulePipe exPipe = new IntermodulePipe();
//        IntermodulePipe inPipe = new IntermodulePipe();
//        super.addComponent("ex.sniff", new ComponentSniffer(assembly, externalBridgePcapPort, exPipe, true));
//        super.addComponent("ex.inject", new ComponentInjector(assembly, exPipe, internalBridgePcapPort));
//        super.addComponent("in.sniff", new ComponentSniffer(assembly, internalBridgePcapPort, inPipe, true));
//        super.addComponent("in.inject", new ComponentInjector(assembly, inPipe, externalBridgePcapPort));
        //components
//        super.addComponent("ex.bridge", new ComponentBridge(assembly, externalBridgePcapPort, internalBridgePcapPort));
//        super.addComponent("in.bridge", new ComponentBridge(assembly, internalBridgePcapPort, externalBridgePcapPort));
    }
}
