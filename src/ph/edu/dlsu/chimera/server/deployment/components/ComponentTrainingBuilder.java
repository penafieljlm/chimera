/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import au.com.bytecode.opencsv.CSVWriter;
import java.io.FileWriter;
import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduComposite;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ComponentTrainingBuilder extends ComponentActive {

    public final ConcurrentLinkedQueue<PduAtomic> inQueue;
    public final ConcurrentLinkedQueue<PduAtomic> outQueue;
    public final String trainingFileName;
    public final boolean tagTrainingAsNormal;

    public ComponentTrainingBuilder(Assembly assembly,
            ConcurrentLinkedQueue<PduAtomic> inQueue,
            ConcurrentLinkedQueue<PduAtomic> outQueue,
            String trainingFileName,
            boolean tagTrainingAsNormal) {
        super(assembly);
        this.inQueue = inQueue;
        this.outQueue = outQueue;
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
                    if (this.outQueue != null) {
                        this.outQueue.add(pkt);
                    }
                }
            }
        }
    }
}
