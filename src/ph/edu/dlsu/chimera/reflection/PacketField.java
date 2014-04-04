package ph.edu.dlsu.chimera.reflection;

import de.tbsol.iptablesjava.rules.IpRule;
import de.tbsol.iptablesjava.rules.match.ModMac;
import de.tbsol.iptablesjava.rules.match.ModTcp;
import de.tbsol.iptablesjava.rules.match.ModUdp;
import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.net.InetAddress;
import org.jnetpcap.packet.JHeader;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.structure.JField;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

/**
 * An instance of this class constitutes an attribute that could be extracted
 * from a packet.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class PacketField {

    /**
     * The class of the header from which the packet field could be extracted
     * from
     */
    public final Class headerClass;
    /**
     * The name of the field being referred to
     */
    public final String fieldName;
    private final Constructor headerConstructor;

    /**
     * Constructs a new PacketField object from the given PacketField
     * expression. The PacketField expression syntax follows the following
     * format: <header packages (optinal)>.<class name>.<field name>.
     *
     * @param expression The PacketField expression
     * @throws Exception
     */
    public PacketField(String expression) throws Exception {
        String[] e = expression.split("[.]");
        if (e.length < 2) {
            throw new Exception("Parse Error: Syntax structure error in expression '" + expression + "'");
        }
        String field = e[e.length - 1].trim();
        int hLastIdx = expression.lastIndexOf(field) - 1;
        if (hLastIdx < 0) {
            throw new Exception("Parse Error: Syntax structure error in expression '" + expression + "'");
        }
        String header = expression.substring(0, hLastIdx).trim();
        try {
            this.headerClass = Class.forName(header);
        } catch (Exception ex) {
            throw new Exception("Parse Error: header type '" + header + "', not found!");
        }
        if (this.headerClass != null) {
            if (JHeader.class.isAssignableFrom(this.headerClass)) {
                Constructor constr = null;
                try {
                    constr = this.headerClass.getConstructor();
                } catch (Exception ex) {
                    throw new Exception("Parse Error: constructor for header type '" + header + "', not found!");
                }
                if (constr != null) {
                    JHeader jh = null;
                    try {
                        jh = (JHeader) constr.newInstance();
                    } catch (Exception ex) {
                        throw new Exception("Parse Error: cannot create instance of header type '" + header + "'!");
                    }
                    if (jh != null) {
                        boolean found = false;
                        for (JField f : jh.getFields()) {
                            if (f.getName().equals(field)) {
                                found = true;
                                break;
                            }
                        }
                        if (found) {
                            this.headerConstructor = constr;
                        } else {
                            throw new Exception("Parse Error: field '" + field + "' not found on header type '" + header + "'!");
                        }
                    } else {
                        throw new Exception("Parse Error: cannot create instance of header type '" + header + "'!");
                    }
                } else {
                    throw new Exception("Parse Error: constructor for header type '" + header + "', not found!");
                }
            } else {
                throw new Exception("Parse Error: header type '" + header + "', not found!");
            }
        } else {
            throw new Exception("Parse Error: header type '" + header + "', not found!");
        }
        this.fieldName = field;
    }

    /**
     * Extracts the value of the field being referred to by this PacketField
     * object from the provided PcapPacket.
     *
     * @param pkt The provided PcapPacket
     * @return The value of the field being referred to
     */
    public BigInteger getFieldValue(PcapPacket pkt) {
        try {
            JHeader type = (JHeader) this.headerConstructor.newInstance();
            try {
                if (pkt.hasHeader(type)) {
                    JHeader inst = (JHeader) this.headerConstructor.newInstance();
                    JHeader jh = pkt.getHeader(inst);
                    for (JField f : jh.getFields()) {
                        if (f.getName().equals(this.fieldName)) {
                            byte[] value = jh.getByteArray(f.getOffset(jh) / 8, f.getLength(jh) / 8);
                            return new BigInteger(value);
                        }
                    }
                }
            } catch (Exception ex) {
            }
        } catch (Exception ex) {
        }
        return null;
    }

    /**
     * Applies the value of the field being referred to by this PacketField
     * object from the provided PcapPacket onto the provided IpRule object.
     *
     * @param rule The iptables rule to apply the values to
     * @param pkt The provided PcapPacket
     * @return
     */
    public boolean applyRule(IpRule rule, PcapPacket pkt) {
        try {
            JHeader type = (JHeader) this.headerConstructor.newInstance();
            if (pkt.hasHeader(type)) {
                if (this.headerClass == Ethernet.class) {
                    ModMac mac = new ModMac();
                    if (this.fieldName.equals("source")) {
                        mac.setSrcaddr(this.getFieldValue(pkt).toByteArray());
                        rule.addModule(mac);
                        return true;
                    }
                } else if (this.headerClass == Ip4.class) {
                    if (this.fieldName.equals("source")) {
                        rule.setSource(InetAddress.getByAddress(this.getFieldValue(pkt).toByteArray()));
                        rule.setSourceMask(InetAddress.getByName("255.255.255.255"));
                        return true;
                    } else if (this.fieldName.equals("destination")) {
                        rule.setDestination(InetAddress.getByAddress(this.getFieldValue(pkt).toByteArray()));
                        rule.setDestinationMask(InetAddress.getByName("255.255.255.255"));
                        return true;
                    }
                } else if (this.headerClass == Icmp.class) {
                    rule.setProtocol(IpRule.IpProto.IPPROTO_ICMP);
                } else if (this.headerClass == Tcp.class) {
                    ModTcp tcp = new ModTcp();
                    rule.setProtocol(IpRule.IpProto.IPPROTO_TCP);
                    if (this.fieldName.equals("source")) {
                        tcp.setSourcePortStart(this.getFieldValue(pkt).intValue());
                        tcp.setSourcePortEnd(this.getFieldValue(pkt).intValue());
                        rule.addModule(tcp);
                        return true;
                    } else if (this.fieldName.equals("destination")) {
                        tcp.setDestinationPortStart(this.getFieldValue(pkt).intValue());
                        tcp.setDestinationPortEnd(this.getFieldValue(pkt).intValue());
                        rule.addModule(tcp);
                        return true;
                    } else if (this.fieldName.equals("flags")) {
                        tcp.setFlagCompare(this.getFieldValue(pkt).byteValue());
                        tcp.setFlagMask((byte) 0xFF);
                        rule.addModule(tcp);
                        return true;
                    }
                } else if (this.headerClass == Udp.class) {
                    ModUdp udp = new ModUdp();
                    rule.setProtocol(IpRule.IpProto.IPPROTO_UDP);
                    if (this.fieldName.equals("source")) {
                        udp.setSourcePortStart(this.getFieldValue(pkt).intValue());
                        udp.setSourcePortEnd(this.getFieldValue(pkt).intValue());
                        rule.addModule(udp);
                        return true;
                    } else if (this.fieldName.equals("destination")) {
                        udp.setDestinationPortStart(this.getFieldValue(pkt).intValue());
                        udp.setDestinationPortEnd(this.getFieldValue(pkt).intValue());
                        rule.addModule(udp);
                        return true;
                    }
                }
            }
        } catch (Exception ex) {
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PacketField other = (PacketField) obj;
        if (this.headerClass != other.headerClass && (this.headerClass == null || !this.headerClass.equals(other.headerClass))) {
            return false;
        }
        if ((this.fieldName == null) ? (other.fieldName != null) : !this.fieldName.equals(other.fieldName)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + (this.headerClass != null ? this.headerClass.hashCode() : 0);
        hash = 59 * hash + (this.fieldName != null ? this.fieldName.hashCode() : 0);
        return hash;
    }
}
