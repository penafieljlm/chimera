/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera;

import org.jnetpcap.PcapIf;
import ph.edu.dlsu.chimera.core.NicData;
import ph.edu.dlsu.chimera.core.NicDataAddress;
import ph.edu.dlsu.chimera.util.ToolsInterface;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class DriverInterfaces {

    public static final String USAGE = ""
            + "\nCHIMERA Interfaces Command Usage"
            + "\nDescription:"
            + "\n    The cifaces command prints the configuration of all detected interfaces."
            + "\n    The index of an interface is indicated by ."
            + "\nSyntax:"
            + "\n    cifaces";

    public static void main(String[] args) {
        try {
            System.out.println("CHIMERA Network Interfaces:");
            int intctr = 0;
            for (PcapIf nic : ToolsInterface.getInterfaces()) {
                NicData _nic = new NicData(nic);
                System.out.println("Interface................ " + intctr++);
                System.out.println("    Name................. " + _nic.name);
                System.out.println("    Description.......... " + _nic.description);
                System.out.println("    Hardware Address..... " + _nic.hardwareAddress);
                int addrctr = 0;
                for (NicDataAddress addr : _nic.addresses) {
                    System.out.println("        Interface Address " + ++addrctr);
                    System.out.println("            IP Address... " + addr.address);
                    System.out.println("            Subnet Mask.. " + addr.netmask);
                }
            }
        } catch (Exception ex) {
            System.out.println();
            System.err.println(ex.getMessage());
            System.out.println(DriverInterfaces.USAGE);
            return;
        }
    }
}
