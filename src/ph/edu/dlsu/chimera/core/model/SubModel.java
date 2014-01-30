/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.core.model;

import java.io.Serializable;
import weka.classifiers.trees.J48;
import weka.core.FastVector;

/**
 *
 * @author AMD
 */
public final class SubModel implements Serializable {

    public final J48 tree;
    public final FastVector attributes;
    public final double attackClass;

    public SubModel(J48 tree, FastVector attributes, double attackClass) {
        this.tree = tree;
        this.attributes = attributes;
        this.attackClass = attackClass;
        
    }
}
