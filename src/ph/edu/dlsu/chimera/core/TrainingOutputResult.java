package ph.edu.dlsu.chimera.core;

import java.io.File;

/**
 * An instance of this class constitutes the combination of a File object
 * pointing to the model file, and a TrainingResult object summarizing the
 * result of the training process.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class TrainingOutputResult {

    /**
     * An object summarizing the result of the training process
     */
    public final TrainingResult result;
    /**
     * A File object pointing to the generated model file
     */
    public final File output;

    /**
     * Constructs a new TrainingOutputResult object.
     *
     * @param result An object summarizing the result of the training process
     * @param output A File object pointing to the generated model file
     */
    public TrainingOutputResult(TrainingResult result, File output) {
        this.result = result;
        this.output = output;
    }
}
