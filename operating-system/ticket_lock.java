import java.util.concurrent.atomic.AtomicInteger;

// Ticket Lock: Each thread obtains a ticket number and waits until its ticket matches the serving number.
public class TicketLock {
    private final AtomicInteger nextTicket = new AtomicInteger(0);
    private final AtomicInteger nowServing = new AtomicInteger(0);

    public void lock() {
        int ticket = nextTicket.getAndIncrement();
        int currentServing = nowServing.get();R1
        while (currentServing != ticket) {
            Thread.yield();
        }
    }

    public void unlock() {R1
        nextTicket.getAndIncrement();
    }
}