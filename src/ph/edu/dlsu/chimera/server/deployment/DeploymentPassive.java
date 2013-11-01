/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment;

import com.gremwell.jnetbridge.PcapPort;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentBridge;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentInjector;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentSniffer;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class DeploymentPassive extends Deployment {

    private final PcapPort externalBridgePcapPort;
    private final PcapPort internalBridgePcapPort;

    public DeploymentPassive(Assembly assembly) throws Exception {
        super("Passive");

        //packet capture objects
        try {
            this.externalBridgePcapPort = new PcapPort(assembly.getConfig().ifExternal);
        } catch (Exception ex) {
            throw new Exception("External interface cannot be opened.");
        }
        try {
            this.internalBridgePcapPort = new PcapPort(assembly.getConfig().ifInternal);
        } catch (Exception ex) {
            throw new Exception("Internal interface cannot be opened.");
        }

        //components
        super.addComponent("ex.bridge", new ComponentBridge(assembly, this.externalBridgePcapPort, this.internalBridgePcapPort));
        super.addComponent("in.bridge", new ComponentBridge(assembly, this.externalBridgePcapPort, this.internalBridgePcapPort));
    }

    @Override
    public void startDeployment() {
        super.startDeployment();
        this.externalBridgePcapPort.start();
        this.internalBridgePcapPort.start();
    }

    @Override
    public synchronized void killDeployment() {
        super.killDeployment();
        this.externalBridgePcapPort.stop();
        this.internalBridgePcapPort.stop();
    }
}
