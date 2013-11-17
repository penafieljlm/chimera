/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera;

import de.tbsol.iptablesjava.IpTables;
import de.tbsol.iptablesjava.rules.IpRule;
import de.tbsol.iptablesjava.rules.match.MatchModule;
import de.tbsol.iptablesjava.rules.match.ModTcp;
import java.util.ArrayList;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class cdeploy {

    public static void main(String[] args) throws Exception {
        IpTables filterIpTables = new IpTables("filter");
        filterIpTables.createChain("FORWARD");

        IpRule rule = new IpRule();
        rule.setProtocol(IpRule.IpProto.IPPROTO_ICMP);

        ModTcp tcp = new ModTcp();
        tcp.setDestinationPortStart(50);
        tcp.setDestinationPortEnd(58);
        tcp.setSourcePortStart(60);
        tcp.setSourcePortEnd(68);
        tcp.setInvertDestinationPort(true);
        ArrayList<MatchModule> modList = new ArrayList<>();
        modList.add(tcp);

        rule.setJump("ACCEPT");
        rule.setModules(modList);

        filterIpTables.appendEntry("FORWARD", rule);
        filterIpTables.commit();
        filterIpTables.free();
    }
}
