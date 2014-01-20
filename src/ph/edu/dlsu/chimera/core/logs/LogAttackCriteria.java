/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.core.logs;

import java.math.BigInteger;
import java.util.Date;
import org.jnetpcap.packet.PcapPacket;
import ph.edu.dlsu.chimera.core.Statistics;
import ph.edu.dlsu.chimera.core.Criteria;
import ph.edu.dlsu.chimera.reflection.PacketField;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class LogAttackCriteria extends LogAttack {

    public String expression;
    public CriteriaSubject[] fields;
    public Statistics statistics;

    public LogAttackCriteria(Date dateTime, Criteria criteria, PcapPacket pkt, Statistics statistics) {
        super(dateTime);
        this.fields = new CriteriaSubject[criteria.subjects.length];
        int i = 0;
        for (PacketField f : criteria.subjects) {
            this.fields[i] = new CriteriaSubject(f.headerClass.getName(), f.fieldName, f.getFieldValue(pkt));
            i++;
        }
        this.statistics = statistics;
    }

    public class CriteriaSubject {

        public String headerClass;
        public String fieldName;
        public BigInteger value;

        public CriteriaSubject(String headerClass, String fieldName, BigInteger value) {
            this.headerClass = headerClass;
            this.fieldName = fieldName;
            this.value = value;
        }
    }
}
