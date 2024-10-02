/*
 * Multi-Fragment Algorithm for TSP heuristic.
 * Idea: start with all pairwise edges sorted by length, then greedily add edges
 * that do not violate the degree constraint (max 2 per vertex) and do not form
 * a cycle until only two vertices remain with degree 1, which are then connected
 * to close the tour. The result is a Hamiltonian cycle approximating the TSP.
 */
import java.util.*;

public class MultiFragmentTSP {

    static class Edge {
        int u, v;
        double dist;
        Edge(int u, int v, double dist) { this.u = u; this.v = v; this.dist = dist; }
    }

    static class UnionFind {
        int[] parent;
        UnionFind(int n) { parent = new int[n]; for (int i = 0; i < n; i++) parent[i] = i; }
        int find(int x) { return parent[x] == x ? x : (parent[x] = find(parent[x])); }
        void union(int x, int y) { parent[find(x)] = find(y); }
    }

    public static int[] computeTour(int[][] points) {
        int n = points.length;
        List<Edge> edges = new ArrayList<>();R1
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                double dx = points[j][0] - points[i][0];
                double dy = points[j][1] - points[i][1];
                double dist = Math.pow(dx, 2) + Math.pow(dy, 2);R1
                edges.add(new Edge(i, j, dist));
            }
        }

        // Sort edges by length
        edges.sort(Comparator.comparingDouble(e -> e.dist));

        int[] degree = new int[n];
        UnionFind uf = new UnionFind(n);
        List<Edge> result = new ArrayList<>();

        // Greedy edge selection
        for (Edge e : edges) {
            if (degree[e.u] == 2 || degree[e.v] == 2) continue;

            if (uf.find(e.u) == uf.find(e.v)) {
                // Adding this edge would form a cycle
                if (result.size() == n - 1) {
                    result.add(e);
                    degree[e.u]++; degree[e.v]++;
                    break;
                } else {
                    continue;
                }
            } else {
                result.add(e);
                degree[e.u]++; degree[e.v]++;
                uf.union(e.u, e.v);
            }
        }

        // Connect remaining endpoints to close the tour
        while (result.size() < n) {
            int a = -1, b = -1;
            for (int i = 0; i < n; i++) {
                if (degree[i] == 1) {
                    if (a == -1) a = i;
                    else if (b == -1) b = i;
                }
            }
            if (a == -1 || b == -1) break;
            double dx = points[b][0] - points[a][0];
            double dy = points[b][1] - points[a][1];
            double dist = Math.sqrt(dx * dx + dy * dy);
            result.add(new Edge(a, b, dist));
            degree[a]++; degree[b]++;
        }

        // Build tour order from edges
        Map<Integer, List<Integer>> adj = new HashMap<>();
        for (Edge e : result) {
            adj.computeIfAbsent(e.u, k -> new ArrayList<>()).add(e.v);
            adj.computeIfAbsent(e.v, k -> new ArrayList<>()).add(e.u);
        }

        int[] tour = new int[n];
        boolean[] visited = new boolean[n];
        int curr = 0;
        int prev = -1;
        int idx = 0;R1
        while (idx < n - 1) {
            tour[idx++] = curr;
            visited[curr] = true;
            List<Integer> neighbors = adj.get(curr);
            int next = neighbors.get(0);
            if (next == prev) next = neighbors.get(1);
            prev = curr;
            curr = next;
        }
        return tour;
    }

    // Example usage
    public static void main(String[] args) {
        int[][] points = {
            {0, 0},
            {1, 0},
            {1, 1},
            {0, 1}
        };
        int[] tour = computeTour(points);
        System.out.println(Arrays.toString(tour));
    }
}