/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jnetpcap.packet.PcapPacket;
import ph.edu.dlsu.chimera.server.core.reflection.PacketFilter;
import ph.edu.dlsu.chimera.server.core.reflection.PacketField;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class Criteria {

    public static final String EXP_SUBJECT = "subject[(](((.+)([,]))*([^,]+))[)]";
    public static final String EXP_FILTER = "filter[(](((.+)([,]))*([^,]+))[)]";
    public static final String EXP_EXPRESSION = Criteria.EXP_SUBJECT + "( " + Criteria.EXP_FILTER + "){0,1}";
    public final String expression;
    public final PacketField[] subjects;
    public final PacketFilter[] filters;

    //syntax: subject(<subject 1>, ... , <subject n>) filter(<filter 1>, ... , <filter n>)
    public Criteria(String expression) throws Exception {
        if (Pattern.matches(Criteria.EXP_EXPRESSION, expression)) {
            Pattern subjpattern = Pattern.compile(Criteria.EXP_SUBJECT);
            Matcher subjmatcher = subjpattern.matcher(expression);
            if (subjmatcher.find()) {
                String subjectexp = subjmatcher.group();
                subjectexp = subjectexp.replaceFirst("subject", "").substring(1, subjectexp.length() - 1).trim();
                String[] sexps = subjectexp.split(",");
                PacketField[] _subjects = new PacketField[sexps.length];
                for (int i = 0; i < sexps.length; i++) {
                    _subjects[i] = new PacketField(sexps[i].trim());
                }
                this.subjects = _subjects;
            } else {
                throw new Exception("Parse Error: Criteria subjects are missing in the expression '" + expression + "'");
            }
            Pattern filtpattern = Pattern.compile(Criteria.EXP_FILTER);
            Matcher filtmatcher = filtpattern.matcher(expression);
            if (filtmatcher.find()) {
                String filterexp = subjmatcher.group();
                filterexp = filterexp.replaceFirst("filter", "").substring(1, filterexp.length() - 1).trim();
                String[] fexps = filterexp.split(",");
                PacketFilter[] _filters = new PacketFilter[fexps.length];
                for (int i = 0; i < fexps.length; i++) {
                    _filters[i] = new PacketFilter(fexps[i].trim());
                }
                this.filters = _filters;
            } else {
                this.filters = null;
            }
            this.expression = expression;
        } else {
            throw new Exception("Parse Error: Syntax structure error in expression '" + expression + "'");
        }
    }

    public CriteriaInstance createInstance(PcapPacket pkt) throws Exception {
        if (this.filters != null) {
            for (PacketFilter f : this.filters) {
                if (!f.matches(pkt)) {
                    return null;
                }
            }
        }
        Object[] cId = new Object[this.subjects.length];
        for (int i = 0; i < this.subjects.length; i++) {
            cId[i] = this.subjects[i].getFieldValue(pkt);
        }
        return new CriteriaInstance(cId, this);
    }
}
