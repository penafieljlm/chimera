/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import au.com.bytecode.opencsv.CSVWriter;
import java.io.FileWriter;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.deployment.components.data.Connection;
import ph.edu.dlsu.chimera.server.deployment.components.data.SocketPair;
import ph.edu.dlsu.chimera.server.deployment.components.data.Statistics;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduComposite;
import ph.edu.dlsu.chimera.server.deployment.components.data.stats.Criteria;
import ph.edu.dlsu.chimera.util.ToolsArray;
import ph.edu.dlsu.chimera.util.ToolsPacket;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ComponentTrainingBuilder extends ComponentActive {

    public final ConcurrentLinkedQueue<PduAtomic> inQueue;
    public final ConcurrentLinkedQueue<PduAtomic> outQueue;
    public final List<Criteria> criterias;
    public final String trainingFileName;
    public final boolean tagTrainingAsNormal;

    public ComponentTrainingBuilder(Assembly assembly,
            ConcurrentLinkedQueue<PduAtomic> inQueue,
            ConcurrentLinkedQueue<PduAtomic> outQueue,
            List<Criteria> criterias,
            String trainingFileName,
            boolean tagTrainingAsNormal) {
        super(assembly);
        this.inQueue = inQueue;
        this.outQueue = outQueue;
        this.criterias = criterias;
        this.trainingFileName = trainingFileName;
        this.tagTrainingAsNormal = tagTrainingAsNormal;
    }

    //atomic : per packet, get related statistics data and state data, and create a training set entry
    @Override
    protected void componentRun() throws Exception {
        CSVWriter writer = new CSVWriter(new FileWriter(this.trainingFileName));
        while (super.running) {
            if (this.inQueue != null) {
                while (!this.inQueue.isEmpty()) {
                    PduAtomic pkt = this.inQueue.poll();
                    String[] set = new String[3];
                    //packet protocol
                    set[0] = ToolsPacket.getPacketProtocolName(pkt.packet);
                    //destination port
                    SocketPair conn = ToolsPacket.getSocketPair(pkt.packet);
                    if (conn != null) {
                        set[1] = "" + conn.destinationPort;
                    }
                    //packet size
                    set[2] = "" + pkt.packet.size();
                    //connection statistics
                    if (pkt.getConnection() != null) {
                        set = ToolsArray.concat(set, pkt.getConnection().toCsvValues());
                    } else {
                        set = ToolsArray.concat(set, new String[Statistics.STAT_CSV_VAL_COUNT + Connection.CONN_CSV_VAL_COUNT]);
                    }
                    //custom statistics
                    for (Criteria crt : this.criterias) {
                        Statistics st = pkt.getStatisticsByType(crt);
                        if (st != null) {
                            set = ToolsArray.concat(set, st.toCsvValues());
                        } else {
                            set = ToolsArray.concat(set, new String[Statistics.STAT_CSV_VAL_COUNT]);
                        }
                    }
                    //verdict
                    String[] verdict = new String[1];
                    verdict[0] = "" + this.tagTrainingAsNormal;
                    set = ToolsArray.concat(set, verdict);
                    writer.writeNext(set);
                    if (this.outQueue != null) {
                        this.outQueue.add(pkt);
                    }
                }
            }
        }
        writer.close();
    }
}
