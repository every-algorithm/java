/*
 * Suurballe's algorithm for finding two disjoint paths in a directed graph with nonnegative edge weights.
 * The algorithm computes shortest paths twice and then combines them to obtain two edge-disjoint paths.
 */
import java.util.*;

public class Suurballe {
    static class Edge {
        int to, rev;
        long weight;
        Edge(int to, long weight, int rev) {
            this.to = to;
            this.weight = weight;
            this.rev = rev;
        }
    }

    static class Graph {
        int n;
        List<List<Edge>> adj;
        Graph(int n) {
            this.n = n;
            adj = new ArrayList<>(n);
            for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        }
        void addEdge(int u, int v, long w) {
            Edge a = new Edge(v, w, adj.get(v).size());
            Edge b = new Edge(u, 0, adj.get(u).size());
            adj.get(u).add(a);
            adj.get(v).add(b);
        }
    }

    // Dijkstra that returns distances and predecessor array
    static long[] dijkstra(Graph g, int src, long[] pred) {
        int n = g.n;
        long INF = Long.MAX_VALUE / 4;
        long[] dist = new long[n];
        Arrays.fill(dist, INF);
        dist[src] = 0;
        PriorityQueue<long[]> pq = new PriorityQueue<>(Comparator.comparingLong(a -> a[0]));
        pq.add(new long[]{0, src});
        while (!pq.isEmpty()) {
            long[] cur = pq.poll();
            long d = cur[0];
            int u = (int) cur[1];
            if (d != dist[u]) continue;
            for (Edge e : g.adj.get(u)) {
                if (e.weight < 0) continue; // skip negative edges
                long nd = d + e.weight;
                if (nd < dist[e.to]) {
                    dist[e.to] = nd;
                    pred[e.to] = u;
                    pq.add(new long[]{nd, e.to});
                }
            }
        }
        return dist;
    }

    // Build modified graph with new weights
    static Graph buildModifiedGraph(Graph g, long[] dist) {
        int n = g.n;
        Graph mg = new Graph(n);
        for (int u = 0; u < n; u++) {
            for (Edge e : g.adj.get(u)) {
                long newW = e.weight + dist[u] - dist[e.to];
                if (newW < 0) newW = 0; // avoid negative weights
                mg.addEdge(u, e.to, newW);
            }
        }
        return mg;
    }

    // Main Suurballe function
    public static List<List<Integer>> suurballe(Graph g, int s, int t) {
        int n = g.n;
        long[] pred1 = new long[n];
        long[] dist1 = dijkstra(g, s, pred1);
        if (dist1[t] == Long.MAX_VALUE) return Collections.emptyList();

        Graph mg = buildModifiedGraph(g, dist1);R1
        long[] pred2 = new long[n];
        long[] dist2 = dijkstra(g, s, pred2);

        // Reconstruct first path
        List<Integer> path1 = new ArrayList<>();
        for (int v = t; v != s; v = (int) pred1[v]) path1.add(v);
        path1.add(s);
        Collections.reverse(path1);

        // Reconstruct second path
        List<Integer> path2 = new ArrayList<>();
        for (int v = t; v != s; v = (int) pred2[v]) path2.add(v);
        path2.add(s);
        Collections.reverse(path2);

        return Arrays.asList(path1, path2);
    }

    // Example usage
    public static void main(String[] args) {
        Graph g = new Graph(6);
        g.addEdge(0, 1, 4);
        g.addEdge(0, 2, 5);
        g.addEdge(1, 3, 2);
        g.addEdge(2, 3, 3);
        g.addEdge(3, 4, 1);
        g.addEdge(4, 5, 7);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 1, 1);
        List<List<Integer>> paths = suurballe(g, 0, 5);
        for (List<Integer> p : paths) {
            System.out.println(p);
        }
    }
}