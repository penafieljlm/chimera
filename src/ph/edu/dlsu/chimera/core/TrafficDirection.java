package ph.edu.dlsu.chimera.core;

/**
 * Enumerates the three possible directions of traffic in the system: Ingress
 * (Network Inbound), Egress (Network Outbound), and None (System Generated)
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public enum TrafficDirection {

    /**
     * Network Inbound (Traffic coming into the network)
     */
    Ingress,
    /**
     * Network Outbound (Traffic going out of the network)
     */
    Egress,
    /**
     * None (Error or system generated traffic)
     */
    None
}
