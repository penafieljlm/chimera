/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.core.Connection;
import ph.edu.dlsu.chimera.server.core.Criteria;
import ph.edu.dlsu.chimera.server.core.Statistics;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;
import ph.edu.dlsu.chimera.util.ToolsPacket;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ComponentInstancePreprocessor extends ComponentActive {

    public final ConcurrentLinkedQueue<PduAtomic> inQueue;
    public final ConcurrentLinkedQueue<PduAtomic> outQueue;
    public final Criteria[] criterias;
    public final boolean tagTrafficAsAttacks;
    public final String[] instanceHeaders;
    private long processed;

    public ComponentInstancePreprocessor(Assembly assembly,
            ConcurrentLinkedQueue<PduAtomic> inQueue,
            ConcurrentLinkedQueue<PduAtomic> outQueue,
            Criteria[] criterias,
            boolean tagTrafficAsAttacks) {
        super(assembly);
        this.inQueue = inQueue;
        this.outQueue = outQueue;
        this.criterias = criterias;
        this.tagTrafficAsAttacks = tagTrafficAsAttacks;
        this.instanceHeaders = this.getInstanceHeaders();
        this.processed = 0;
    }

    @Override
    protected void componentRun() throws Exception {
        while (super.running) {
            if (this.inQueue != null) {
                while (!this.inQueue.isEmpty()) {
                    PduAtomic pkt = this.inQueue.poll();
                    if (pkt.inbound) {
                        pkt.setInstance(this.instanceHeaders, this.getInstance(pkt));
                        if (this.outQueue != null) {
                            this.processed++;
                            this.outQueue.add(pkt);
                        } else {
                            throw new Exception("Error: [Instance Preprocessor] outQueue is null.");
                        }
                    } else {
                        throw new Exception("Error: [Instance Preprocessor] Encountered outbound packet.");
                    }
                }
            } else {
                throw new Exception("Error: [Instance Preprocessor] inQueue is null.");
            }
        }
    }
    
    private String[] getInstanceHeaders() {
        ArrayList<String> header = new ArrayList<>();
        header.add("protocol");
        header.add("pdu_size");
        header.add("dest_tcp");
        header.add("dest_udp");
        header.add("conn_in_enc_timed");
        header.add("conn_ou_enc_timed");
        header.add("conn_in_enc_count");
        header.add("conn_ou_enc_count");
        header.add("conn_in_tsize");
        header.add("conn_ou_tsize");
        header.add("conn_in_asize");
        header.add("conn_ou_asize");
        header.add("conn_in_rateps");
        header.add("conn_ou_rateps");
        for (Criteria crt : this.criterias) {
            String exp = crt.expression.replaceAll(" ", "");
            header.add(exp + "_enc_timed");
            header.add(exp + "_enc_count");
            header.add(exp + "_enc_tsize");
            header.add(exp + "_enc_asize");
            header.add(exp + "_enc_rateps");
        }
        header.add("attack");
        return header.toArray(new String[0]);
    }

    private String[] getInstance(PduAtomic pkt) {
        ArrayList<String> set = new ArrayList<>();
        Connection conn = pkt.getConnection();

        //field - protocol
        set.add("" + ToolsPacket.getPacketProtocolName(pkt.packet));

        //field - size
        set.add("" + pkt.packet.size());

        //field - destination tcp port
        set.add("" + ToolsPacket.getTcpPort(pkt.packet, false));

        //field - destination udp port
        set.add("" + ToolsPacket.getUdpPort(pkt.packet, false));

        //field - connection inbound encounter interval
        set.add("" + ((conn == null) ? null : conn.inboundLastEncounterDeltaNs()));

        //field - connection outbound encounter interval
        set.add("" + ((conn == null) ? null : conn.outboundLastEncounterDeltaNs()));

        //field - connection inbound encounters
        set.add("" + ((conn == null) ? null : conn.inboundEncounters()));

        //field - connection outbound encounters
        set.add("" + ((conn == null) ? null : conn.outboundEncounters()));

        //field - connection inbound total size
        set.add("" + ((conn == null) ? null : conn.inboundTotalSize()));

        //field - connection outbound total size
        set.add("" + ((conn == null) ? null : conn.outboundTotalSize()));

        //field - connection inbound average size
        set.add("" + ((conn == null) ? null : conn.inboundAverageSize()));

        //field - connection outbound average size
        set.add("" + ((conn == null) ? null : conn.outboundAverageSize()));

        //field - connection inbound average packet rate
        set.add("" + ((conn == null) ? null : conn.inboundRatePerSec()));

        //field - connection outbound average packet rate
        set.add("" + ((conn == null) ? null : conn.outboundRatePerSec()));

        //field - criterias
        for (Criteria crt : this.criterias) {
            Statistics crtstats = pkt.getStatistics(crt);

            //subfield - encounter interval
            set.add("" + ((crtstats == null) ? null : crtstats.getLastEncounterDeltaNs()));

            //subfield - total encounters
            set.add("" + ((crtstats == null) ? null : crtstats.getTotalEncounters()));

            //subfield - total size
            set.add("" + ((crtstats == null) ? null : crtstats.getTotalSize()));

            //subfield - average size
            set.add("" + ((crtstats == null) ? null : crtstats.getAverageSize()));

            //subfield - average rate
            set.add("" + ((crtstats == null) ? null : crtstats.getTrafficRatePerSec()));
        }

        //verdict
        set.add("" + this.tagTrafficAsAttacks);

        return set.toArray(new String[0]);
    }

    @Override
    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = super.getDiagnostics();
        if (this.inQueue != null) {
            diag.add(new Diagnostic("inqueue", "Inbound Queued Packets", this.inQueue.size()));
        } else {
            diag.add(new Diagnostic("inqueue", "Inbound Queued Packets", "N/A"));
        }
        if (this.outQueue != null) {
            diag.add(new Diagnostic("outqueue", "Outbound Queued Packets", this.outQueue.size()));
        } else {
            diag.add(new Diagnostic("outqueue", "Outbound Queued Packets", "N/A"));
        }
        diag.add(new Diagnostic("processed", "Packets Processed", this.processed));
        return diag;
    }
}
