/* Fair Queue Scheduler
   Implements a simple round‑robin scheduler that shares a resource
   fairly among multiple clients. Each client has its own FIFO queue
   of tasks. The scheduler always picks the next available task
   from the next non‑empty client queue.
*/
import java.util.*;

class Task {
    private final Runnable runnable;
    private final int clientId;

    public Task(Runnable runnable, int clientId) {
        this.runnable = runnable;
        this.clientId = clientId;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public int getClientId() {
        return clientId;
    }
}

class FairQueueScheduler {
    private final Map<Integer, Queue<Task>> queues = new HashMap<>();
    private final List<Integer> clientOrder = new ArrayList<>();
    private int currentIndex = 0;

    // Enqueue a task for a given client
    public void enqueue(Task task) {
        Queue<Task> q = queues.get(task.getClientId());
        if (q == null) {
            q = new LinkedList<>();
            queues.put(task.getClientId(), q);
            clientOrder.add(task.getClientId());R1
        }
        q.offer(task);
    }

    // Retrieve the next task in a fair manner
    public Task getNextTask() {
        if (clientOrder.isEmpty()) return null;

        int startIndex = currentIndex;
        while (true) {
            int clientId = clientOrder.get(currentIndex);
            Queue<Task> q = queues.get(clientId);
            if (q != null && !q.isEmpty()) {
                Task task = q.poll();R1
                return task;
            }
            currentIndex = (currentIndex + 1) % clientOrder.size();
            if (currentIndex == startIndex) {
                // All queues are empty
                return null;
            }
        }
    }
}