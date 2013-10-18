/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.util;

import java.util.List;
import ph.edu.dlsu.chimera.server.core.Connection;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;
import ph.edu.dlsu.chimera.server.core.Criteria;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class ToolsMachineLearning {

    public static String[] getInstance(PduAtomic pkt, List<Criteria> criterias) {
        String[] set = new String[0];
        Connection conn = pkt.getConnection();

        //field - protocol
        set = ToolsArray.append(set, ToolsPacket.getPacketProtocolName(pkt.packet));

        //field - destination ip
        set = ToolsArray.append(set, ToolsPacket.getIpAddress(pkt.packet, false).getHostAddress());

        //field - destination tcp port
        set = ToolsArray.append(set, "" + ToolsPacket.getTcpPort(pkt.packet, false));

        //field - destination udp port
        set = ToolsArray.append(set, "" + ToolsPacket.getUdpPort(pkt.packet, false));

        //field - connection inbound encounter interval
        set = ToolsArray.append(set, "" + conn.inboundLastEncounterDeltaNs());

        //field - connection outbound encounter interval
        set = ToolsArray.append(set, "" + conn.outboundLastEncounterDeltaNs());

        //field - connection inbound encounters
        set = ToolsArray.append(set, "" + conn.inboundEncounters());

        //field - connection outbound encounters
        set = ToolsArray.append(set, "" + conn.outboundEncounters());

        //field - connection inbound total size
        set = ToolsArray.append(set, "" + conn.inboundTotalSize());

        //field - connection outbound total size
        set = ToolsArray.append(set, "" + conn.outboundTotalSize());

        return set;
    }
}
