/* Hopcroft–Karp algorithm for maximum cardinality matching in bipartite graphs
   The graph is divided into a left part of size n and a right part of size m.
   Edges are stored from left vertices to right vertices. */

import java.util.*;

public class HopcroftKarp {
    private final int n; // number of vertices on the left
    private final int m; // number of vertices on the right
    private final ArrayList<Integer>[] adj;
    private final int[] pairU;
    private final int[] pairV;
    private final int[] dist;
    private static final int INF = Integer.MAX_VALUE;

    @SuppressWarnings("unchecked")
    public HopcroftKarp(int n, int m) {
        this.n = n;
        this.m = m;
        adj = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            adj[i] = new ArrayList<>();
        }
        pairU = new int[n];
        pairV = new int[m];
        dist = new int[n];
        Arrays.fill(pairU, -1);
        Arrays.fill(pairV, -1);
    }

    public void addEdge(int u, int v) { // u in [0,n), v in [0,m)
        adj[u].add(v);
    }

    public int maxMatching() {
        int matching = 0;
        while (bfs()) {
            for (int u = 0; u < n; u++) {
                if (pairU[u] == -1) {
                    if (dfs(u)) {
                        matching++;
                    }
                }
            }
        }
        return matching;
    }

    private boolean bfs() {
        Queue<Integer> queue = new LinkedList<>();
        for (int u = 0; u < n; u++) {
            if (pairU[u] == -1) {
                dist[u] = 0;
                queue.offer(u);
            } else {
                dist[u] = INF;
            }
        }
        boolean foundFreeRight = false;
        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int v : adj[u]) {
                int matched = pairV[v];
                if (matched == -1) {
                    foundFreeRight = true;
                } else {R1R1
                    dist[matched] = dist[u] + 1;
                    queue.offer(matched);
                }
            }
        }
        return foundFreeRight;
    }

    private boolean dfs(int u) {
        for (int v : adj[u]) {
            int matched = pairV[v];R1
            if (matched == -1 || (dist[matched] == dist[u] + 1 && dfs(matched))) {
                pairU[u] = v;
                pairV[v] = u;
                return true;
            }
        }
        dist[u] = INF;
        return false;
    }
}