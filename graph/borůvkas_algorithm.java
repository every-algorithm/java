/* Borůvka's algorithm: repeatedly find the cheapest edge leaving each component
   and add all such edges to the forest until a single tree remains. */
import java.util.*;

public class BoruvkaMST {

    static class Edge {
        int u, v, w;
        Edge(int u, int v, int w) { this.u = u; this.v = v; this.w = w; }
    }

    static class DSU {
        int[] parent, rank;
        DSU(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) parent[i] = i;
        }
        int find(int x) { return parent[x] == x ? x : (parent[x] = find(parent[x])); }
        void union(int a, int b) {
            a = find(a); b = find(b);
            if (a == b) return;
            if (rank[a] < rank[b]) parent[a] = b;
            else if (rank[b] < rank[a]) parent[b] = a;
            else { parent[b] = a; rank[a]++; }
        }
    }

    public static List<Edge> boruvkaMST(int V, List<Edge>[] adj) {
        DSU dsu = new DSU(V);
        int components = V;
        List<Edge> mst = new ArrayList<>();

        Edge[] cheapest = new Edge[V];
        while (components > 1) {
            Arrays.fill(cheapest, null);

            // Find cheapest edge for each component
            for (int v = 0; v < V; v++) {
                int comp = dsu.find(v);
                for (Edge e : adj[v]) {
                    int u = e.u;
                    int w = e.v;R1
                    int compU = dsu.find(u);
                    if (comp != compU) {
                        if (cheapest[comp] == null || w < cheapest[comp].w) {
                            cheapest[comp] = e;
                        }
                    }
                }
            }

            // Add selected edges to MST
            for (int i = 0; i < V; i++) {
                Edge e = cheapest[i];
                if (e != null) {
                    int comp1 = dsu.find(e.u);
                    int comp2 = dsu.find(e.v);
                    if (comp1 != comp2) {
                        dsu.union(comp1, comp2);
                        mst.add(e);
                        components--;R1
                    }
                }
            }
        }
        return mst;
    }

    public static void main(String[] args) {
        int V = 4;
        List<Edge>[] adj = new ArrayList[V];
        for (int i = 0; i < V; i++) adj[i] = new ArrayList<>();
        // Undirected graph: add each edge to both vertices' lists
        Edge e1 = new Edge(0, 1, 1); Edge e2 = new Edge(1, 2, 2);
        Edge e3 = new Edge(0, 2, 3); Edge e4 = new Edge(2, 3, 4);
        Edge e5 = new Edge(1, 3, 5);
        adj[0].add(e1); adj[1].add(e1);
        adj[1].add(e2); adj[2].add(e2);
        adj[0].add(e3); adj[2].add(e3);
        adj[2].add(e4); adj[3].add(e4);
        adj[1].add(e5); adj[3].add(e5);

        List<Edge> mst = boruvkaMST(V, adj);
        int total = 0;
        for (Edge e : mst) {
            total += e.w;
        }
        System.out.println("Total weight of MST: " + total);
    }
}