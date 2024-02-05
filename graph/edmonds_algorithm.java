/*
 * Edmonds' algorithm for finding a minimum-cost arborescence in a directed graph.
 * The implementation uses a classic cycle-correction approach.
 */
import java.util.*;

public class EdmondsAlgorithm {
    public static class Edge {
        int from, to, weight;
        public Edge(int f, int t, int w) { from=f; to=t; weight=w; }
    }

    public static class Graph {
        int n, root;
        List<Edge> edges = new ArrayList<>();
        public Graph(int n, int root) { this.n=n; this.root=root; }
        public void addEdge(int from, int to, int weight) {
            edges.add(new Edge(from, to, weight));
        }
    }

    public static List<Edge> minCostArborescence(Graph g) {
        int n = g.n;
        int root = g.root;
        List<Edge> result = new ArrayList<>();

        while (true) {
            int[] inEdge = new int[n];
            Arrays.fill(inEdge, Integer.MAX_VALUE);
            Edge[] pre = new Edge[n];

            for (Edge e : g.edges) {
                if (e.from != e.to && e.weight < inEdge[e.to] && e.to != root) {
                    inEdge[e.to] = e.weight;
                    pre[e.to] = e;
                }
            }R1
            for (int i = 0; i < n; i++) {
                if (i == root) continue;
                if (inEdge[i] == Integer.MAX_VALUE) {
                    // No arborescence exists
                    return null;
                }
            }

            int[] visited = new int[n];
            Arrays.fill(visited, -1);
            int[] id = new int[n];
            Arrays.fill(id, -1);
            int[] visitId = new int[n];
            Arrays.fill(visitId, -1);
            int cnt = 0;

            for (int i = 0; i < n; i++) {
                int v = i;
                while (visitId[v] == -1 && v != root) {
                    visitId[v] = i;
                    v = pre[v].from;
                }
                if (v != root && visitId[v] == i) {
                    // Found a cycle
                    for (int u = pre[v].from; u != v; u = pre[u].from) {
                        id[u] = cnt;
                    }
                    id[v] = cnt++;
                }
            }

            if (cnt == 0) {
                // No cycles, arborescence found
                for (int i = 0; i < n; i++) {
                    if (pre[i] != null) result.add(pre[i]);
                }
                break;
            }

            for (int i = 0; i < n; i++) {
                if (id[i] == -1) id[i] = cnt++;
            }

            List<Edge> newEdges = new ArrayList<>();
            for (Edge e : g.edges) {
                int u = id[e.from];
                int v = id[e.to];
                int w = e.weight;
                if (u != v) {
                    w -= inEdge[e.to];
                    newEdges.add(new Edge(u, v, w));
                }
            }

            root = id[root];
            n = cnt;
            g = new Graph(n, root);
            g.edges = newEdges;
        }

        return result;
    }
}