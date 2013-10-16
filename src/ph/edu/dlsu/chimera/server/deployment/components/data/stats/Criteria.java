/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data.stats;

import java.util.HashMap;
import org.jnetpcap.packet.JHeader;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.structure.JField;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class Criteria {

    private static HashMap<String, JHeader> headerLookup;
    //expression syntax:
    //<header 1>.<field 1> ... <header n>.<field n>
    public final String expression;
    //filter syntax:
    //<header 1>.<field 1><op 1><value 1> ... <header n>.<field n><op n><value n>
    //filter operators:
    // = - equal
    // ! - not equal
    // < - less than
    // > - greater than
    public final String filter;

    public Criteria(String expression, String filter) throws Exception {
        Criteria.initialize();
        Criteria.parseExpression(expression);
        this.expression = expression;
        this.filter = filter;
    }

    private static void parseExpression(String expression) throws Exception {
        String[] fields = expression.split(" ");
        for (String field : fields) {
            String[] subfields = field.split(".");
            if (subfields.length != 2) {
                throw new Exception("Parse Error: '" + field + "', does not follow expression syntax.");
            }
            String headerName = subfields[0];
            if (!Criteria.headerLookup.containsKey(headerName)) {
                throw new Exception("Parse Error: No such header '" + headerName + "'");
            }
            JHeader header = Criteria.headerLookup.get(headerName);
            String attributeName = subfields[1];
            JField attribute = null;
            for (JField attr : header.getFields()) {
                if (attributeName.equals(attr.getName())) {
                    attribute = attr;
                    break;
                }
            }
            if (attribute == null) {
                throw new Exception("Parse Error: Header '" + headerName + "' has no field '" + attributeName + "'");
            }
        }
    }

    public void parseFilter(String filter) throws Exception {
        
    }

    private static void initialize() {
        if (Criteria.headerLookup == null) {
            Criteria.headerLookup = new HashMap<String, JHeader>();
            Criteria.headerLookup.put("eth", new Ethernet());
            Criteria.headerLookup.put("ip4", new Ip4());
            Criteria.headerLookup.put("tcp", new Tcp());
            Criteria.headerLookup.put("udp", new Udp());
            Criteria.headerLookup.put("icmp", new Icmp());
        }
    }

    public CriteriaInstance createInstance(PduAtomic pkt) {
    }
}
