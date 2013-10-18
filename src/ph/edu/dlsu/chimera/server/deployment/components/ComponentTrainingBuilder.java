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
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;
import ph.edu.dlsu.chimera.server.core.Criteria;
import ph.edu.dlsu.chimera.util.ToolsMachineLearning;

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

    @Override
    protected void componentRun() throws Exception {
        CSVWriter writer = new CSVWriter(new FileWriter(this.trainingFileName));
        while (super.running) {
            if (this.inQueue != null) {
                while (!this.inQueue.isEmpty()) {
                    PduAtomic pkt = this.inQueue.poll();
                    if (pkt.inbound) {
                        writer.writeNext(ToolsMachineLearning.getInstance(pkt, this.criterias));
                        if (this.outQueue != null) {
                            this.outQueue.add(pkt);
                        }
                    }
                }
            }
        }
        writer.close();
    }
}
