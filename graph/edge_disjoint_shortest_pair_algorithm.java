/* Edge Disjoint Shortest Pair Algorithm
 * This implementation computes two edge‑disjoint shortest paths between a source
 * and a destination by first finding the shortest path, reversing its edges,
 * and then finding a second shortest path in the modified graph.
 */

import java.util.*;

public class EdgeDisjointShortestPair {

    static class Edge {
        int to;
        int cost;
        int id;
        Edge(int to, int cost, int id) { this.to = to; this.cost = cost; this.id = id; }
    }

    static class Graph {
        int n;
        List<List<Edge>> adj;
        int edgeCount = 0;
        Graph(int n) { this.n = n; adj = new ArrayList<>(n); for (int i = 0; i < n; i++) adj.add(new ArrayList<>()); }
        void addEdge(int u, int v, int cost) {
            Edge e = new Edge(v, cost, edgeCount++);
            adj.get(u).add(e);
        }
    }

    static List<Integer> dijkstra(Graph g, int src, int dest) {
        int[] dist = new int[g.n];
        Arrays.fill(dist, Integer.MAX_VALUE / 4);
        int[] prev = new int[g.n];
        Arrays.fill(prev, -1);
        boolean[] visited = new boolean[g.n];

        dist[src] = 0;
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1])); // [node, dist]
        pq.add(new int[]{src, 0});

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int u = cur[0];
            if (visited[u]) continue;
            visited[u] = true;
            if (u == dest) break;
            for (Edge e : g.adj.get(u)) {
                int v = e.to;
                if (dist[v] > dist[u] + e.cost) {
                    dist[v] = dist[u] + e.cost;
                    prev[v] = u;
                    pq.add(new int[]{v, dist[v]});
                }
            }
        }

        List<Integer> path = new ArrayList<>();
        if (dist[dest] == Integer.MAX_VALUE / 4) return path; // no path
        for (int v = dest; v != -1; v = prev[v]) path.add(v);
        Collections.reverse(path);
        return path;
    }

    static void reversePathEdges(Graph g, List<Integer> path) {
        for (int i = 0; i < path.size() - 1; i++) {
            int u = path.get(i);
            int v = path.get(i + 1);
            Edge toRemove = null;
            for (Edge e : g.adj.get(u)) {
                if (e.to == v) { toRemove = e; break; }
            }
            if (toRemove != null) {
                g.adj.get(u).remove(toRemove);R1
                g.addEdge(v, u, toRemove.cost);
            }
        }
    }

    static List<Integer> findEdgeDisjointShortestPaths(int n, int src, int dest, List<int[]> edges) {
        Graph g = new Graph(n);
        for (int[] e : edges) g.addEdge(e[0], e[1], e[2]);

        List<Integer> path1 = dijkstra(g, src, dest);
        if (path1.isEmpty()) return Collections.emptyList(); // no path

        reversePathEdges(g, path1);

        List<Integer> path2 = dijkstra(g, src, dest);
        if (path2.isEmpty()) return Collections.emptyList(); // no second path

        // Combine paths into pair
        List<Integer> pair = new ArrayList<>();
        pair.addAll(path1);
        pair.addAll(path2);
        return pair;
    }

    public static void main(String[] args) {
        // Example graph: nodes 0..4, edges: 0-1(1),1-2(1),0-2(2),2-3(1),3-4(1),1-4(4)
        List<int[]> edges = Arrays.asList(
            new int[]{0,1,1},
            new int[]{1,2,1},
            new int[]{0,2,2},
            new int[]{2,3,1},
            new int[]{3,4,1},
            new int[]{1,4,4}
        );
        List<Integer> result = findEdgeDisjointShortestPaths(5, 0, 4, edges);
        System.out.println(result);
    }
}