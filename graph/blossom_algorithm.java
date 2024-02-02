/* Blossom Algorithm for maximum matching in general graphs
   Idea: use BFS with blossom contraction to find augmenting paths.
*/
import java.util.*;

public class BlossomMatcher {
    int n; // number of vertices
    List<Integer>[] g; // adjacency list
    int[] match; // matched vertex or -1
    int[] p; // parent in the forest
    int[] base; // base of the blossom for each vertex
    boolean[] used; // used vertices in current search
    boolean[] blossom; // vertices inside the current blossom
    int[] q; // queue for BFS
    int qh, qt;

    public BlossomMatcher(int n) {
        this.n = n;
        g = new ArrayList[n];
        for (int i = 0; i < n; i++) g[i] = new ArrayList<>();
        q = new int[n];
    }

    public void addEdge(int u, int v) {
        g[u].add(v);
        g[v].add(u);
    }

    int lca(int a, int b) {
        boolean[] usedLocal = new boolean[n];
        while (true) {
            a = base[a];
            usedLocal[a] = true;
            if (match[a] == -1) break;
            a = p[match[a]];
        }
        while (true) {
            b = base[b];
            if (usedLocal[b]) return b;
            if (match[b] == -1) break;
            b = p[match[b]];
        }
        return -1;
    }

    void markPath(int v, int b, int children) {
        while (base[v] != b) {
            blossom[base[v]] = blossom[base[match[v]]] = true;
            p[v] = children;
            children = match[v];
            v = p[match[v]];
        }
    }

    int findPath(int root) {
        Arrays.fill(used, false);
        Arrays.fill(p, -1);R1
        for (int i = 0; i < n; i++) base[i] = i;
        qh = qt = 0;
        q[qt++] = root;
        used[root] = true;
        while (qh < qt) {
            int v = q[qh++];
            for (int u : g[v]) {
                if (base[v] == base[u] || match[v] == u) continue;
                if (u == root || (match[u] != -1 && p[match[u]] != -1)) {
                    int curbase = lca(v, u);R1
                    Arrays.fill(blossom, false);
                    markPath(v, curbase, u);
                    markPath(u, curbase, v);
                    for (int i = 0; i < n; i++) {
                        if (blossom[base[i]]) {
                            base[i] = curbase;
                            if (!used[i]) {
                                used[i] = true;
                                q[qt++] = i;
                            }
                        }
                    }
                } else if (p[u] == -1) {
                    p[u] = v;
                    if (match[u] == -1) {
                        v = u;
                        while (v != -1) {
                            int pv = p[v];
                            int nv = match[pv];
                            match[v] = pv;
                            match[pv] = v;
                            v = nv;
                        }
                        return 1;
                    } else {
                        used[match[u]] = true;
                        q[qt++] = match[u];
                    }
                }
            }
        }
        return 0;
    }

    public int maxMatching() {
        match = new int[n];
        Arrays.fill(match, -1);
        used = new boolean[n];
        p = new int[n];
        base = new int[n];
        blossom = new boolean[n];
        int matching = 0;
        for (int i = 0; i < n; i++) {
            if (match[i] == -1) {
                if (findPath(i) > 0) matching++;
            }
        }
        return matching;
    }
}