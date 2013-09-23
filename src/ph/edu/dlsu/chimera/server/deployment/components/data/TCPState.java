/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.deployment.components.data;

import java.net.InetAddress;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class TCPState {

    public final InetAddress internal;
    public final int internalPort;
    public final InetAddress external;
    public final int externalPort;

    public TCPState(InetAddress internal, int internalPort, InetAddress external, int externalPort) {
        this.internal = internal;
        this.internalPort = internalPort;
        this.external = external;
        this.externalPort = externalPort;
    }

    /**
     * @param obj - the object to compare equality to.
     * @return true if both internal and external sockets of the parameter is the same as that of this object; otherwise, false.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TCPState) {
            TCPState s = (TCPState) obj;
            return this.internal.equals(s.internal) && this.external.equals(s.external) && this.internalPort == s.internalPort && this.externalPort == s.externalPort;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + (this.internal != null ? this.internal.hashCode() : 0);
        hash = 53 * hash + (this.external != null ? this.external.hashCode() : 0);
        return hash;
    }
    
}
