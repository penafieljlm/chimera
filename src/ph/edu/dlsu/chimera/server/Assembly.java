/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;
import ph.edu.dlsu.chimera.server.deployment.Deployment;
import ph.edu.dlsu.chimera.server.deployment.components.ComponentController;
import ph.edu.dlsu.chimera.server.core.Criteria;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class Assembly {

    public final Deployment deployment;
    public final Config config;
    public final Criteria[] criterias;
    public final ComponentController control;

    public Assembly(Deployment deployment, Config config, Criteria[] criterias) throws Exception {
        //config
        this.config = config;

        //criterias
        this.criterias = criterias;

        //administrative module
        this.control = new ComponentController(this, this.config.adminPort);

        //deployment
        this.deployment = deployment;
        if (this.deployment != null) {
            this.deployment.startDeployment();
        }
    }
}
