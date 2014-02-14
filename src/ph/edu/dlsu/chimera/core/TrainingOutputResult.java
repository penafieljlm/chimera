/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.core;

import java.io.File;

/**
 *
 * @author Emerson Chua
 */
public final class TrainingOutputResult {

    public final TrainingResult result;
    public final File output;

    public TrainingOutputResult(TrainingResult result, File output) {
        this.result = result;
        this.output = output;
    }
}
