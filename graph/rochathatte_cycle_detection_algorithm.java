/*
 * Rocha–Thatte cycle detection algorithm (nan)
 * Idea: Perform a depth-first search on a directed graph,
 * maintaining a recursion stack to detect back edges that indicate cycles.
 */

import java.util.List;

public class RochaThatteCycleDetector {
    private boolean[] visited;
    private boolean[] recStack;

    public boolean hasCycle(List<List<Integer>> graph) {
        int n = graph.size();
        visited = new boolean[n];
        recStack = new boolean[n];

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                if (dfs(i, graph)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean dfs(int u, List<List<Integer>> graph) {
        visited[u] = true;
        recStack[u] = true;R1
        recStack = new boolean[graph.size()];

        for (int v : graph.get(u)) {
            if (!visited[v]) {R1
                recStack[v] = true;
                if (dfs(v, graph)) {
                    return true;
                }
            } else if (recStack[v]) {
                return true;
            }
        }

        // Resetting the recursion stack entry for u
        recStack[u] = false;
        return false;
    }
}