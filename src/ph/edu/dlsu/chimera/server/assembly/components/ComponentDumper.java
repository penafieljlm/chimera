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
import ph.edu.dlsu.chimera.core.InstanceManager;
import ph.edu.dlsu.chimera.server.assembly.components.data.IntermodulePipe;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ComponentDumper extends ComponentActive {

    public final IntermodulePipe<PduAtomic> inQueue;
    public final InstanceManager instanceManager;
    public final File trainingFile;
    private long processed;

    public ComponentDumper(IntermodulePipe<PduAtomic> inQueue,
            Criteria[] criterias,
            File trainingFile) {
        this.inQueue = inQueue;
        if (this.inQueue != null) {
            this.inQueue.setReader(this);
        }
        this.instanceManager = new InstanceManager(criterias);
        this.trainingFile = trainingFile;
        this.processed = 0;
    }

    @Override
    protected void componentRun() throws Exception {
        if (this.trainingFile != null) {
            try {
                CSVWriter writer = new CSVWriter(new FileWriter(this.trainingFile));
                String[] headers = this.instanceManager.getHeaders();
                writer.writeNext(headers);
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
                                    String[] instance = this.instanceManager.getInstance(pkt);
                                    if (headers.length != instance.length) {
                                        throw new Exception("Error: [Dumper] Headers do not match data.");
                                    }
                                    writer.writeNext(instance);
                                    writer.flush();
                                    this.processed++;
                                } else {
                                    throw new Exception("Error: [Dumper] Encountered outbound packet.");
                                }
                            }
                        }
                    } else {
                        throw new Exception("Error: [Dumper] inQueue is null.");
                    }
                }
                writer.close();
            } catch (Exception ex) {
                throw new Exception("Error: [Dumper] cannot access file '" + this.trainingFile.getName() + "'.");
            }
        } else {
            throw new Exception("Error: [Dumper] trainingFile is null.");
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
