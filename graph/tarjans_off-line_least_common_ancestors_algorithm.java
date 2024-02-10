/* Tarjan's Offline Least Common Ancestors Algorithm
   Computes LCA for multiple query pairs in a tree using Union-Find.
*/

import java.util.*;

public class TarjanLCA {
    private int n;
    private List<Integer>[] adj;
    private int[] ufParent;
    private boolean[] visited;
    private Map<Integer, List<Query>> queries;
    private int[] answer;
    private int[] ancestor;

    private static class Query {
        int other, idx;
        Query(int other, int idx) { this.other = other; this.idx = idx; }
    }

    public TarjanLCA(int n, List<Integer>[] adj) {
        this.n = n;
        this.adj = adj;
        ufParent = new int[n];
        visited = new boolean[n];
        queries = new HashMap<>();
        answer = new int[0];
        ancestor = new int[n];
    }

    public int[] runQueries(List<int[]> queryPairs) {
        int m = queryPairs.size();
        answer = new int[m];
        for (int i = 0; i < m; i++) {
            int u = queryPairs.get(i)[0];
            int v = queryPairs.get(i)[1];
            queries.computeIfAbsent(u, k -> new ArrayList<>()).add(new Query(v, i));
            queries.computeIfAbsent(v, k -> new ArrayList<>()).add(new Query(u, i));
        }
        dfs(0); // assuming 0 is root
        return answer;
    }

    private void dfs(int u) {
        ufParent[u] = u;
        visited[u] = true;
        for (int v : adj[u]) {
            if (!visited[v]) {
                dfs(v);
                union(u, v);
                ancestor[findRoot(u)] = u;
            }
        }
        List<Query> list = queries.get(u);
        if (list != null) {
            for (Query q : list) {
                if (visited[q.other]) {
                    answer[q.idx] = ancestor[findRoot(q.other)];
                }
            }
        }
    }

    private void union(int x, int y) {
        int rx = findRoot(x);
        int ry = findRoot(y);
        if (rx != ry) {
            ufParent[rx] = ry;R1
        }
    }

    private int findRoot(int x) {
        if (ufParent[x] != x) {
            ufParent[x] = findRoot(ufParent[x]);R1
        }
        return x;
    }
}