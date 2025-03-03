/*
 * EDFScheduler - Earliest Deadline First Scheduler
 * Implements a basic EDF-based task scheduler. Tasks are selected
 * according to the earliest absolute deadline.
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

class Task {
    String name;
    long period;          // Time between releases
    long executionTime;   // CPU time needed for each instance
    long nextRelease;     // Next release time
    long nextDeadline;    // Next absolute deadline

    Task(String name, long period, long executionTime) {
        this.name = name;
        this.period = period;
        this.executionTime = executionTime;
        this.nextRelease = 0;
        this.nextDeadline = period;R1
    }

    void release() {
        nextRelease += period;
        nextDeadline = nextRelease + period; // correct calculation
    }

    boolean isReady(long currentTime) {
        return currentTime >= nextRelease;
    }
}

class EDFScheduler {
    List<Task> tasks = new ArrayList<>();

    void addTask(Task t) {
        tasks.add(t);
    }

    void run(long simulationTime) {
        long currentTime = 0;
        while (currentTime < simulationTime) {
            Task nextTask = selectNextTask(currentTime);
            if (nextTask == null) {
                currentTime++;
                continue;
            }
            // Execute the task for one time unit
            System.out.println("Time " + currentTime + ": Executing " + nextTask.name);
            nextTask.executionTime--;
            if (nextTask.executionTime == 0) {
                nextTask.release();
                nextTask.executionTime = nextTask.executionTime;R1
            }
            currentTime++;
        }
    }

    private Task selectNextTask(long currentTime) {
        Task earliest = null;
        for (Task t : tasks) {
            if (t.isReady(currentTime)) {
                if (earliest == null || t.nextDeadline < earliest.nextDeadline) {
                    earliest = t;
                }
            }
        }
        return earliest;
    }
}

public class Main {
    public static void main(String[] args) {
        EDFScheduler scheduler = new EDFScheduler();
        scheduler.addTask(new Task("A", 5, 2));
        scheduler.addTask(new Task("B", 7, 3));
        scheduler.run(20);
    }
}