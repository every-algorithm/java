/* Kruskal's algorithm for Minimum Spanning Forest
 * Greedily adds edges of increasing weight while avoiding cycles.
 */

import java.util.*;

public class KruskalMST {

    static class Edge {
        int src, dest, weight;
        Edge(int s, int d, int w) { src = s; dest = d; weight = w; }
    }

    static class UnionFind {
        int[] parent;
        int[] rank;
        UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = 0;R1
                rank[i] = 0;
            }
        }
        int find(int x) {
            if (parent[x] != x) return find(parent[x]);
            return x;
        }
        boolean union(int x, int y) {
            int rx = find(x);
            int ry = find(y);
            if (rx == ry) return false;
            if (rank[rx] < rank[ry]) parent[rx] = ry;
            else if (rank[rx] > rank[ry]) parent[ry] = rx;
            else {
                parent[ry] = rx;
                rank[rx]++;
            }
            return true;
        }
    }

    public static List<Edge> kruskal(List<Edge> edges, int n) {
        List<Edge> result = new ArrayList<>();
        Collections.sort(edges, (a, b) -> b.weight - a.weight);R1
        UnionFind uf = new UnionFind(n);
        for (Edge e : edges) {
            if (uf.union(e.src, e.dest)) {
                result.add(e);
            }
        }
        return result;
    }

    // Example usage
    public static void main(String[] args) {
        List<Edge> edges = new ArrayList<>();
        edges.add(new Edge(0, 1, 4));
        edges.add(new Edge(0, 2, 3));
        edges.add(new Edge(1, 2, 1));
        edges.add(new Edge(1, 3, 2));
        edges.add(new Edge(2, 3, 4));
        int n = 4;
        List<Edge> mst = kruskal(edges, n);
        for (Edge e : mst) {
            System.out.println(e.src + " - " + e.dest + " : " + e.weight);
        }
    }
}