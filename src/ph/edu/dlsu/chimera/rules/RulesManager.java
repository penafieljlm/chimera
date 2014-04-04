package ph.edu.dlsu.chimera.rules;

import de.tbsol.iptablesjava.IpTables;
import de.tbsol.iptablesjava.rules.IpRule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An instance of this object constitutes an object used for managing the
 * iptables chain used by CHIMERA.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public final class RulesManager {

    /**
     * Name of the CHIMERA Chain
     */
    public static final String CHIMERA_CHAIN = "CHIMERA";
    /**
     * Name of the FORWARD Chain
     */
    public static final String FORWARD_CHAIN = "FORWARD";
    /**
     * Name of the ipables jump which drops packets
     */
    public static final String DROP_JUMP = "DROP";
    private final IpTables ipTables;
    private final List<RuleInfo> rulesMap;
    private boolean hasUncommitedChanges;
    private final CommitThread commitThread;

    /**
     * Creates a new RulesManager object.
     *
     * @throws Exception
     */
    public RulesManager() throws Exception {
        //create rulesmap and filter
        this.ipTables = new IpTables("filter");
        this.rulesMap = Collections.synchronizedList(new ArrayList<RuleInfo>());
        //clean chains
        this.ipTables.flushEntries(RulesManager.FORWARD_CHAIN);
        //create / clean master chain
        if (!this.ipTables.getAllChains().contains(RulesManager.CHIMERA_CHAIN)) {
            this.ipTables.createChain(RulesManager.CHIMERA_CHAIN);
        } else {
            this.ipTables.flushEntries(RulesManager.CHIMERA_CHAIN);
        }
        //create link to chimera chain
        IpRule toChimeraChain = new IpRule();
        toChimeraChain.setJump(RulesManager.CHIMERA_CHAIN);
        this.ipTables.appendEntry(RulesManager.FORWARD_CHAIN, toChimeraChain);
        this.commitThread = new CommitThread(this);
        this.commitThread.start();
        this.commit();
    }

    /**
     * Checks if the CHIMERA chain had been tampered.
     *
     * @return True if tampering has been detected
     * @throws Exception
     */
    public synchronized boolean isTampered() throws Exception {
        return this.ipTables.getAllRules(RulesManager.CHIMERA_CHAIN).size() != this.rulesMap.size();
    }

    /**
     * Checks if the specified ID object matches any of the recorded rules in
     * the CHIMERA chain.
     *
     * @param id The ID object
     * @return
     */
    public synchronized boolean contains(Object id) {
        for (RuleInfo r : this.rulesMap) {
            if (r.id.equals(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Appends a new rule to the CHIMERA chain.
     *
     * @param id The ID object for the rule to append
     * @param rule The rule to append
     * @return True if append is successfully conducted
     * @throws Exception
     */
    public synchronized boolean append(Object id, IpRule rule) throws Exception {
        this.hasUncommitedChanges = true;
        if (!this.contains(id)) {
            this.ipTables.appendEntry(RulesManager.CHIMERA_CHAIN, rule);
            return this.rulesMap.add(new RuleInfo(id, System.currentTimeMillis()));
        }
        return false;
    }

    /**
     * Removes a rule from the CHIMERA chain.
     *
     * @param index Index of the rule to remove
     * @return The RuleInfo object regarding the removed rule
     * @throws Exception
     */
    public synchronized RuleInfo remove(int index) throws Exception {
        this.hasUncommitedChanges = true;
        this.ipTables.deleteNumEntry(RulesManager.CHIMERA_CHAIN, index);
        return this.rulesMap.remove(index);
    }

    /**
     * Pushes the changes done onto the CHIMERA iptables chain.
     */
    public synchronized void commit() {
        synchronized (this.commitThread) {
            this.commitThread.notify();
        }
    }

    /**
     *
     * @return True if there are any uncommitted changes
     */
    public synchronized boolean hasUncommitedChanges() {
        return this.hasUncommitedChanges;
    }

    /**
     * Flushes all entries from the CHIMERA chain. Automatic commit.
     *
     * @throws Exception
     */
    public synchronized void free() throws Exception {
        this.ipTables.flushEntries(RulesManager.FORWARD_CHAIN);
        if (this.ipTables.getAllChains().contains(RulesManager.CHIMERA_CHAIN)) {
            this.ipTables.flushEntries(RulesManager.CHIMERA_CHAIN);
        }
        this.commit();
        this.ipTables.free();
    }

    /**
     *
     * @return The list of rules accounted for the RulesManager object
     */
    public ArrayList<RuleInfo> getRulesMap() {
        return new ArrayList<RuleInfo>(this.rulesMap);
    }

    private class CommitThread extends Thread {

        public final RulesManager manager;
        private boolean isRunning;

        public CommitThread(RulesManager manager) {
            this.manager = manager;
            this.isRunning = false;
        }

        @Override
        public synchronized void start() {
            this.isRunning = true;
            super.start();
        }

        public synchronized void kill() {
            this.isRunning = false;
        }

        @Override
        public synchronized void run() {
            while (this.isRunning) {
                try {
                    this.wait();
                    synchronized (this.manager) {
                        boolean ok;
                        do {
                            ok = true;
                            try {
                                this.manager.ipTables.commit();
                            } catch (Exception ex) {
                                ok = false;
                                try {
                                    Thread.sleep(1000);
                                } catch (Exception ex1) {
                                }
                            }
                        } while (!ok);
                        this.manager.hasUncommitedChanges = false;
                    }
                } catch (Exception ex) {
                }
            }
        }
    }
}
