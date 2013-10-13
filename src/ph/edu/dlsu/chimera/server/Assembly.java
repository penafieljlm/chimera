/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ph.edu.dlsu.chimera.server;

import java.util.Collections;
import java.util.List;
import ph.edu.dlsu.chimera.server.deployment.Deployment;
import ph.edu.dlsu.chimera.server.admin.AdministrativeModule;
import ph.edu.dlsu.chimera.server.admin.UserBase;
import ph.edu.dlsu.chimera.server.deployment.components.data.stats.Criteria;
import ph.edu.dlsu.chimera.server.deployment.components.data.stats.CriteriaIpDst;
import ph.edu.dlsu.chimera.server.deployment.components.data.stats.CriteriaIpSrc;
import ph.edu.dlsu.chimera.server.deployment.components.data.stats.CriteriaIpSrcDst;
import ph.edu.dlsu.chimera.server.deployment.components.data.stats.CriteriaIpTcpDst;
import ph.edu.dlsu.chimera.server.deployment.components.data.stats.CriteriaIpTcpDstSyn;
import ph.edu.dlsu.chimera.server.deployment.components.data.stats.CriteriaIpTcpSrc;
import ph.edu.dlsu.chimera.server.deployment.components.data.stats.CriteriaIpTcpSrcDst;
import ph.edu.dlsu.chimera.server.deployment.components.data.stats.CriteriaIpTcpSrcDstSyn;
import ph.edu.dlsu.chimera.server.deployment.components.data.stats.CriteriaIpTcpSrcSyn;

/**
 * 
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class Assembly {

    public final AdministrativeModule admin;
    public final UserBase users;

    /**
     * The assembly of current active modules.
     */
    private Deployment deployment;

    /**
     * The criterias that the system uses for measuring statistics.
     */
    public final List<Criteria> criterias;

    public Assembly(int port) {
        this.admin = new AdministrativeModule(this, port);
        this.users = new UserBase();
        this.criterias = Collections.synchronizedList(Collections.EMPTY_LIST);
        this.criterias.add(new CriteriaIpDst());
        this.criterias.add(new CriteriaIpSrc());
        this.criterias.add(new CriteriaIpSrcDst());
        this.criterias.add(new CriteriaIpTcpDst());
        this.criterias.add(new CriteriaIpTcpDstSyn());
        this.criterias.add(new CriteriaIpTcpSrc());
        this.criterias.add(new CriteriaIpTcpSrcDst());
        this.criterias.add(new CriteriaIpTcpSrcDstSyn());
        this.criterias.add(new CriteriaIpTcpSrcSyn());
    }

    /**
     * Aborts the current deployment and starts a new one.
     * @param deployment
     */
    public void setDeployment(Deployment deployment) {
        if(this.deployment != null)
            this.deployment.killDeployment();
        this.deployment = deployment;
        if(this.deployment != null)
            this.deployment.startDeployment();
    }

    /**
     * @return the current deployment; null if there is no current deployment
     */
    public Deployment getDeployment() {
        return this.deployment;
    }

    public void startAdmin() {
        this.admin.start();
    }

}
