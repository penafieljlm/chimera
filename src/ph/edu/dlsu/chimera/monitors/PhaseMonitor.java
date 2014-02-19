package ph.edu.dlsu.chimera.monitors;

import java.util.HashMap;
import ph.edu.dlsu.chimera.components.Component;
import ph.edu.dlsu.chimera.components.ComponentActiveProcessor;
import ph.edu.dlsu.chimera.core.Statistics;

/**
 *
 * @author AMD
 */
public abstract class PhaseMonitor {

    public final long updateInterval;
    public final HashMap<String, Statistics[]> statisticsGraph;
    private boolean terminate;
    private HashMap<String, Component> components;

    public PhaseMonitor(long updateInterval) {
        this.updateInterval = updateInterval;
        this.statisticsGraph = new HashMap<String, Statistics[]>();
        this.terminate = false;
    }

    protected abstract void update();

    public boolean isTerminated() {
        return terminate;
    }

    public void terminate() {
        this.terminate = true;
    }

    public void setComponents(HashMap<String, Component> components) {
        this.components = components;
        for (String _c : this.components.keySet()) {
            Component c = this.components.get(_c);
            if (c instanceof ComponentActiveProcessor) {
                ComponentActiveProcessor cap = (ComponentActiveProcessor) c;
                this.statisticsGraph.put(_c, new Statistics[20]);
            }
        }
    }

    public HashMap<String, Component> getComponents() {
        return this.components;
    }

    public void createGraphDataPoint() {
        for (String _c : this.statisticsGraph.keySet()) {
            Statistics[] cq = this.statisticsGraph.get(_c);
            //shift right
            for (int i = 0; i < cq.length && cq.length - i - 2 >= 0; i++) {
                cq[cq.length - i - 1] = cq[cq.length - i - 2];
            }
            //commit current stats
            Component c = this.components.get(_c);
            if (c instanceof ComponentActiveProcessor) {
                ComponentActiveProcessor cap = (ComponentActiveProcessor) c;
                cq[0] = cap.stats.copy();
            } else {
                cq[0] = null;
            }
        }
    }
}
