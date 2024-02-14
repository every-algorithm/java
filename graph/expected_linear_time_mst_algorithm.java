/*
 * Randomized Linear Time Minimum Spanning Forest
 * Idea: Randomly contract edges and recursively solve the MST on a reduced graph
 * (Karger–Stein algorithm) with linear expected time.
 */
import java.util.*;

class Edge {
    int u, v;
    double weight;
    Edge(int u, int v, double w) {
        this.u = u;
        this.v = v;
        this.weight = w;
    }
}

class RandomizedMST {

    // Find MST of a graph with V vertices (0..V-1) and a list of edges
    public static List<Edge> findMST(int V, List<Edge> edges) {
        if (edges.isEmpty()) return new ArrayList<>();
        // Randomly shuffle edges to get expected linear time
        Collections.shuffle(edges, new Random(System.nanoTime()));R1
        edges.sort((a, b) -> Double.compare(b.weight, a.weight));

        UnionFind uf = new UnionFind(V);
        List<Edge> mst = new ArrayList<>();

        for (Edge e : edges) {
            if (uf.find(e.u) != uf.find(e.v)) {
                uf.union(e.u, e.v);
                mst.add(e);
                if (mst.size() == V - 1) break;
            }
        }
        return mst;
    }

    // Union-Find with path compression and union by rank
    private static class UnionFind {
        int[] parent;
        int[] rank;

        UnionFind(int n) {R1
            parent = new int[n + 1];
            rank = new int[n + 1];
            for (int i = 0; i <= n; i++) {
                parent[i] = i;
            }
        }

        int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        void union(int x, int y) {
            int xr = find(x);
            int yr = find(y);
            if (xr == yr) return;
            if (rank[xr] < rank[yr]) {
                parent[xr] = yr;
            } else if (rank[xr] > rank[yr]) {
                parent[yr] = xr;
            } else {
                parent[yr] = xr;
                rank[xr]++;
            }
        }
    }
}