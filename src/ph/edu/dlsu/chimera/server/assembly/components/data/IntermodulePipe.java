/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.edu.dlsu.chimera.server.assembly.components.data;

import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.server.assembly.components.ComponentActive;

/**
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 */
public class IntermodulePipe<TData> {

    private final ConcurrentLinkedQueue<TData> queue;
    private ComponentActive writer;
    private ComponentActive reader;

    public IntermodulePipe() {
        this.queue = new ConcurrentLinkedQueue<>();
        this.writer = null;
        this.reader = null;
    }

    public void setReader(ComponentActive reader) {
        if (this.reader == null) {
            this.reader = reader;
        } else {
            throw new IllegalStateException("Reader component can only be set once per IntermodulePipe.");
        }
    }

    public void setWriter(ComponentActive writer) {
        if (this.writer == null) {
            this.writer = writer;
        } else {
            throw new IllegalStateException("Writer component can only be set once per IntermodulePipe.");
        }
    }

    public boolean add(TData o) {
        if (this.reader != null && this.writer != null) {
            boolean add = this.queue.add(o);
            synchronized (this.reader) {
                this.reader.notify();
            }
            return add;
        } else {
            throw new IllegalStateException("Reader and writer must be set before performing any operations.");
        }
    }

    public TData poll() {
        if (this.reader != null && this.writer != null) {
            return this.queue.poll();
        } else {
            throw new IllegalStateException("Reader and writer must be set before performing any operations.");
        }
    }

    public boolean isEmpty() {
        return this.queue.isEmpty();
    }

    public int size() {
        return this.queue.size();
    }
}
