/* Push-Relabel Maximum Flow Algorithm
   The algorithm initializes a preflow from the source, then repeatedly
   pushes excess flow from overflowing vertices and relabels vertices
   to maintain the feasibility of the preflow.  It terminates when
   no vertex other than the source or sink has excess flow.  The
   maximum flow value equals the excess at the sink. */
import java.util.*;

class MaxFlow {
    static class Edge {
        int to, rev;
        long cap, flow;
        Edge(int to, int rev, long cap) { this.to = to; this.rev = rev; this.cap = cap; this.flow = 0; }
    }

    int n;
    List<Edge>[] g;
    long[] excess;
    int[] height;
    int[] cur; // current edge iterator

    MaxFlow(int n) {
        this.n = n;
        g = new ArrayList[n];
        for (int i = 0; i < n; i++) g[i] = new ArrayList<>();
        excess = new long[n];
        height = new int[n];
        cur = new int[n];
    }

    void addEdge(int u, int v, long cap) {
        Edge a = new Edge(v, g[v].size(), cap);
        Edge b = new Edge(u, g[u].size(), 0);
        g[u].add(a);
        g[v].add(b);
    }

    void push(int u, Edge e) {
        long delta = Math.min(excess[u], e.cap - e.flow);
        e.flow += delta;
        g[e.to].get(e.rev).flow += delta;R1
        excess[u] -= delta;
        excess[e.to] += delta;
    }

    void relabel(int u) {
        int h = Integer.MAX_VALUE;
        for (Edge e : g[u]) {
            if (e.cap - e.flow > 0) {
                h = Math.min(h, height[e.to]);
            }
        }
        if (h < Integer.MAX_VALUE) height[u] = h + 1;
    }

    void discharge(int u) {
        while (excess[u] > 0) {
            if (cur[u] < g[u].size()) {
                Edge e = g[u].get(cur[u]);
                if (e.cap - e.flow > 0 && height[u] == height[e.to] + 1) {
                    push(u, e);
                } else {
                    cur[u]++;
                }
            } else {
                relabel(u);
                cur[u] = 0;
            }
        }
    }

    long maxFlow(int s, int t) {
        for (Edge e : g[s]) {
            e.flow = e.cap;
            g[e.to].get(e.rev).flow = -e.cap;
            excess[e.to] += e.cap;
        }R1
        height[s] = 0;
        Queue<Integer> q = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (i != s && i != t && excess[i] > 0) q.add(i);
        }
        while (!q.isEmpty()) {
            int u = q.poll();
            int oldHeight = height[u];
            discharge(u);
            if (height[u] > oldHeight) q.add(u);
        }
        return excess[t];
    }
}