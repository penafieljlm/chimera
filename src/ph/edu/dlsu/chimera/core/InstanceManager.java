/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;
import ph.edu.dlsu.chimera.server.assembly.components.data.pdu.PduAtomic;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class InstanceManager {

    public static final String[] CORE_HEADERS = {"protocol",
        "weekday",
        "timeofday",
        "pdu_size",
        "dest_tcp",
        "dest_udp",
        "flag_tcp"};

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
        String[] subinst = new String[InstanceManager.CORE_HEADERS.length];
        System.arraycopy(instance, 0, subinst, 0, subinst.length);
        return subinst;
    }
    public static final String[] CONN_HEADERS = {"conn_in_enc_timed",
        "conn_ou_enc_timed",
        "conn_in_enc_count",
        "conn_ou_enc_count",
        "conn_in_tsize",
        "conn_ou_tsize",
        "conn_in_asize",
        "conn_ou_asize",
        "conn_in_rateps",
        "conn_ou_rateps"};

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
        String[] subinst = new String[InstanceManager.CONN_HEADERS.length];
        System.arraycopy(instance, InstanceManager.CORE_HEADERS.length, subinst, 0, subinst.length);
        return subinst;
    }
    public static final String ATTK_HEADER = "attack";

    public static String[] getCriteriaHeaders(Criteria criteria) {
        ArrayList<String> headers = new ArrayList<>();
        String exp = criteria.expression.replaceAll(" ", "");
        headers.add(exp + "_enc_timed");
        headers.add(exp + "_enc_count");
        headers.add(exp + "_enc_tsize");
        headers.add(exp + "_enc_asize");
        headers.add(exp + "_enc_rateps");
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
    public final Criteria[] criterias;

    public InstanceManager(Criteria[] criterias) {
        this.criterias = criterias;
    }

    public String[] getCriteriaInstance(Criteria criteria, String[] instance) {
        String[] subinst = new String[5];
        int offset = -1;
        for (int i = 0; i < this.criterias.length; i++) {
            if (this.criterias[i] == criteria) {
                offset = 5 * i;
                break;
            }
        }
        if (offset < 0) {
            return null;
        }
        System.arraycopy(instance, InstanceManager.CORE_HEADERS.length + InstanceManager.CONN_HEADERS.length + offset, subinst, 0, subinst.length);
        return subinst;
    }

    public String[] getCriteriasHeaders() {
        ArrayList<String> headers = new ArrayList<>();
        for (Criteria crt : this.criterias) {
            headers.addAll(Arrays.asList(InstanceManager.getCriteriaHeaders(crt)));
        }
        return headers.toArray(new String[0]);
    }

    public String[] getCriteriasInstance(PduAtomic packet) {
        ArrayList<String> instance = new ArrayList<>();
        for (Criteria crt : this.criterias) {
            instance.addAll(Arrays.asList(InstanceManager.getCriteriaInstance(crt, packet)));
        }
        return instance.toArray(new String[0]);
    }

    public String[] getHeaders() {
        ArrayList<String> headers = new ArrayList<>();
        headers.addAll(Arrays.asList(InstanceManager.CORE_HEADERS));
        headers.addAll(Arrays.asList(InstanceManager.CONN_HEADERS));
        for (Criteria crt : this.criterias) {
            headers.addAll(Arrays.asList(InstanceManager.getCriteriaHeaders(crt)));
        }
        headers.add(InstanceManager.ATTK_HEADER);
        return headers.toArray(new String[0]);
    }

    public String[] getInstance(PduAtomic packet, boolean tagAsAttack) {
        ArrayList<String> instance = new ArrayList<>();
        instance.addAll(Arrays.asList(InstanceManager.getCoreInstance(packet)));
        instance.addAll(Arrays.asList(InstanceManager.getConnectionInstance(packet)));
        for (Criteria crt : this.criterias) {
            instance.addAll(Arrays.asList(InstanceManager.getCriteriaInstance(crt, packet)));
        }
        instance.add("" + tagAsAttack);
        return instance.toArray(new String[0]);
    }
}
