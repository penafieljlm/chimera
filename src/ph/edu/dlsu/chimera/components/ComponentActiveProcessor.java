package ph.edu.dlsu.chimera.components;

import java.util.ArrayList;
import ph.edu.dlsu.chimera.core.Diagnostic;
import ph.edu.dlsu.chimera.core.Statistics;
import ph.edu.dlsu.chimera.core.tools.IntermodulePipe;

/**
 * An instance of this class constitutes a ComponentActive object which takes in
 * input objects from a queue and places output objects onto another queue.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 * @param <TInput> The type of input objects consumed
 * @param <TOutput> The type of output objects produced
 */
public abstract class ComponentActiveProcessor<TInput, TOutput> extends ComponentActive {

    /**
     * The input queue
     */
    public final IntermodulePipe<TInput> inQueue;
    /**
     * The output queue
     */
    public final IntermodulePipe<TOutput> outQueue;
    /**
     * Statistics monitor for the ingress queue
     */
    public final Statistics ingressStats;
    /**
     * Statistics monitor for the egress queue
     */
    public final Statistics egressStats;

    /**
     * Constructs a new ComponentActiveProcessor object.
     *
     * @param inQueue The input queue
     * @param outQueue The output queue
     */
    public ComponentActiveProcessor(IntermodulePipe<TInput> inQueue, IntermodulePipe<TOutput> outQueue) {
        this.inQueue = inQueue;
        this.outQueue = outQueue;
        this.ingressStats = new Statistics(System.currentTimeMillis() * 1000000);
        this.egressStats = new Statistics(System.currentTimeMillis() * 1000000);
    }

    @Override
    protected void componentRun() throws Exception {
        this.preLoop();
        while (super.running) {
            if (this.inQueue != null) {
                TInput in = this.inQueue.poll();
                synchronized (in) {
                    this.ingressStats.commitEncounter(this.getProcessedTimestampInNanos(in), this.getProcessedSize(in));
                    TOutput out = this.process(in);
                    if (this.outQueue != null && out != null) {
                        this.outQueue.add(out);
                        this.egressStats.commitEncounter(this.getProcessedTimestampInNanos(in), this.getProcessedSize(in));
                    }
                }
            } else {
                throw new Exception("Error: [Processor Component] inQueue is null.");
            }
        }
        this.postLoop();
    }

    /**
     * A handler function which should return the timestamp that an input object
     * was received.
     *
     * @param input The input object
     * @return The timestamp with which the input was received
     */
    public abstract long getProcessedTimestampInNanos(TInput input);

    /**
     * A handler function which should return the size of an input object (if
     * size is actually measured).
     *
     * @param input
     * @return
     */
    public abstract long getProcessedSize(TInput input);

    @Override
    public synchronized ArrayList<Diagnostic> getDiagnostics() {
        ArrayList<Diagnostic> diag = super.getDiagnostics();
        diag.addAll(this.ingressStats.getDiagnostics());
        return diag;
    }

    /**
     * Function invoked before the process loop is called.
     *
     * @throws Exception
     */
    protected void preLoop() throws Exception {

    }

    /**
     * Function invoked after the process loop is called.
     *
     * @throws Exception
     */
    protected void postLoop() throws Exception {

    }

    /**
     * An iteration of the process loop. Individually process an input object.
     *
     * @param input The input object to be processed
     * @return The result of the process
     * @throws Exception
     */
    protected abstract TOutput process(TInput input) throws Exception;

}
