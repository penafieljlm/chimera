/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.core.criteria;

import de.tbsol.iptablesjava.rules.IpRule;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jnetpcap.packet.PcapPacket;
import ph.edu.dlsu.chimera.reflection.PacketField;
import ph.edu.dlsu.chimera.reflection.PacketFilter;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class Criteria {

    public static final String EXP_SUBJECT = "subject[(]((([^,()]+)([,]))*([^,()]+))[)]";
    public static final String EXP_FILTER = "filter[(]((([^,()]+)([,]))*([^,()]+))[)]";
    public static final String EXP_EXPRESSION = Criteria.EXP_SUBJECT + "( " + Criteria.EXP_FILTER + "){0,1}";
    public final String expression;
    public final String expressionSubjects;
    public final String expressionFilter;
    public final PacketField[] subjects;
    public final PacketFilter filter;

    //syntax: subject(<subject 1>, ... , <subject n>) filter(<filter 1>, ... , <filter n>)
    public Criteria(String expression) throws Exception {
        if (Pattern.matches(Criteria.EXP_EXPRESSION, expression)) {
            Pattern subjpattern = Pattern.compile(Criteria.EXP_SUBJECT);
            Matcher subjmatcher = subjpattern.matcher(expression);
            if (subjmatcher.find()) {
                String subjectexp = subjmatcher.group();
                subjectexp = subjectexp.replaceFirst("subject", "");
                subjectexp = subjectexp.substring(1, subjectexp.length() - 1);
                subjectexp = subjectexp.trim();
                String[] sexps = subjectexp.split("[,]");
                PacketField[] _subjects = new PacketField[sexps.length];
                for (int i = 0; i < sexps.length; i++) {
                    _subjects[i] = new PacketField(sexps[i].trim());
                }
                this.subjects = _subjects;
                this.expressionSubjects = subjectexp;
            } else {
                throw new Exception("Parse Error: Criteria subjects are missing in the expression '" + expression + "'");
            }
            Pattern filtpattern = Pattern.compile(Criteria.EXP_FILTER);
            Matcher filtmatcher = filtpattern.matcher(expression);
            if (filtmatcher.find()) {
                String filterexp = filtmatcher.group();
                filterexp = filterexp.replaceFirst("filter", "");
                filterexp = filterexp.substring(1, filterexp.length() - 1);
                filterexp = filterexp.trim();
                this.filter = PacketFilter.parseExpression(filterexp);
                this.expressionFilter = filterexp;
            } else {
                this.filter = null;
                this.expressionFilter = null;
            }
            this.expression = expression;
        } else {
            throw new Exception("Parse Error: Syntax structure error in expression '" + expression + "'");
        }
    }

    public CriteriaInstance createInstance(PcapPacket pkt) throws Exception {
        if (this.filter != null) {
            if (!this.filter.matches(pkt)) {
                return null;
            }
        }
        BigInteger[] cId = new BigInteger[this.subjects.length];
        for (int i = 0; i < this.subjects.length; i++) {
            cId[i] = this.subjects[i].getFieldValue(pkt);
            if (cId[i] == null) {
                return null;
            }
        }
        return new CriteriaInstance(cId, this);
    }

    public IpRule createRule(PcapPacket pkt) {
        IpRule rule = new IpRule();
        rule.setProtocol(IpRule.IpProto.IPPROTO_ALL);
        for (PacketField f : this.subjects) {
            if (!f.applyRule(rule, pkt)) {
                return null;
            }
        }
        return rule;
    }

    public static Criteria[] loadCriterias() throws Exception {
        File criteriasFile = new File("criterias.config");
        ArrayList<String> expressions = new ArrayList<String>();
        if (!criteriasFile.exists()) {
            expressions.add("subject(org.jnetpcap.protocol.network.Ip4.source)");
            expressions.add("subject(org.jnetpcap.protocol.network.Ip4.source, org.jnetpcap.protocol.tcpip.Tcp.source)");
            expressions.add("subject(org.jnetpcap.protocol.network.Ip4.source, org.jnetpcap.protocol.tcpip.Tcp.source) filter(org.jnetpcap.protocol.tcpip.Tcp.flags==hex:02)");

            expressions.add("subject(org.jnetpcap.protocol.network.Ip4.destination)");
            expressions.add("subject(org.jnetpcap.protocol.network.Ip4.destination, org.jnetpcap.protocol.tcpip.Tcp.destination)");
            expressions.add("subject(org.jnetpcap.protocol.network.Ip4.destination, org.jnetpcap.protocol.tcpip.Tcp.destination) filter(org.jnetpcap.protocol.tcpip.Tcp.flags==hex:02)");

            expressions.add("subject(org.jnetpcap.protocol.network.Ip4.source, org.jnetpcap.protocol.network.Ip4.destination)");
            expressions.add("subject(org.jnetpcap.protocol.network.Ip4.source, org.jnetpcap.protocol.tcpip.Tcp.source, org.jnetpcap.protocol.network.Ip4.destination, org.jnetpcap.protocol.tcpip.Tcp.destination)");
            expressions.add("subject(org.jnetpcap.protocol.network.Ip4.source, org.jnetpcap.protocol.tcpip.Tcp.source, org.jnetpcap.protocol.network.Ip4.destination, org.jnetpcap.protocol.tcpip.Tcp.destination) filter(org.jnetpcap.protocol.tcpip.Tcp.flags==hex:02)");

            String[] exps = new String[expressions.size()];
            for (int i = 0; i < exps.length; i++) {
                exps[i] = expressions.get(i);
            }
            Criteria.saveCriterias(exps);
        } else {
            Scanner cConfigFileScanner = new Scanner(criteriasFile);
            cConfigFileScanner = cConfigFileScanner.useDelimiter(";");
            while (cConfigFileScanner.hasNext()) {
                String exp = cConfigFileScanner.next().trim();
                if (exp.length() > 0) {
                    expressions.add(exp);
                }
            }
        }
        Criteria[] criterias = new Criteria[expressions.size()];
        for (int i = 0; i < criterias.length; i++) {
            criterias[i] = new Criteria(expressions.get(i));
        }
        return criterias;
    }

    public static void saveCriterias(String[] criterias) throws Exception {
        File criteriasFile = new File("criterias.config");
        if (criteriasFile.exists()) {
            try {
                criteriasFile.delete();
            } catch (Exception ex) {
                throw new Exception("Unable to overwrite 'criterias.config'.");
            }
        }
        try {
            if (criteriasFile.createNewFile()) {
                FileWriter cConfigFileWriter = new FileWriter(criteriasFile);
                for (String exp : criterias) {
                    cConfigFileWriter.write(exp + ";\r\n");
                }
                cConfigFileWriter.close();
            }
        } catch (IOException ex) {
            throw new Exception("Unable to write 'criterias.config'.");
        }
    }
}
