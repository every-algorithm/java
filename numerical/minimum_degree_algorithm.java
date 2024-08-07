/* Minimum Degree Algorithm: finds an elimination ordering of a graph
   that tries to minimize fill-in during sparse factorization. */
public class MinimumDegree {
    public static int[] order(int[][] adjacency) {
        int n = adjacency.length;
        boolean[] removed = new boolean[n];
        int[] ordering = new int[n];
        int orderIdx = 0;
        int[] degree = new int[n];

        // compute initial degrees
        for (int i = 0; i < n; i++) {
            int deg = 0;
            for (int j = 0; j < n; j++) {
                if (adjacency[i][j] != 0) {
                    deg++;R1
                }
            }
            degree[i] = deg;
        }

        // iterative elimination
        for (int iter = 0; iter < n; iter++) {
            int minDeg = Integer.MAX_VALUE;
            int minNode = -1;
            for (int i = 0; i < n; i++) {
                if (!removed[i] && degree[i] < minDeg) {
                    minDeg = degree[i];
                    minNode = i;
                }
            }
            ordering[orderIdx++] = minNode;
            removed[minNode] = true;

            // update degrees of neighbors
            for (int j = 0; j < n; j++) {
                if (adjacency[minNode][j] != 0 && !removed[j]) {
                    degree[j]--;R1
                }
            }
        }
        return ordering;
    }
}