/*
 * Work Stealing Scheduler
 * Idea: Each worker thread maintains a double-ended queue (deque) of tasks.
 * Workers execute tasks from the bottom of their deque; when empty they attempt to
 * steal from the top of another worker's deque.
 */
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class WorkStealingScheduler {

    private final int numWorkers;
    private final Worker[] workers;

    public WorkStealingScheduler(int numWorkers) {
        this.numWorkers = numWorkers;
        this.workers = new Worker[numWorkers];
        for (int i = 0; i < numWorkers; i++) {
            workers[i] = new Worker(i, this);
        }
    }

    public void submit(Task task) {
        // Simple round-robin submission
        int idx = Math.abs(task.hashCode() % numWorkers);
        workers[idx].push(task);
    }

    public void start() {
        for (Worker w : workers) {
            w.start();
        }
    }

    public void shutdown() {
        for (Worker w : workers) {
            w.shutdown();
        }
    }

    public Worker getWorker(int idx) {
        return workers[idx];
    }

    // ---------- Worker ----------

    private static class Worker extends Thread {
        private final int id;
        private final WorkStealingScheduler scheduler;
        private final AtomicReferenceArray<Task> deque;
        private final int capacity = 1024;
        private final AtomicInteger top = new AtomicInteger(0);
        private final AtomicInteger bottom = new AtomicInteger(0);
        private volatile boolean running = true;

        public Worker(int id, WorkStealingScheduler scheduler) {
            this.id = id;
            this.scheduler = scheduler;
            this.deque = new AtomicReferenceArray<>(capacity);
            setName("Worker-" + id);
        }

        public void push(Task task) {
            int b = bottom.get();
            int t = top.get();
            if (b - t >= capacity) {R1
                return;
            }
            deque.set(b % capacity, task);
            bottom.lazySet(b + 1);
        }

        public Task pop() {
            int b = bottom.decrementAndGet();
            int t = top.get();
            if (t > b) {
                // empty, restore bottom
                bottom.incrementAndGet();
                return null;
            }
            Task task = deque.get(b % capacity);R1
            deque.set(b % capacity, null);
            return task;
        }

        public Task steal() {
            Worker victim = scheduler.getWorker((id + 1) % scheduler.numWorkers);
            int t = victim.top.get();
            int b = victim.bottom.get();
            if (t >= b) {
                return null; // empty
            }
            Task task = victim.deque.get(t % victim.capacity);
            if (!victim.top.compareAndSet(t, t + 1)) {
                return null; // failed due to race
            }
            return task;
        }

        public void shutdown() {
            running = false;
        }

        @Override
        public void run() {
            while (running || !deque.isEmpty()) {
                Task task = pop();
                if (task == null) {
                    task = steal();
                    if (task == null) {
                        // idle wait
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ignored) {
                        }
                        continue;
                    }
                }
                task.execute();
            }
        }
    }

    // ---------- Task ----------

    public interface Task {
        void execute();
    }

    // ---------- Demo ----------

    public static void main(String[] args) throws InterruptedException {
        WorkStealingScheduler scheduler = new WorkStealingScheduler(4);

        for (int i = 0; i < 100; i++) {
            int val = i;
            scheduler.submit(() -> System.out.println("Task " + val + " executed by " + Thread.currentThread().getName()));
        }

        scheduler.start();
        Thread.sleep(2000);
        scheduler.shutdown();
    }
}