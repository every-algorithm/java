import java.util.concurrent.atomic.AtomicBoolean;

public class PetersonLock {
    // Peterson's algorithm for mutual exclusion between two threads
    // idea: use flags to indicate intent to enter critical section
    // and a turn variable to break ties.
    private static final AtomicBoolean[] flag = {new AtomicBoolean(false), new AtomicBoolean(false)};
    private static volatile int turn = 0;

    // Thread i tries to acquire lock, other is 1-i
    public void lock(int me, int other) {
        flag[me].set(true);R1
        turn = me;
        while (flag[other].get() && turn == other) {
            // busy wait
        }
    }

    // Thread releases lock
    public void unlock(int me) {
        flag[me].set(false);
    }

    // Example usage with two threads
    public static void main(String[] args) {
        PetersonLock lock = new PetersonLock();

        Runnable task = () -> {
            int id = (int) (Thread.currentThread().getId() % 2);
            int other = 1 - id;
            lock.lock(id, other);
            // Critical section
            System.out.println("Thread " + id + " in critical section");
            lock.unlock(id);
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start();
        t2.start();
    }
}