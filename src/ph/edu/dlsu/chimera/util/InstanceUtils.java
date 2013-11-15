/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;
import ph.edu.dlsu.chimera.core.Connection;
import ph.edu.dlsu.chimera.core.Criteria;
import ph.edu.dlsu.chimera.core.Statistics;
import ph.edu.dlsu.chimera.pdu.PduAtomic;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public abstract class InstanceUtils {

    public static final String[] CORE_HEADERS = {"protocol",
        "weekday",
        "timeofday",
        "pdu_size",
        "dest_tcp",
        "dest_udp",
        "flag_tcp"};
    public static final String[] CONN_HEADERS = {"conn.in_enc_timed",
        "conn.ou_enc_timed",
        "conn.in_enc_count",
        "conn.ou_enc_count",
        "conn.in_tsize",
        "conn.ou_tsize",
        "conn.in_asize",
        "conn.ou_asize",
        "conn.in_rateps",
        "conn.ou_rateps"};
    public static final String ATTK_HEADER = "attack";

    public static String[] getCoreInstance(PduAtomic packet) {
        ArrayList<String> instance = new ArrayList<>();
        Tcp tcp = packet.packet.getHeader(new Tcp());
        Udp udp = packet.packet.getHeader(new Udp());
        instance.add("" + packet.getProtocolName());
        instance.add("" + Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
        instance.add("" + ((Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 3600) + (Calendar.getInstance().get(Calendar.MINUTE) * 60) + (Calendar.getInstance().get(Calendar.SECOND) * 1)));
        instance.add("" + packet.packet.size());
        instance.add("" + ((tcp == null) ? null : tcp.destination()));
        instance.add("" + ((udp == null) ? null : udp.destination()));
        instance.add("" + ((tcp == null) ? null : tcp.flags()));
        return instance.toArray(new String[0]);
    }

    public static String[] getCoreInstance(String[] instance) {
        String[] subinst = new String[InstanceUtils.CORE_HEADERS.length];
        System.arraycopy(instance, 0, subinst, 0, subinst.length);
        return subinst;
    }

    public static String[] getConnectionInstance(PduAtomic packet) {
        ArrayList<String> instance = new ArrayList<>();
        Connection conn = packet.getConnection();
        instance.add("" + ((conn == null) ? null : conn.inboundLastEncounterDeltaNs()));
        instance.add("" + ((conn == null) ? null : conn.outboundLastEncounterDeltaNs()));
        instance.add("" + ((conn == null) ? null : conn.inboundEncounters()));
        instance.add("" + ((conn == null) ? null : conn.outboundEncounters()));
        instance.add("" + ((conn == null) ? null : conn.inboundTotalSize()));
        instance.add("" + ((conn == null) ? null : conn.outboundTotalSize()));
        instance.add("" + ((conn == null) ? null : conn.inboundAverageSize()));
        instance.add("" + ((conn == null) ? null : conn.outboundAverageSize()));
        instance.add("" + ((conn == null) ? null : conn.inboundRatePerSec()));
        instance.add("" + ((conn == null) ? null : conn.outboundRatePerSec()));
        return instance.toArray(new String[0]);
    }

    public static String[] getConnectionInstance(String[] instance) {
        String[] subinst = new String[InstanceUtils.CONN_HEADERS.length];
        System.arraycopy(instance, InstanceUtils.CORE_HEADERS.length, subinst, 0, subinst.length);
        return subinst;
    }

    public static String[] getCriteriaHeaders(Criteria criteria) {
        ArrayList<String> headers = new ArrayList<>();
        String exp = criteria.expression.replaceAll(" ", "");
        headers.add("exp(" + exp + ").enc_timed");
        headers.add("exp(" + exp + ").enc_count");
        headers.add("exp(" + exp + ").enc_tsize");
        headers.add("exp(" + exp + ").enc_asize");
        headers.add("exp(" + exp + ").enc_rateps");
        return headers.toArray(new String[0]);
    }

    public static String[] getCriteriaInstance(Criteria criteria, PduAtomic packet) {
        ArrayList<String> instance = new ArrayList<>();
        Statistics crtstats = packet.getStatistics(criteria);
        instance.add("" + ((crtstats == null) ? null : crtstats.getLastEncounterDeltaNs()));
        instance.add("" + ((crtstats == null) ? null : crtstats.getTotalEncounters()));
        instance.add("" + ((crtstats == null) ? null : crtstats.getTotalSize()));
        instance.add("" + ((crtstats == null) ? null : crtstats.getAverageSize()));
        instance.add("" + ((crtstats == null) ? null : crtstats.getTrafficRatePerSec()));
        return instance.toArray(new String[0]);
    }

    public static String[] getCriteriaInstance(Criteria criteria, String[] headers, String[] instance) {
        if (headers.length != instance.length) {
            return null;
        }
        String[] _headers = InstanceUtils.getCriteriaHeaders(criteria);
        String[] subinst = new String[headers.length];
        for (int hCounter = 0; hCounter < _headers.length; hCounter++) {
            String _header = _headers[hCounter];
            String _value = null;
            for (int locCounter = 0; locCounter < headers.length; locCounter++) {
                if (_header.equals(headers[locCounter])) {
                    _value = instance[locCounter];
                }
            }
            subinst[hCounter] = _value;
        }
        return subinst;
    }

    public static String[] getCriteriasHeaders(Criteria[] criterias) {
        ArrayList<String> headers = new ArrayList<>();
        for (Criteria crt : criterias) {
            headers.addAll(Arrays.asList(InstanceUtils.getCriteriaHeaders(crt)));
        }
        return headers.toArray(new String[0]);
    }

    public static String[] getCriteriasInstance(Criteria[] criterias, PduAtomic packet) {
        ArrayList<String> instance = new ArrayList<>();
        for (Criteria crt : criterias) {
            instance.addAll(Arrays.asList(InstanceUtils.getCriteriaInstance(crt, packet)));
        }
        return instance.toArray(new String[0]);
    }

    public static String[] getHeaders(Criteria[] criterias) {
        ArrayList<String> headers = new ArrayList<>();
        headers.addAll(Arrays.asList(InstanceUtils.CORE_HEADERS));
        headers.addAll(Arrays.asList(InstanceUtils.CONN_HEADERS));
        for (Criteria crt : criterias) {
            headers.addAll(Arrays.asList(InstanceUtils.getCriteriaHeaders(crt)));
        }
        headers.add(InstanceUtils.ATTK_HEADER);
        return headers.toArray(new String[0]);
    }

    public static String[] getInstance(Criteria[] criterias, PduAtomic packet, boolean tagAsAttack) {
        ArrayList<String> instance = new ArrayList<>();
        instance.addAll(Arrays.asList(InstanceUtils.getCoreInstance(packet)));
        instance.addAll(Arrays.asList(InstanceUtils.getConnectionInstance(packet)));
        for (Criteria crt : criterias) {
            instance.addAll(Arrays.asList(InstanceUtils.getCriteriaInstance(crt, packet)));
        }
        instance.add("" + tagAsAttack);
        return instance.toArray(new String[0]);
    }
}
