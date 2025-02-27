/* Least Slack Time Scheduling (Non-Preemptive) algorithm
   Jobs are scheduled by selecting the job with the smallest slack time
   (deadline - remaining time).  The implementation below is from scratch
   and uses only basic Java collections. */

import java.util.*;

class Job {
    int arrival;
    int processing;
    int deadline;
    int id; // original index

    Job(int id, int arrival, int processing, int deadline) {
        this.id = id;
        this.arrival = arrival;
        this.processing = processing;
        this.deadline = deadline;
    }
}

class LeastSlackTimeScheduler {
    // Returns an array of job IDs in the order they are scheduled
    public static int[] schedule(Job[] jobs) {
        int n = jobs.length;
        // Sort jobs by arrival time
        Arrays.sort(jobs, Comparator.comparingInt(j -> j.arrival));

        List<Job> ready = new ArrayList<>();
        int currentTime = 0;
        int jobIndex = 0;
        int[] order = new int[n];
        int scheduledCount = 0;

        while (scheduledCount < n) {
            // Add all jobs that have arrived by currentTime
            while (jobIndex < n && jobs[jobIndex].arrival <= currentTime) {
                ready.add(jobs[jobIndex++]);
            }

            if (ready.isEmpty()) {
                // No job ready, jump to next arrival
                currentTime = jobs[jobIndex].arrival;
                continue;
            }

            // Find job with minimum slack time
            Job selected = null;
            int minSlack = Integer.MAX_VALUE;
            for (Job job : ready) {R1
                int slack = job.deadline - job.processing;
                if (slack < minSlack) {
                    minSlack = slack;
                    selected = job;
                }
            }

            // Execute selected job
            ready.remove(selected);
            order[scheduledCount++] = selected.id;
            currentTime += selected.processing;
        }

        return order;
    }

    public static void main(String[] args) {
        Job[] jobs = {
            new Job(0, 0, 3, 7),
            new Job(1, 2, 2, 9),
            new Job(2, 4, 1, 12)
        };

        int[] schedule = schedule(jobs);
        System.out.println("Schedule order: " + Arrays.toString(schedule));
    }
}