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
import ph.edu.dlsu.chimera.server.core.Criteria;
import ph.edu.dlsu.chimera.server.deployment.components.data.stats.CriteriaIpDst;
import ph.edu.dlsu.chimera.server.deployment.components.data.stats.CriteriaIpSrc;
import ph.edu.dlsu.chimera.server.deployment.components.data.stats.CriteriaIpSrcDst;
import ph.edu.dlsu.chimera.server.deployment.components.data.stats.CriteriaIpTcpDst;
import ph.edu.dlsu.chimera.server.deployment.components.data.stats.CriteriaAtomicIpTcpDstSyn;
import ph.edu.dlsu.chimera.server.deployment.components.data.stats.CriteriaAtomicIpTcpSrc;
import ph.edu.dlsu.chimera.server.deployment.components.data.stats.CriteriaAtomicIpTcpSrcDst;
import ph.edu.dlsu.chimera.server.deployment.components.data.stats.CriteriaAtomicIpTcpSrcDstSyn;
import ph.edu.dlsu.chimera.server.deployment.components.data.stats.CriteriaAtomicIpTcpSrcSyn;

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
     * The criteriasAtomic that the system uses for measuring statistics.
     */
    public final List<Criteria> criteriasAtomic;

    public Assembly(int port) {
        this.admin = new AdministrativeModule(this, port);
        this.users = new UserBase();
        this.criteriasAtomic = Collections.synchronizedList(Collections.EMPTY_LIST);
        this.criteriasAtomic.add(new CriteriaIpDst());
        this.criteriasAtomic.add(new CriteriaIpSrc());
        this.criteriasAtomic.add(new CriteriaIpSrcDst());
        this.criteriasAtomic.add(new CriteriaIpTcpDst());
        this.criteriasAtomic.add(new CriteriaAtomicIpTcpDstSyn());
        this.criteriasAtomic.add(new CriteriaAtomicIpTcpSrc());
        this.criteriasAtomic.add(new CriteriaAtomicIpTcpSrcDst());
        this.criteriasAtomic.add(new CriteriaAtomicIpTcpSrcDstSyn());
        this.criteriasAtomic.add(new CriteriaAtomicIpTcpSrcSyn());
    }

    /**
     * Aborts the current deployment and starts a new one.
     * @param deployment
     */
    public void setDeployment(Deployment deployment) {
        if (this.deployment != null) {
            this.deployment.killDeployment();
        }
        this.deployment = deployment;
        if (this.deployment != null) {
            this.deployment.startDeployment();
        }
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
