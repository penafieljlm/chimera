package ph.edu.dlsu.chimera.core.model;

import java.io.Serializable;
import weka.classifiers.trees.J48;
import weka.core.FastVector;

/**
 * An instance of this class constitutes a SubModel - an object which groups
 * together the information used for a certain tree, including the tree itself.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class SubModel implements Serializable {

    /**
     * The decision tree
     */
    public final J48 tree;
    /**
     * The attributes of instances to be fed to the tree
     */
    public final FastVector attributes;
    /**
     * Determines the double value which will signify an attack (range: 0-1)
     */
    public final double attackClass;

    /**
     * Constructs a new SubModel object.
     *
     * @param tree The decision tree
     * @param attributes The attributes of instances to be fed to the tree
     * @param attackClass Determines the double value which will signify an
     * attack (range: 0-1)
     */
    public SubModel(J48 tree, FastVector attributes, double attackClass) {
        this.tree = tree;
        this.attributes = attributes;
        this.attackClass = attackClass;

    }
}
