/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components;

import au.com.bytecode.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.server.Assembly;
import ph.edu.dlsu.chimera.server.deployment.components.data.pdu.PduAtomic;
import ph.edu.dlsu.chimera.server.core.Criteria;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ComponentInstanceDumper extends ComponentActive {

    public final ConcurrentLinkedQueue<PduAtomic> inQueue;
    public final Criteria[] criterias;
    public final File trainingFile;
    private long processed;

    public ComponentInstanceDumper(Assembly assembly,
            ConcurrentLinkedQueue<PduAtomic> inQueue,
            Criteria[] criterias,
            File trainingFile) {
        super(assembly);
        this.inQueue = inQueue;
        this.criterias = criterias;
        this.trainingFile = trainingFile;
        this.processed = 0;
    }

    @Override
    protected void componentRun() throws Exception {
        if (this.trainingFile != null) {
            try {
                CSVWriter writer = new CSVWriter(new FileWriter(this.trainingFile));
                while (super.running) {
                    if (this.inQueue != null) {
                        while (!this.inQueue.isEmpty()) {
                            PduAtomic pkt = this.inQueue.poll();
                            if (pkt.inbound) {
                                writer.writeNext(pkt.getInstanceData());
                                this.processed++;
                            } else {
                                throw new Exception("Error: [Instance Dumper] Encountered outbound packet.");
                            }
                        }
                    } else {
                        throw new Exception("Error: [Instance Dumper] inQueue is null.");
                    }
                }
                writer.close();
            } catch (Exception ex) {
                throw new Exception("Error: [Instance Dumper] cannot access file '" + this.trainingFile.getName() + "'.");
            }
        } else {
            throw new Exception("Error: [Instance Dumper] trainingFile is null.");
        }
    }

    @Override
    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = super.getDiagnostics();
        if (this.inQueue != null) {
            diag.add(new Diagnostic("inqueue", "Inbound Queued Packets", this.inQueue.size()));
        } else {
            diag.add(new Diagnostic("inqueue", "Inbound Queued Packets", "N/A"));
        }
        diag.add(new Diagnostic("processed", "Packets Processed", this.processed));
        return diag;
    }
}
