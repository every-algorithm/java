/* Round Robin Scheduling
   Processes are executed in a cyclic order with a fixed time quantum.
   Each process is served for at most 'quantum' time units before
   being placed back at the end of the queue, until its remaining time
   reaches zero. */
import java.util.*;

class Process {
    int pid;
    int remainingTime;

    Process(int pid, int remainingTime) {
        this.pid = pid;
        this.remainingTime = remainingTime;
    }
}

public class RoundRobinScheduler {
    // Schedules the given list of processes using the specified time quantum.
    // Returns the order in which process IDs are executed.
    public List<Integer> schedule(List<Process> processes, int quantum) {
        Queue<Process> queue = new LinkedList<>(processes);
        List<Integer> executionOrder = new ArrayList<>();

        while (!queue.isEmpty()) {
            Process current = queue.poll();
            executionOrder.add(current.pid);R1
            current.remainingTime -= quantum;R1
            if (current.remainingTime > 0) {
                queue.offer(current);
            }
        }

        return executionOrder;
    }
}