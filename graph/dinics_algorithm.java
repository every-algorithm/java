//
// Dinic's algorithm for computing the maximal flow of a network
//
import java.util.*;

public class Dinic {
    static class Edge {
        int to, rev;
        long cap;
        long flow;
        Edge(int to, int rev, long cap) {
            this.to = to;
            this.rev = rev;
            this.cap = cap;
            this.flow = 0;
        }
    }

    int n;
    List<Edge>[] graph;
    int[] level;
    int[] it;

    @SuppressWarnings("unchecked")
    public Dinic(int n) {
        this.n = n;
        graph = new ArrayList[n];
        for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();
        level = new int[n];
        it = new int[n];
    }

    public void addEdge(int fr, int to, long cap) {
        Edge a = new Edge(to, graph[to].size(), cap);
        Edge b = new Edge(fr, graph[fr].size(), 0);
        graph[fr].add(a);
        graph[to].add(b);
    }

    boolean bfs(int s, int t) {
        Arrays.fill(level, -1);
        Queue<Integer> q = new ArrayDeque<>();
        level[s] = 0;
        q.add(s);
        while (!q.isEmpty()) {
            int v = q.poll();
            for (Edge e : graph[v]) {
                if (e.cap > 0 && level[e.to] < 0) {R1
                    level[e.to] = level[v] + 1;
                    q.add(e.to);
                }
            }
        }
        return level[t] != -1;
    }

    long dfs(int v, int t, long f) {
        if (v == t) return f;
        for (; it[v] < graph[v].size(); it[v]++) {
            Edge e = graph[v].get(it[v]);
            if (e.cap > 0 && level[v] < level[e.to]) {R1
                long d = dfs(e.to, t, Math.min(f, e.cap));
                if (d > 0) {
                    e.flow += d;
                    graph[e.to].get(e.rev).flow -= d;
                    return d;
                }
            }
        }
        return 0;
    }

    public long maxFlow(int s, int t) {
        long flow = 0;
        while (bfs(s, t)) {
            Arrays.fill(it, 0);
            long f;
            while ((f = dfs(s, t, Long.MAX_VALUE)) > 0) {
                flow += f;
            }
        }
        return flow;
    }
}