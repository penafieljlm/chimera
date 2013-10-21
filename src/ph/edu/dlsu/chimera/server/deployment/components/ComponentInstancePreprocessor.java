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
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;
import ph.edu.dlsu.chimera.util.ToolsArray;
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
        this.processed = 0;
    }

    @Override
    protected void componentRun() throws Exception {
        while (super.running) {
            if (this.inQueue != null) {
                while (!this.inQueue.isEmpty()) {
                    PduAtomic pkt = this.inQueue.poll();
                    if (pkt.inbound) {
                        pkt.setInstanceData(this.getInstance(pkt));
                        if (this.outQueue != null) {
                            this.processed = 0;
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

    private String[] getInstance(PduAtomic pkt) {
        String[] set = new String[0];
        Connection conn = pkt.getConnection();

        //field - protocol
        set = ToolsArray.append(set, ToolsPacket.getPacketProtocolName(pkt.packet));

        //field - destination ip
        set = ToolsArray.append(set, ToolsPacket.getIpAddress(pkt.packet, false).getHostAddress());

        //field - destination tcp port
        set = ToolsArray.append(set, "" + ToolsPacket.getTcpPort(pkt.packet, false));

        //field - destination udp port
        set = ToolsArray.append(set, "" + ToolsPacket.getUdpPort(pkt.packet, false));

        //field - connection inbound encounter interval
        set = ToolsArray.append(set, "" + conn.inboundLastEncounterDeltaNs());

        //field - connection outbound encounter interval
        set = ToolsArray.append(set, "" + conn.outboundLastEncounterDeltaNs());

        //field - connection inbound encounters
        set = ToolsArray.append(set, "" + conn.inboundEncounters());

        //field - connection outbound encounters
        set = ToolsArray.append(set, "" + conn.outboundEncounters());

        //field - connection inbound total size
        set = ToolsArray.append(set, "" + conn.inboundTotalSize());

        //field - connection outbound total size
        set = ToolsArray.append(set, "" + conn.outboundTotalSize());

        //fields - criterias


        return set;
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
