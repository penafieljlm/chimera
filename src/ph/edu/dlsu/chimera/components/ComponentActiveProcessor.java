/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.components;

import java.util.ArrayList;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.core.Pdu;
import ph.edu.dlsu.chimera.core.Statistics;
import ph.edu.dlsu.chimera.core.tools.IntermodulePipe;

/**
 *
 * @author AMD
 * @param <TInput>
 * @param <TOutput>
 */
public abstract class ComponentActiveProcessor<TInput, TOutput> extends ComponentActive {

    public final IntermodulePipe<TInput> inQueue;
    public final IntermodulePipe<TOutput> outQueue;
    public final Statistics stats;

    public ComponentActiveProcessor(IntermodulePipe<TInput> inQueue, IntermodulePipe<TOutput> outQueue) {
        this.inQueue = inQueue;
        this.outQueue = outQueue;
        this.stats = new Statistics(System.currentTimeMillis() * 1000000);
    }

    @Override
    protected void componentRun() throws Exception {
        this.preLoop();
        while (super.running) {
            if (this.inQueue.isEmpty()) {
                synchronized (this) {
                    this.wait();
                }
            }
            if (this.inQueue != null) {
                while (!this.inQueue.isEmpty()) {
                    TInput in = this.inQueue.poll();
                    synchronized (in) {
                        TOutput out = this.process(in);
                        if (this.outQueue != null && out != null) {
                            this.outQueue.add(out);
                        }
                        this.stats.commitEncounter(this.getProcessedTimestampInNanos(in), this.getProcessedSize(in));
                    }
                }
            } else {
                throw new Exception("Error: [Processor Component] inQueue is null.");
            }
        }
        this.postLoop();
    }

    public abstract long getProcessedTimestampInNanos(TInput input);

    public abstract long getProcessedSize(TInput input);

    @Override
    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = super.getDiagnostics();
        diag.addAll(this.stats.getDiagnostics());
        return diag;
    }

    protected void preLoop() throws Exception {

    }

    protected void postLoop() throws Exception {

    }

    protected abstract TOutput process(TInput input) throws Exception;

}
