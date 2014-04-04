package ph.edu.dlsu.chimera.core.tools;

import java.util.concurrent.ConcurrentLinkedQueue;
import ph.edu.dlsu.chimera.components.ComponentActive;

/**
 * An instance of this class constitutes an object which controls data flow from
 * one module to another. It could be imagined as a pipe connecting two
 * different components. Data flows in only one direction; one component to
 * another.
 *
 * @author John Lawrence M. Penafiel <penafieljlm@gmail.com>
 * @param <TData> The type of data that can flow inside the pipe
 */
public class IntermodulePipe<TData> {

    private final ConcurrentLinkedQueue<TData> queue;
    private ComponentActive writer;
    private ComponentActive reader;

    /**
     * Constructs a new IntermodulePipe object.
     */
    public IntermodulePipe() {
        this.queue = new ConcurrentLinkedQueue<TData>();
        this.writer = null;
        this.reader = null;
    }

    /**
     * Sets the reader to this IntermodulePipe.
     *
     * @param reader The reader
     */
    public void setReader(ComponentActive reader) {
        if (this.reader == null) {
            this.reader = reader;
        } else {
            throw new IllegalStateException("Reader component can only be set once per IntermodulePipe.");
        }
    }

    /**
     * Sets the writer to this IntermodulePipe.
     *
     * @param writer The writer
     */
    public void setWriter(ComponentActive writer) {
        if (this.writer == null) {
            this.writer = writer;
        } else {
            throw new IllegalStateException("Writer component can only be set once per IntermodulePipe.");
        }
    }

    /**
     * Adds data to this IntermodulePipe. Can only be called by the set writer.
     *
     * @param o The data
     * @return True if successful
     */
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

    /**
     * Polls data from this IntermodulePipe. Can only be called by the set
     * reader.
     *
     * @return The polled data; null if unsuccessful
     * @throws InterruptedException
     */
    public TData poll() throws InterruptedException {
        if (this.reader != null && this.writer != null) {
            while (this.queue.isEmpty()) {
                synchronized (this.reader) {
                    this.reader.wait();
                }
            }
            return this.queue.poll();
        } else {
            throw new IllegalStateException("Reader and writer must be set before performing any operations.");
        }
    }

    /**
     *
     * @return True if this IntermodulePipe is empty
     */
    public boolean isEmpty() {
        return this.queue.isEmpty();
    }

    /**
     *
     * @return The size of this IntermodulePipe
     */
    public int size() {
        return this.queue.size();
    }

    /**
     * Clears this IntermodulePipe
     */
    public void clear() {
        this.queue.clear();
    }
}
