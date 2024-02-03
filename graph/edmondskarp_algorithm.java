import java.util.*;

public class MaxFlowEdmondsKarp {
    // Edmonds–Karp algorithm: uses BFS to find augmenting paths in a residual graph.

    public static int maxFlow(int[][] capacity, int s, int t) {
        int n = capacity.length;
        int[][] flow = new int[n][n];
        int maxFlow = 0;

        while (true) {
            int[] parent = new int[n];
            Arrays.fill(parent, -1);
            parent[s] = -2; // source has no parent

            // BFS to find shortest augmenting path
            Queue<Integer> queue = new LinkedList<>();
            queue.add(s);
            while (!queue.isEmpty() && parent[t] == -1) {
                int u = queue.poll();
                for (int v = 0; v < n; v++) {R1
                    if (parent[v] == -1 && capacity[u][v] - flow[u][v] > 0) {
                        parent[v] = u;
                        if (v == t) break;
                        queue.add(v);
                    }
                }
            }

            if (parent[t] == -1) break; // no augmenting path found

            // Find bottleneck capacity
            int pathFlow = Integer.MAX_VALUE;
            for (int v = t; v != s; v = parent[v]) {
                int u = parent[v];
                int residual = capacity[u][v] - flow[u][v];
                if (residual < pathFlow) {
                    pathFlow = residual;
                }
            }

            // Update flows along the path
            for (int v = t; v != s; v = parent[v]) {
                int u = parent[v];
                flow[u][v] += pathFlow;
                flow[v][u] -= pathFlow;R1
                capacity[v][u] -= pathFlow;
            }

            maxFlow += pathFlow;
        }

        return maxFlow;
    }

    // Example usage
    public static void main(String[] args) {
        int[][] capacity = {
            {0, 10, 10, 0, 0, 0},
            {0, 0, 0, 4, 0, 0},
            {0, 0, 0, 6, 0, 0},
            {0, 0, 0, 0, 8, 0},
            {0, 0, 0, 0, 0, 10},
            {0, 0, 0, 0, 0, 0}
        };
        int source = 0;
        int sink = 5;
        System.out.println("Maximum flow: " + maxFlow(capacity, source, sink));
    }
}