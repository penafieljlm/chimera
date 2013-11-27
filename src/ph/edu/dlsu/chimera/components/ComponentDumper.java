/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.components;

import au.com.bytecode.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import org.jnetpcap.PcapIf;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.pdu.PduAtomic;
import ph.edu.dlsu.chimera.core.criteria.Criteria;
import ph.edu.dlsu.chimera.util.UtilsTraining;
import ph.edu.dlsu.chimera.reflection.PacketFilter;
import ph.edu.dlsu.chimera.core.tools.IntermodulePipe;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ComponentDumper extends ComponentActive {

    public final String inPcapIf;
    public final IntermodulePipe<PduAtomic> inQueue;
    public final Criteria[] criterias;
    public final File trainingFile;
    public final PacketFilter trainingFilter;
    public final boolean tagFilteredAsAttack;
    private long processed;

    public ComponentDumper(IntermodulePipe<PduAtomic> inQueue,
            String inPcapIf,
            Criteria[] criterias,
            File trainingFile,
            PacketFilter trainingFilter,
            boolean tagFilteredAsAttack) {
        this.inPcapIf = inPcapIf;
        this.inQueue = inQueue;
        if (this.inQueue != null) {
            this.inQueue.setReader(this);
        }
        this.criterias = criterias;
        this.trainingFile = trainingFile;
        this.trainingFilter = trainingFilter;
        this.tagFilteredAsAttack = tagFilteredAsAttack;
        this.processed = 0;
    }

    @Override
    protected void componentRun() throws Exception {
        if (this.trainingFile != null) {
            CSVWriter writer = new CSVWriter(new FileWriter(this.trainingFile));
            String[] iface = {this.inPcapIf};
            String[] headers = UtilsTraining.getHeaders(this.criterias);
            String[] _criterias = new String[this.criterias.length];
            for (int i = 0; i < this.criterias.length; i++) {
                _criterias[i] = this.criterias[i].expression;
            }
            writer.writeNext(iface);
            writer.writeNext(_criterias);
            writer.writeNext(headers);
            writer.flush();
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
                            if (pkt.ingress) {
                                boolean attack = this.tagFilteredAsAttack;
                                if (this.trainingFilter != null) {
                                    attack = !(this.trainingFilter.matches(pkt.packet) ^ attack);
                                }
                                String[] instance = UtilsTraining.getInstance(this.criterias, pkt, attack);
                                if (headers.length != instance.length) {
                                    throw new Exception("Error: [Dumper] Headers do not match data.");
                                }
                                writer.writeNext(instance);
                                writer.flush();
                                this.processed++;
                            } else {
                                throw new Exception("Error: [Dumper] Encountered egress packet.");
                            }
                        }
                    }
                } else {
                    throw new Exception("Error: [Dumper] inQueue is null.");
                }
            }
            writer.close();
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
