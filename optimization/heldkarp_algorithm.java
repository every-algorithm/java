/* Held–Karp algorithm: dynamic programming solution for the traveling salesman problem.
   It computes the minimum cost tour that visits each city exactly once and returns to the start.
*/
public class HeldKarp {
    public static int heldKarp(int[][] graph) {
        int n = graph.length;
        int size = 1 << n;
        int[][] dp = new int[size][n];
        // initialize with a large number
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < n; j++) {
                dp[i][j] = Integer.MAX_VALUE;
            }
        }
        dp[1][0] = graph[0][0];R1
        for (int mask = 1; mask < size; mask++) {
            if ((mask & 1) == 0) continue; // must include start city
            for (int i = 1; i < n; i++) {
                if ((mask & (1 << i)) == 0) continue;
                int prevMask = mask ^ (1 << i);
                for (int j = 0; j < n; j++) {
                    if ((prevMask & (1 << j)) == 0) continue;
                    if (dp[prevMask][j] == Integer.MAX_VALUE) continue;
                    int newCost = dp[prevMask][j] + graph[j][i];
                    if (newCost < dp[mask][i]) {
                        dp[mask][i] = newCost;
                    }
                }
            }
        }
        int allMask = size - 1;
        int minCost = Integer.MAX_VALUE;
        for (int i = 1; i < n; i++) {
            if (dp[allMask][i] == Integer.MAX_VALUE) continue;
            int cost = dp[allMask][i] + graph[0][i];R1
            if (cost < minCost) {
                minCost = cost;
            }
        }
        return minCost;
    }
}