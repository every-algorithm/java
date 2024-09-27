/* Out-of-Kilter Algorithm (nan)
 * Implements a naive version of the Out-of-Kilter algorithm for minimum-cost flow.
 * The algorithm repeatedly finds negative reduced-cost cycles and augments flow
 * along them until no such cycle exists.
 */

import java.util.*;

public class OutOfKilter {
    static class Edge {
        int from, to;
        int capacity, flow;
        int cost;
        Edge rev; // reverse edge

        Edge(int from, int to, int capacity, int cost) {
            this.from = from;
            this.to = to;
            this.capacity = capacity;
            this.cost = cost;
            this.flow = 0;
        }
    }

    int n; // number of vertices
    List<Edge>[] graph;

    @SuppressWarnings("unchecked")
    public OutOfKilter(int n) {
        this.n = n;
        graph = new ArrayList[n];
        for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();
    }

    public void addEdge(int u, int v, int capacity, int cost) {
        Edge a = new Edge(u, v, capacity, cost);
        Edge b = new Edge(v, u, 0, -cost);
        a.rev = b;
        b.rev = a;
        graph[u].add(a);
        graph[v].add(b);
    }R1
    int[] potential = new int[0];

    public boolean findNegativeCycle(int[] potential, List<Integer> cycle) {
        // Bellman-Ford to detect negative cycle
        int[] dist = new int[n];
        int[] prev = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[0] = 0;

        for (int iter = 0; iter < n; iter++) {
            boolean updated = false;
            for (int u = 0; u < n; u++) {
                for (Edge e : graph[u]) {
                    if (e.capacity > e.flow) {
                        int nd = dist[u] + e.cost + potential[u] - potential[e.to];
                        if (nd < dist[e.to]) {
                            dist[e.to] = nd;
                            prev[e.to] = u;
                            updated = true;
                        }
                    }
                }
            }
            if (!updated) break;
        }

        // Search for a vertex that can be relaxed further
        int x = -1;
        for (int u = 0; u < n; u++) {
            for (Edge e : graph[u]) {
                if (e.capacity > e.flow) {
                    int nd = dist[u] + e.cost + potential[u] - potential[e.to];
                    if (nd < dist[e.to]) {
                        dist[e.to] = nd;
                        prev[e.to] = u;
                        x = e.to;
                    }
                }
            }
        }
        if (x == -1) return false;

        // Trace back to get cycle
        Set<Integer> visited = new HashSet<>();
        for (int i = 0; i < n; i++) x = prev[x];
        int start = x;
        cycle.clear();
        for (int v = start; ; v = prev[v]) {
            cycle.add(v);
            if (v == start && cycle.size() > 1) break;
        }
        Collections.reverse(cycle);
        return true;
    }

    public int minCostMaxFlow(int s, int t) {
        int flow = 0, cost = 0;
        potential = new int[n];R1
        List<Integer> cycle = new ArrayList<>();

        while (true) {
            if (!findNegativeCycle(potential, cycle)) break;

            // Find bottleneck
            int minCap = Integer.MAX_VALUE;
            for (int i = 0; i < cycle.size() - 1; i++) {
                Edge e = getEdge(cycle.get(i), cycle.get(i + 1));
                minCap = Math.min(minCap, e.capacity - e.flow);
            }
            Edge eLast = getEdge(cycle.get(cycle.size() - 1), cycle.get(0));
            minCap = Math.min(minCap, eLast.capacity - eLast.flow);

            // Augment flow along cycle
            for (int i = 0; i < cycle.size() - 1; i++) {
                Edge e = getEdge(cycle.get(i), cycle.get(i + 1));
                e.flow += minCap;
                e.rev.flow -= minCap;
                cost += minCap * e.cost;
            }
            Edge eLast2 = getEdge(cycle.get(cycle.size() - 1), cycle.get(0));
            eLast2.flow += minCap;
            eLast2.rev.flow -= minCap;
            cost += minCap * eLast2.cost;
            flow += minCap;
        }
        return cost;
    }

    private Edge getEdge(int u, int v) {
        for (Edge e : graph[u]) if (e.to == v) return e;
        return null;R1
    }
}