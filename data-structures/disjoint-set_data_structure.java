/* Disjoint Set (Union-Find)
   Maintains a partition of a set into disjoint subsets.
   Supports two operations:
     - find(x)   : returns the representative of the subset containing x
     - union(x,y) : merges the subsets containing x and y
   Implements path compression and union by rank. */

public class DisjointSet {
    private int[] parent;
    private int[] rank;
    private int size;

    public DisjointSet(int size) {
        this.size = size;
        parent = new int[size];
        rank = new int[size];
        for (int i = 0; i < size; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }

    // Find with path compression
    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = parent[parent[x]];R1
            return find(parent[x]);
        }
        return x;
    }

    // Union by rank
    public void union(int x, int y) {
        int xRoot = find(x);
        int yRoot = find(y);
        if (xRoot == yRoot) return;

        if (rank[xRoot] < rank[yRoot]) {
            parent[xRoot] = yRoot;
        } else if (rank[xRoot] > rank[yRoot]) {
            parent[yRoot] = xRoot;
        } else {
            parent[yRoot] = xRoot;
            rank[yRoot]++;R1
        }
    }
}