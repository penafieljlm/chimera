/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.components;

import au.com.bytecode.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.core.TrafficDirection;
import ph.edu.dlsu.chimera.core.PduAtomic;
import ph.edu.dlsu.chimera.core.criteria.Criteria;
import ph.edu.dlsu.chimera.util.UtilsTraining;
import ph.edu.dlsu.chimera.reflection.PacketFilter;
import ph.edu.dlsu.chimera.core.tools.IntermodulePipe;
import ph.edu.dlsu.chimera.util.UtilsArray;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ComponentDumper extends ComponentActiveProcessorPdu<PduAtomic, PduAtomic> {

    public final String inPcapIf;
    public final Criteria[] criterias;
    public final File trainingFile;
    public final PacketFilter trainingFilter;
    public final boolean tagFilteredAsAttack;
    private CSVWriter writer;
    private String[] headers;

    public ComponentDumper(IntermodulePipe<PduAtomic> inQueue,
            String inPcapIf,
            Criteria[] criterias,
            File trainingFile,
            PacketFilter trainingFilter,
            boolean tagFilteredAsAttack) {
        super(inQueue, null);
        this.inPcapIf = inPcapIf;
        this.criterias = criterias;
        this.trainingFile = trainingFile;
        this.trainingFilter = trainingFilter;
        this.tagFilteredAsAttack = tagFilteredAsAttack;
    }

    @Override
    protected void preLoop() throws Exception {
        if (this.trainingFile == null) {
            throw new Exception("Error: [Dumper] trainingFile is null.");
        }
        this.writer = new CSVWriter(new FileWriter(this.trainingFile));
        String[] _iface = this.inPcapIf.split("[\\\\]");
        ArrayList<String> iface = new ArrayList();
        for (String _iface1 : _iface) {
            if (_iface1 != null) {
                if (!_iface1.isEmpty()) {
                    iface.add(_iface1);
                }
            }
        }
        this.headers = UtilsTraining.getHeaders(this.criterias, null);
        String[] _criterias = new String[this.criterias.length];
        for (int i = 0; i < this.criterias.length; i++) {
            _criterias[i] = this.criterias[i].expression;
        }
        this.writer.writeNext(iface.toArray(new String[0]));
        this.writer.writeNext(_criterias);
        this.writer.writeNext(this.headers);
        this.writer.flush();
    }

    @Override
    protected PduAtomic process(PduAtomic input) throws Exception {
        if (input.direction == TrafficDirection.Ingress) {
            boolean attack = this.tagFilteredAsAttack;
            if (this.trainingFilter != null) {
                attack = !(this.trainingFilter.matches(input.packet) ^ attack);
            }
            Object[] instance = UtilsTraining.getInstance(this.criterias, input, attack, null);
            if (this.headers.length != instance.length) {
                throw new Exception("Error: [Dumper] Headers do not match data.");
            }
            this.writer.writeNext(UtilsArray.toCsv(instance));
            this.writer.flush();
        } else {
            throw new Exception("Error: [Dumper] Encountered egress packet.");
        }
        return null;
    }

    @Override
    protected void postLoop() throws Exception {
        this.writer.close();
    }

    @Override
    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = super.getDiagnostics();
        if (this.inQueue != null) {
            diag.add(new Diagnostic("inqueue", "Inbound Queued Packets", this.inQueue.size()));
        } else {
            diag.add(new Diagnostic("inqueue", "Inbound Queued Packets", "N/A"));
        }
        return diag;
    }
}
