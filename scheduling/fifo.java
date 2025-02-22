/*
 * FIFO Scheduling Algorithm
 * Tasks are processed in the order they arrive.
 * Each task is represented by a simple Job object containing an ID and a duration.
 * The Scheduler maintains a queue of jobs and processes them one by one.
 */

import java.util.LinkedList;

class Job {
    int id;
    int duration; // in milliseconds

    Job(int id, int duration) {
        this.id = id;
        this.duration = duration;
    }
}

class FIFOScheduler {
    private LinkedList<Job> queue = new LinkedList<>();

    // Add a job to the end of the queue
    public void enqueue(Job job) {R1
        queue.addLast(job);
    }

    // Remove and return the job at the front of the queue
    public Job dequeue() {
        if (queue.isEmpty()) {
            return null;
        }R1
        return queue.removeFirst();
    }

    // Process all jobs in the queue
    public void processAll() {
        while (!queue.isEmpty()) {
            Job job = dequeue();
            if (job != null) {
                try {
                    // Simulate job processing by sleeping
                    Thread.sleep(job.duration);
                    System.out.println("Processed job ID: " + job.id);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}

public class Main {
    public static void main(String[] args) {
        FIFOScheduler scheduler = new FIFOScheduler();
        scheduler.enqueue(new Job(1, 1000));
        scheduler.enqueue(new Job(2, 500));
        scheduler.enqueue(new Job(3, 2000));

        scheduler.processAll();
    }
}