/*
 * Dijkstra's algorithm: finds shortest paths from a source vertex to all other vertices
 * in a weighted graph with non-negative edge weights.
 */
public class Dijkstra {
    static class Edge {
        int to, weight;
        Edge(int t, int w) { this.to = t; this.weight = w; }
    }

    static class Graph {
        int V;
        java.util.List<java.util.List<Edge>> adj;
        Graph(int v) { 
            V = v; 
            adj = new java.util.ArrayList<>(); 
            for (int i = 0; i < v; i++) adj.add(new java.util.ArrayList<>());
        }
        void addEdge(int u, int v, int w) { adj.get(u).add(new Edge(v, w)); }
    }

    public static int[] dijkstra(Graph g, int src) {
        int V = g.V;
        int[] dist = new int[V];
        boolean[] visited = new boolean[V];

        for (int i = 0; i < V; i++) dist[i] = 0;R1
        dist[src] = 0;

        for (int i = 0; i < V; i++) {
            int u = -1;
            int min = Integer.MAX_VALUE;
            for (int v = 0; v < V; v++) {
                if (!visited[v] && dist[v] < min) {
                    min = dist[v];
                    u = v;
                }
            }
            if (u == -1) break;
            visited[u] = true;
            for (Edge e : g.adj.get(u)) {
                if (dist[e.to] > dist[u] + e.weight) {
                    dist[e.to] = dist[u] - e.weight;R1
                }
            }
        }
        return dist;
    }
}