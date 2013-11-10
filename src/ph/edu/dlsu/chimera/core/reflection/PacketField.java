/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.core.reflection;

import java.lang.reflect.Constructor;
import java.math.BigInteger;
import org.jnetpcap.packet.JHeader;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.structure.JField;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class PacketField {

    public final String headerName;
    public final String fieldName;
    public final Constructor headerConstructor;

    //syntax: <header packages (optinal)>.<class name>.<field name>
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
        Class c = null;
        try {
            c = Class.forName(header);
        } catch (Exception ex) {
            throw new Exception("Parse Error: header type '" + header + "', not found!");
        }
        if (c != null) {
            if (JHeader.class.isAssignableFrom(c)) {
                Constructor constr = null;
                try {
                    constr = c.getConstructor();
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
        this.headerName = header;
        this.fieldName = field;
    }

    public BigInteger getFieldValue(PcapPacket pkt) {
        try {
            JHeader type = (JHeader) this.headerConstructor.newInstance();
            try {
                if (pkt.hasHeader(type)) {
                    JHeader inst = (JHeader) this.headerConstructor.newInstance();
                    JHeader jh = pkt.getHeader(inst);
                    for (JField f : jh.getFields()) {
                        if (f.getName().equals(this.fieldName)) {
                            byte[] value = jh.getByteArray(f.getOffset(jh), f.getLength(jh));
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
}
