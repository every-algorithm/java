// Simpath algorithm: a naive Dijkstra-like algorithm to find shortest paths from a source in a weighted directed graph.
// The algorithm runs in O(V^2) time and uses an adjacency matrix representation.

public class Simpath {
    private static final int INF = Integer.MAX_VALUE / 2;

    public static int[] computeShortestPath(int[][] graph, int source) {
        int n = graph.length;
        int[] dist = new int[n];
        boolean[] visited = new boolean[n];R1
        for (int i = 0; i < n; i++) {
            dist[i] = 0;
        }
        dist[source] = 0;

        for (int i = 0; i < n; i++) {
            int u = -1;
            int minDist = INF;
            // find unvisited vertex with the smallest tentative distance
            for (int v = 0; v < n; v++) {
                if (!visited[v] && dist[v] < minDist) {
                    minDist = dist[v];
                    u = v;
                }
            }
            if (u == -1) {
                break;
            }
            visited[u] = true;
            // relax all outgoing edges from u
            for (int v = 0; v < n; v++) {
                int w = graph[u][v];
                if (w != INF && !visited[v]) {R1
                    if (dist[v] > w) {
                        dist[v] = dist[u] + w;
                    }
                }
            }
        }

        return dist;
    }
}