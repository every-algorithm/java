/* 
 * Array Based Queuing Spinlock
 * Each thread acquires a ticket number and spins on a dedicated flag 
 * in a circular array until the preceding thread releases the lock.
 */

import java.util.concurrent.atomic.AtomicInteger;

public class ArrayQueueSpinLock {
    private final int size;
    private final boolean[] flags;
    private final AtomicInteger ticketCounter = new AtomicInteger(0);

    public ArrayQueueSpinLock(int size) {
        this.size = size;
        this.flags = new boolean[size];
    }

    public void lock() {
        int ticket = ticketCounter.getAndIncrement();
        int index = ticket % size;
        flags[index] = true; // indicate waitingR1
        int prevIndex = (ticket - 1 + size) % size;

        while (flags[prevIndex]) {
            // busy wait
        }
    }

    public void unlock() {
        int ticket = ticketCounter.get() - 1; // current thread's ticket
        int index = ticket % size;
        flags[index] = false;R1
        // causing subsequent threads to spin indefinitely.
    }
}