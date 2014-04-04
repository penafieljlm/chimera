package ph.edu.dlsu.chimera.core.logs;

import java.math.BigInteger;
import java.util.Date;
import org.jnetpcap.packet.PcapPacket;
import ph.edu.dlsu.chimera.core.Statistics;
import ph.edu.dlsu.chimera.core.criteria.Criteria;
import ph.edu.dlsu.chimera.reflection.PacketField;

/**
 * An instance of this class constitutes a type of Log which deals with attacks
 * associated with a CriteriaInstance.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class LogAttackCriteria extends LogAttack {

    /**
     * The fields and values of the Criteria involved
     */
    public final CriteriaSubject[] fields;
    /**
     * The statistics monitor for the Criteria involved
     */
    public final Statistics statistics;

    /**
     * Constructs a new LogAttackCriteria object.
     *
     * @param dateTime The date and time when the log was created
     * @param criteria The Criteria object involved in the attack
     * @param pkt The packet involved in the attack
     * @param statistics The statistics monitor associated with the attack
     */
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

    /**
     * An internal helper class used for CriteriaField and value serialization
     * (using JSON).
     */
    public class CriteriaSubject {

        /**
         * The header class
         */
        public final String headerClass;
        /**
         * The field name
         */
        public final String fieldName;
        /**
         * The value
         */
        public final BigInteger value;

        /**
         * Constructs a new CriteriaSubject object.
         *
         * @param headerClass The header class
         * @param fieldName The field name
         * @param value The value
         */
        public CriteriaSubject(String headerClass, String fieldName, BigInteger value) {
            this.headerClass = headerClass;
            this.fieldName = fieldName;
            this.value = value;
        }
    }
}
