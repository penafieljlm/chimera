/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.assembly.components;

import au.com.bytecode.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.server.assembly.components.data.pdu.PduAtomic;
import ph.edu.dlsu.chimera.core.Criteria;
import ph.edu.dlsu.chimera.server.assembly.components.data.IntermodulePipe;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ComponentInstanceDumper extends ComponentActive {

    public final IntermodulePipe<PduAtomic> inQueue;
    public final Criteria[] criterias;
    public final File trainingFile;
    private long processed;
    private boolean headerOk;

    public ComponentInstanceDumper(IntermodulePipe<PduAtomic> inQueue,
            Criteria[] criterias,
            File trainingFile) {
        this.inQueue = inQueue;
        if (this.inQueue != null) {
            this.inQueue.setReader(this);
        }
        this.criterias = criterias;
        this.trainingFile = trainingFile;
        this.processed = 0;
        this.headerOk = false;
    }

    @Override
    protected void componentRun() throws Exception {
        if (this.trainingFile != null) {
            try {
                CSVWriter writer = new CSVWriter(new FileWriter(this.trainingFile));
                while (super.running) {
                    if (this.inQueue != null) {
                        if (this.inQueue.isEmpty()) {
                            synchronized (this) {
                                this.wait();
                            }
                        }
                        while (!this.inQueue.isEmpty()) {
                            PduAtomic pkt = this.inQueue.poll();
                            synchronized (pkt) {
                                if (pkt.inbound) {
                                    if (pkt.getInstanceHeaders().length != pkt.getInstanceData().length) {
                                        throw new Exception("Error: [Instance Dumper] Headers do not match data.");
                                    }
                                    if (!this.headerOk) {
                                        this.headerOk = true;
                                        writer.writeNext(pkt.getInstanceHeaders());
                                    }
                                    writer.writeNext(pkt.getInstanceData());
                                    writer.flush();
                                    this.processed++;
                                } else {
                                    throw new Exception("Error: [Instance Dumper] Encountered outbound packet.");
                                }
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
