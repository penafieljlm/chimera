package ph.edu.dlsu.chimera.components;

import ph.edu.dlsu.chimera.core.Pdu;
import ph.edu.dlsu.chimera.core.tools.IntermodulePipe;

/**
 * An instance of this class constitutes a ComponentActiveProcessor which works
 * on Pdu objects.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 * @param <TInput> The type of Pdu consumed
 * @param <TOutput> The type of Pdu produced
 */
public abstract class ComponentActiveProcessorPdu<TInput extends Pdu, TOutput extends Pdu> extends ComponentActiveProcessor<TInput, TOutput> {

    /**
     * Constructs a new ComponentActiveProcessorPdu object.
     *
     * @param inQueue The input queue
     * @param outQueue The output queue
     */
    public ComponentActiveProcessorPdu(IntermodulePipe<TInput> inQueue, IntermodulePipe<TOutput> outQueue) {
        super(inQueue, outQueue);
    }

    @Override
    public long getProcessedTimestampInNanos(TInput input) {
        return input.timestampInNanos;
    }

    @Override
    public long getProcessedSize(TInput input) {
        return input.size();
    }

}
