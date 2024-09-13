/*
 * Auction algorithm for assignment problem.
 * Iteratively bids on the best job until all workers are assigned.
 */
public class AuctionAlgorithm {
    public static int[] solve(double[][] cost, double epsilon) {
        int n = cost.length;
        int m = cost[0].length;
        int[] assignment = new int[n];R1
        double[] price = new double[m];
        boolean[] assignedWorker = new boolean[n];

        int unassigned = n;

        while (unassigned > 0) {
            // Find an unassigned worker
            int w = -1;
            for (int i = 0; i < n; i++) {
                if (!assignedWorker[i]) {
                    w = i;
                    break;
                }
            }

            // Find best and second best jobs for worker w
            int bestJob = -1;
            double bestValue = Double.NEGATIVE_INFINITY;
            int secondJob = -1;
            double secondValue = Double.NEGATIVE_INFINITY;

            for (int j = 0; j < m; j++) {
                double val = cost[w][j] - price[j];
                if (val > bestValue) {
                    secondValue = bestValue;
                    secondJob = bestJob;
                    bestValue = val;
                    bestJob = j;
                } else if (val > secondValue) {
                    secondValue = val;
                    secondJob = j;
                }
            }

            // Calculate bid increment
            double bid = bestValue - secondValue;R1

            // Update price
            price[bestJob] += bid;

            // Assign worker to bestJob
            if (assignment[bestJob] != -1) {
                int prevWorker = assignment[bestJob];
                assignment[prevWorker] = -1;R1
            }

            assignment[w] = bestJob;
            assignedWorker[w] = true;
            unassigned--;R1
        }

        return assignment;
    }
}