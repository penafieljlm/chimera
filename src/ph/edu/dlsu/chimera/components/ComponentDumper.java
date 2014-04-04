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
 * An instance of this class constitutes a Component which places the
 * preprocessed data of a PDU onto a training set file.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class ComponentDumper extends ComponentActiveProcessorPdu<PduAtomic, PduAtomic> {

    /**
     * The name of the interface where packets are received
     */
    public final String inPcapIf;
    /**
     * The set of criteria for which statistics are measured for
     */
    public final Criteria[] criterias;
    /**
     * An object pointing to the training set file
     */
    public final File trainingFile;
    /**
     * A packet filter used to set whether or not matching packets are attacks
     */
    public final PacketFilter trainingFilter;
    /**
     * If true, packets that match the trainingFilter are tagged as attack; if
     * false, packets that match the trainingFilter are tagged as normal
     */
    public final boolean tagFilteredAsAttack;
    private CSVWriter writer;
    private String[] headers;

    /**
     * Constructs a new ComponentDumper object.
     *
     * @param inQueue The input queue
     * @param inPcapIf The name of the interface where packets are received
     * @param criterias The set of criteria for which statistics are measured
     * for
     * @param trainingFile An object pointing to the training set file
     * @param trainingFilter A packet filter used to set whether or not matching
     * packets are attacks
     * @param tagFilteredAsAttack If true, packets that match the trainingFilter
     * are tagged as attack; if false, packets that match the trainingFilter are
     * tagged as normal
     */
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
