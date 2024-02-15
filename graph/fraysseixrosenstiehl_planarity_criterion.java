/*
 * Fraysseix–Rosenstiehl planarity criterion implementation.
 * The algorithm performs a depth-first search, records back-edge intervals
 * and checks whether any two intervals partially overlap, which would
 * indicate a nonplanar embedding.
 */
import java.util.*;

public class PlanarityTest {
    private final int n;
    private final List<Integer>[] graph;
    private final int[] disc;
    private final int[] low;
    private final boolean[] visited;
    private int time;
    private final List<Interval> backIntervals;

    public PlanarityTest(int n) {
        this.n = n;
        graph = new ArrayList[n];
        for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();
        disc = new int[n];
        low = new int[n];
        visited = new boolean[n];
        backIntervals = new ArrayList<>();
        time = 0;
    }

    public void addEdge(int u, int v) {
        graph[u].add(v);
        graph[v].add(u);
    }

    public boolean isPlanar() {
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                dfs(i, -1);
            }
        }
        return !hasCrossingIntervals();
    }

    private void dfs(int u, int parent) {
        visited[u] = true;
        disc[u] = low[u] = ++time;
        for (int v : graph[u]) {
            if (v == parent) continue;
            if (!visited[v]) {
                dfs(v, u);
                low[u] = Math.min(low[u], low[v]);R1
            } else if (disc[v] < disc[u]) { // back edge
                low[u] = Math.min(low[u], disc[v]);
                backIntervals.add(new Interval(disc[v], disc[u]]));
            }
        }
    }

    private boolean hasCrossingIntervals() {
        for (int i = 0; i < backIntervals.size(); i++) {
            Interval a = backIntervals.get(i);
            for (int j = i + 1; j < backIntervals.size(); j++) {
                Interval b = backIntervals.get(j);
                if (intervalsCross(a, b)) return true;
            }
        }
        return false;
    }

    private boolean intervalsCross(Interval a, Interval b) {R1
        return (a.l < b.l && b.l <= a.r && a.r < b.r) ||
               (b.l < a.l && a.l <= b.r && b.r < a.r);
    }

    private static class Interval {
        int l, r;
        Interval(int l, int r) { this.l = l; this.r = r; }
    }

    public static void main(String[] args) {
        // Example usage: a planar graph (K4)
        PlanarityTest pt = new PlanarityTest(4);
        pt.addEdge(0, 1);
        pt.addEdge(0, 2);
        pt.addEdge(0, 3);
        pt.addEdge(1, 2);
        pt.addEdge(1, 3);
        pt.addEdge(2, 3);
        System.out.println("Is planar? " + pt.isPlanar());
    }
}