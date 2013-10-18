/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data.stats;

import java.nio.ByteBuffer;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;
import ph.edu.dlsu.chimera.util.ToolsArray;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class Criteria {

    //subjectExpression syntax:
    //<header 1>.<field 1>, ... <header n>.<field n>
    public final String subjectExpression;
    //filterExpression syntax:
    //<header 1>.<field 1> <op 1> <value 1>, ... <header n>.<field n> <op n> <value n>
    //filterExpression operators:
    // == - equal
    // != - not equal
    // <= - less than or equal
    // >= - greater than or equal
    // < - less than
    // > - greater than
    public final String filterExpression;
    private final CriteriaSubject[] subjects;
    private final CriteriaFilter[] filters;

    public Criteria(String subjectExpression, String filterExpression) throws Exception {
        this.subjectExpression = subjectExpression;
        this.filterExpression = filterExpression;
        String[] subjs = this.subjectExpression.split(" ");
        this.subjects = new CriteriaSubject[subjs.length];
        for (int i = 0; i < subjs.length; i++) {
            String[] e = subjs[i].split(".");
            if (e.length != 2) {
                Criteria.errorExp(e);
            }
            this.subjects[i] = new CriteriaSubject(e[0], e[1]);
        }
        String[] filts = this.filterExpression.split(" ");
        if (filts.length % 3 != 0) {
            throw new Exception("Parse Error: Incomplete filter expression: '" + this.filterExpression + "'!");
        }
        this.filters = new CriteriaFilter[filts.length];
        for (int i = 0; i < filts.length; i += 3) {
            String[] e = filts[i].split(".");
            if (e.length != 2) {
                Criteria.errorExp(e);
            }
            this.filters[i] = new CriteriaFilter(e[0], e[1], filts[i + 1], filts[i + 2]);
        }
    }

    public CriteriaInstance createInstance(PduAtomic pkt) throws Exception {
        if (this.matchesFilter(pkt)) {
            return new CriteriaInstance(this.createInstanceId(pkt), this);
        } else {
            return null;
        }
    }

    private static void errorField(String header, String field) throws Exception {
        throw new Exception("Parse Error: No such field '" + header + "." + field + "'");
    }

    private static void errorExp(String[] exp) throws Exception {
        String x = (exp.length > 0) ? exp[0] : "";
        for (int i = 1; i < exp.length; i++) {
            x += exp[i];
        }
        throw new Exception("Parse Error: '" + x + "', does not follow expression syntax!");
    }

    private static byte[] accessField(PduAtomic pkt, String[] accessor) throws Exception {
        if (accessor.length != 2) {
            Criteria.errorExp(accessor);
        }
        switch (accessor[0]) {
            case "eth":
                Ethernet eth = pkt.packet.getHeader(new Ethernet());
                switch (accessor[1]) {
                    case "src":
                        return ToolsArray.reverse(eth.source());
                    case "dst":
                        return ToolsArray.reverse(eth.destination());
                    default:
                        Criteria.errorField(accessor[0], accessor[1]);
                }
                break;
            case "ip4":
                Ip4 ip4 = pkt.packet.getHeader(new Ip4());
                switch (accessor[1]) {
                    case "src":
                        return ToolsArray.reverse(ip4.source());
                    case "dst":
                        return ToolsArray.reverse(ip4.destination());
                    default:
                        Criteria.errorField(accessor[0], accessor[1]);
                }
                break;
            case "tcp":
                Tcp tcp = pkt.packet.getHeader(new Tcp());
                switch (accessor[1]) {
                    case "src":
                        return ByteBuffer.allocate(Integer.SIZE / Byte.SIZE).putInt(tcp.source()).array();
                    case "dst":
                        return ByteBuffer.allocate(Integer.SIZE / Byte.SIZE).putInt(tcp.destination()).array();
                    default:
                        Criteria.errorField(accessor[0], accessor[1]);
                }
                break;
            case "udp":
                Udp udp = pkt.packet.getHeader(new Udp());
                switch (accessor[1]) {
                    case "src":
                        return ByteBuffer.allocate(Integer.SIZE / Byte.SIZE).putInt(udp.source()).array();
                    case "dst":
                        return ByteBuffer.allocate(Integer.SIZE / Byte.SIZE).putInt(udp.source()).array();
                    default:
                        Criteria.errorField(accessor[0], accessor[1]);
                }
                break;
            default:
                throw new Exception("Parse Error: No such header '" + accessor[0] + "'");
        }
        return null;
    }

    private static long dataToLong(byte[] data) {
        long product = 0x00;
        for (int i = 0; i < data.length || i < (Long.SIZE / Byte.SIZE); i++) {
            long dat = 0x00FF & data[i];
            product <<= Byte.SIZE;
            product |= dat;
        }
        return product;
    }

    private static byte[] deriveValue(String value, String separator, int base) throws Exception {
        try {
            String[] decbytes = value.split(separator);
            byte[] val = new byte[decbytes.length];
            for (int i = 0; i < val.length; i++) {
                int v = 0x00FF & Integer.valueOf(decbytes[i], base);
                val[i] = (byte) v;
            }
            return ToolsArray.reverse(val);
        } catch (NumberFormatException ex) {
            throw new Exception("Parse Error: Invalid value '" + value + "'");
        }
    }

    private static byte[] deriveValue(String value) throws Exception {
        if (value.contains(".")) {
            return Criteria.deriveValue(value, ".", 10);
        } else if (value.contains(":")) {
            return Criteria.deriveValue(value, ":", 16);
        } else {
            long solid = Long.valueOf(value);
            return ByteBuffer.allocate(Integer.SIZE / Byte.SIZE).putLong(solid).array();
        }
    }

    private boolean matchesFilter(PduAtomic pkt) throws Exception {
        if (fields.length % 3 != 0) {
            throw new Exception("Parse Error: Incomplete filter expression: '" + this.filterExpression + "'!");
        }
        byte[] field = null;
        byte[] value = null;
        String op = null;
        int procState = 0; //0 - read field, 1 - read op, 2 - read value
        for (String fld : fields) {
            switch (procState) {
                case 0:
                    field = Criteria.accessField(pkt, fld);
                    value = null;
                    op = null;
                    if (field == null) {
                        Criteria.errorExp(fld);
                    }
                    break;
                case 1:
                    op = fld;
                    break;
                case 2:
                    value = Criteria.deriveValue(fld);
                    if (field != null && op != null && value != null) {
                        long f = Criteria.dataToLong(field);
                        long v = Criteria.dataToLong(value);
                        switch (op) {
                            case "==":
                                if (!(f == v)) {
                                    return false;
                                }
                                break;
                            case "!=":
                                if (!(f != v)) {
                                    return false;
                                }
                                break;
                            case "<=":
                                if (!(f <= v)) {
                                    return false;
                                }
                                break;
                            case ">=":
                                if (!(f >= v)) {
                                    return false;
                                }
                                break;
                            case "<":
                                if (!(f < v)) {
                                    return false;
                                }
                                break;
                            case ">":
                                if (!(f > v)) {
                                    return false;
                                }
                                break;
                            default:
                                Criteria.errorExp(fld);
                                break;
                        }
                    } else {
                        Criteria.errorExp(fld);
                    }
                    break;
                default:
                    Criteria.errorExp(fld);
                    break;
            }
            procState++;
            procState %= 3;
        }
        if (procState != 0) {
            throw new Exception("Parse Error: Incomplete filter expression: '" + this.filterExpression + "'!");
        }
        return true;
    }

    private byte[] createInstanceId(PduAtomic pkt) throws Exception {
        byte[] instanceId = new byte[0];
        for (String[] fld : this.expressionFields) {
            byte[] field = Criteria.accessField(pkt, fld);
            if (field != null) {
                instanceId = ToolsArray.concat(instanceId, field);
            } else {
                Criteria.errorExp(fld);
            }
        }
        return instanceId;
    }
}
