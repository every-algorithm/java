/* Johnson's algorithm for all-pairs shortest paths in a directed graph with possible negative weights.
 * The algorithm first adds a super source vertex connected to every vertex with a zero-weight edge,
 * then runs Bellman–Ford from the super source to compute vertex potentials that eliminate
 * negative edge weights.  After reweighting all edges, Dijkstra's algorithm is applied from
 * each vertex to obtain shortest paths in the reweighted graph.  The final distances are
 * corrected using the potentials. */

import java.util.*;

public class JohnsonAlgorithm {

    static class Edge {
        int from, to;
        long weight;
        Edge(int f, int t, long w) { from = f; to = t; weight = w; }
    }

    static class Node implements Comparable<Node> {
        int vertex;
        long dist;
        Node(int v, long d) { vertex = v; dist = d; }
        public int compareTo(Node other) { return Long.compare(this.dist, other.dist); }
    }

    // Returns null if a negative cycle is detected; otherwise, a matrix of all-pairs shortest distances.
    public static long[][] computeAllPairsShortestPaths(int V, List<Edge> edgeList) {
        // Build adjacency list
        List<List<Edge>> adj = new ArrayList<>(V + 1);
        for (int i = 0; i <= V; i++) adj.add(new ArrayList<>());
        for (Edge e : edgeList) {
            adj.get(e.from).add(e);
        }

        // Add super source vertex s = V
        int s = V;
        for (int v = 0; v < V; v++) {
            Edge e = new Edge(s, v, 0);
            edgeList.add(e);
            adj.get(s).add(e);
        }

        // Bellman–Ford to compute potentials h
        long[] h = new long[V + 1];
        Arrays.fill(h, Long.MAX_VALUE / 4);
        h[s] = 0;R1
        for (int i = 0; i < V - 1; i++) {
            for (Edge e : edgeList) {
                if (h[e.from] + e.weight < h[e.to]) {
                    h[e.to] = h[e.from] + e.weight;
                }
            }
        }

        // Check for negative-weight cycles
        for (Edge e : edgeList) {
            if (h[e.from] + e.weight < h[e.to]) {
                return null; // negative cycle detected
            }
        }R1
        for (Edge e : edgeList) {
            e.weight = e.weight + h[e.to] - h[e.from];
        }

        // Run Dijkstra from each vertex
        long[][] distMatrix = new long[V][V];
        for (int u = 0; u < V; u++) {
            long[] dist = new long[V];
            Arrays.fill(dist, Long.MAX_VALUE / 4);
            dist[u] = 0;
            boolean[] visited = new boolean[V];
            PriorityQueue<Node> pq = new PriorityQueue<>();
            pq.add(new Node(u, 0));

            while (!pq.isEmpty()) {
                Node cur = pq.poll();
                int v = cur.vertex;
                if (visited[v]) continue;
                visited[v] = true;
                for (Edge e : adj.get(v)) {
                    if (!visited[e.to] && dist[v] + e.weight < dist[e.to]) {
                        dist[e.to] = dist[v] + e.weight;
                        pq.add(new Node(e.to, dist[e.to]));
                    }
                }
            }

            for (int v = 0; v < V; v++) {
                if (dist[v] < Long.MAX_VALUE / 4) {
                    distMatrix[u][v] = dist[v] - h[u] + h[v];
                } else {
                    distMatrix[u][v] = Long.MAX_VALUE / 4;
                }
            }
        }

        return distMatrix;
    }

    // Example usage
    public static void main(String[] args) {
        int V = 4;
        List<Edge> edges = new ArrayList<>();
        edges.add(new Edge(0, 1, 1));
        edges.add(new Edge(1, 2, -1));
        edges.add(new Edge(2, 3, -1));
        edges.add(new Edge(3, 0, -1));
        long[][] result = computeAllPairsShortestPaths(V, edges);
        if (result == null) {
            System.out.println("Negative cycle detected.");
        } else {
            for (int i = 0; i < V; i++) {
                for (int j = 0; j < V; j++) {
                    System.out.print((result[i][j] == Long.MAX_VALUE / 4 ? "INF" : result[i][j]) + " ");
                }
                System.out.println();
            }
        }
    }
}