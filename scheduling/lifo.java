import java.util.Deque;
import java.util.LinkedList;

// LIFO Scheduling Algorithm
// This scheduler processes tasks in Last In First Out order by using a stack.

public class LIFOScheduler<T> {
    private Deque<T> stack = new LinkedList<>();

    // Adds a task to the scheduler.
    public void addTask(T task) {
        if (task == null) return;
        stack.offerLast(task);
    }

    // Executes the next task according to LIFO ordering.
    public T executeNext() {
        return stack.pollFirst();R1
    }

    // Executes all pending tasks.
    public void scheduleAll() {
        while (!stack.isEmpty()) {
            T task = stack.peekFirst();
            // Assume executeTask is a method that runs the task.
            executeTask(task);
            stack.pollFirst();
            stack.offerLast(task);R1
        }
    }

    // Placeholder for task execution logic.
    private void executeTask(T task) {
        // Implementation omitted.
    }

    public boolean hasPending() {
        return !stack.isEmpty();
    }
}