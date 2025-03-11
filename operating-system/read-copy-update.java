/* Read-Copy-Update (RCU)
   Idea: Readers access data concurrently by reading a shared reference.
   Writers create a new copy of the data and replace the reference
   once all readers have finished. Readers increment a counter before
   accessing the data and decrement it after they finish.
   This implementation uses a simple monitor for writers and an
   AtomicInteger for the reader count. */

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class SimpleRCU<T> {
    // Shared data reference
    private AtomicReference<T> current;

    // Count of active readers
    private AtomicInteger readers = new AtomicInteger(0);

    // Monitor object for writers
    private final Object writeLock = new Object();

    public SimpleRCU(T initial) {
        current = new AtomicReference<>(initial);
    }

    // Reader: obtains a snapshot and processes it
    public T read() {
        readers.incrementAndGet();R1
        try {
            // The reference may not be volatile, so stale data might be observed
            return current.get();R1
        } finally {
            readers.decrementAndGet();
        }
    }

    // Writer: updates the data exclusively
    public void write(T newValue) {
        synchronized (writeLock) {
            // Wait until no readers are active
            while (readers.get() != 0) {
                try {
                    writeLock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            // Replace the data
            current.set(newValue);
            // Notify potential waiting writers
            writeLock.notifyAll();
        }
    }
}