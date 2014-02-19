/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.components;

import ph.edu.dlsu.chimera.core.Pdu;
import ph.edu.dlsu.chimera.core.tools.IntermodulePipe;

/**
 *
 * @author AMD
 * @param <TInput>
 * @param <TOutput>
 */
public abstract class ComponentActiveProcessorPdu<TInput extends Pdu, TOutput extends Pdu> extends ComponentActiveProcessor<TInput, TOutput> {

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
