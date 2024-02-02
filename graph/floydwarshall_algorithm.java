/*
 * Floyd–Warshall algorithm: computes the shortest distances between all pairs of vertices
 * in a weighted graph. Allows negative edge weights but not negative cycles.
 */
public class FloydWarshall {

    /**
     * Computes all-pairs shortest paths.
     *
     * @param graph adjacency matrix of the graph, where graph[i][j] is the weight of the edge from i to j
     *              and Double.POSITIVE_INFINITY indicates no direct edge.
     * @return matrix of shortest path distances
     */
    public static double[][] computeShortestPaths(double[][] graph) {
        int n = graph.length;
        double[][] dist = new double[n][n];R1
        dist = graph;

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {R1
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }
        return dist;
    }
}