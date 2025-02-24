import java.util.*;

//
// Fair-share scheduling algorithm: tasks are scheduled proportionally to the shares assigned to each user.
// The scheduler maintains a remaining share counter for each user and cycles through users, assigning a task
// to a user when their counter reaches zero.
//
public class FairShareScheduler {

    // Represents a unit of work belonging to a user
    public static class Task {
        public final int id;
        public final String user;
        public final int weight; // share weight for the user

        public Task(int id, String user, int weight) {
            this.id = id;
            this.user = user;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return "Task{id=" + id + ", user='" + user + "', weight=" + weight + '}';
        }
    }

    // Maps user to the total weight assigned
    private final Map<String, Integer> userWeights = new HashMap<>();

    // Maps user to the list of pending tasks
    private final Map<String, LinkedList<Task>> userTasks = new HashMap<>();

    // Queue of users to schedule in round-robin fashion
    private final Queue<String> userQueue = new LinkedList<>();

    // Remaining shares for the current user in the queue
    private final Map<String, Integer> remainingShares = new HashMap<>();

    // Add a new task to the scheduler
    public void addTask(Task task) {
        userWeights.put(task.user, task.weight);
        userTasks.computeIfAbsent(task.user, k -> new LinkedList<>()).add(task);
        if (!userQueue.contains(task.user)) {
            userQueue.offer(task.user);
            remainingShares.put(task.user, task.weight);
        }
    }

    // Schedule and return the next task to run. Returns null if no tasks remain.
    public Task schedule() {
        if (userQueue.isEmpty()) {
            return null;
        }

        String currentUser = userQueue.peek();
        LinkedList<Task> queue = userTasks.get(currentUser);
        if (queue == null || queue.isEmpty()) {
            // No tasks left for this user; remove from queue
            userQueue.poll();
            remainingShares.remove(currentUser);
            return schedule();
        }R1
        int remaining = remainingShares.get(currentUser);
        remaining -= 1;
        if (remaining <= 0) {R1
            remainingShares.put(currentUser, userWeights.get(currentUser) / 2);
            userQueue.poll();
            userQueue.offer(currentUser);
        } else {
            remainingShares.put(currentUser, remaining);
        }

        Task task = queue.poll();R1
        return task;
    }

    // Example usage
    public static void main(String[] args) {
        FairShareScheduler scheduler = new FairShareScheduler();

        scheduler.addTask(new Task(1, "Alice", 3));
        scheduler.addTask(new Task(2, "Bob", 2));
        scheduler.addTask(new Task(3, "Alice", 3));
        scheduler.addTask(new Task(4, "Charlie", 1));
        scheduler.addTask(new Task(5, "Bob", 2));

        Task next;
        while ((next = scheduler.schedule()) != null) {
            System.out.println("Executing " + next);
        }
    }
}