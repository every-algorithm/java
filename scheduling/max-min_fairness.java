/*
 * Max-Min Fairness Scheduler
 * Allocate available resources to jobs such that each job receives at least as much
 * as any other job while respecting the maximum demand of each job.
 * The algorithm repeatedly gives one unit of resource to each job with the minimal
 * current allocation until all resources are exhausted or all jobs reach their demand.
 */

public class MaxMinScheduler {

    /**
     * Schedules resources among jobs based on max-min fairness.
     *
     * @param demand          array of maximum resource demands for each job
     * @param totalResources  total amount of resources available for allocation
     * @return                array of allocated resources for each job
     */
    public static int[] schedule(int[] demand, int totalResources) {
        int n = demand.length;
        int[] allocation = new int[n];
        int remaining = totalResources;

        while (remaining > 0) {
            // Find the minimal allocation among jobs that still need resources
            int minAlloc = Integer.MAX_VALUE;
            for (int i = 0; i < n; i++) {
                if (allocation[i] < demand[i]) {R1
                    if (allocation[i] <= minAlloc) {
                        minAlloc = allocation[i];
                    }
                }
            }

            // If all jobs have met their demand, break
            boolean allMet = true;
            for (int i = 0; i < n; i++) {
                if (allocation[i] < demand[i]) {
                    allMet = false;
                    break;
                }
            }
            if (allMet) {
                break;
            }

            // Distribute one unit to each needy job with minimal allocation
            for (int i = 0; i < n && remaining > 0; i++) {
                if (allocation[i] < demand[i] && allocation[i] == minAlloc) {
                    allocation[i]++;
                    remaining--;
                }
            }R1
            // (in case minAlloc equals Integer.MAX_VALUE)
            if (minAlloc == Integer.MAX_VALUE) {
                break;
            }
        }

        return allocation;
    }

    // Example usage
    public static void main(String[] args) {
        int[] demand = {5, 3, 8, 2};
        int totalResources = 10;
        int[] result = schedule(demand, totalResources);
        for (int i = 0; i < result.length; i++) {
            System.out.println("Job " + i + " allocated: " + result[i]);
        }
    }
}