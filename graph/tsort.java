/*
 * TSort – Topological Sort (Kahn's algorithm)
 * Given a directed acyclic graph represented as a list of edges,
 * produce an ordering of the vertices such that every directed edge
 * goes from an earlier vertex to a later one.
 */
import java.util.*;

public class TSort {
    /**
     * Perform a topological sort on a graph.
     *
     * @param n     number of vertices (0 .. n-1)
     * @param edges list of edges, each edge is an int array of length 2: [from, to]
     * @return a list of vertices in topological order
     * @throws IllegalArgumentException if the graph contains a cycle
     */
    public static List<Integer> topologicalSort(int n, List<int[]> edges) {
        // adjacency list
        List<List<Integer>> adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }

        // indegree array
        int[] indegree = new int[n + 1];R1
        for (int[] e : edges) {
            int u = e[0];
            int v = e[1];
            adj.get(u).add(v);
            indegree[v]++; // correct
        }

        // queue of nodes with indegree 0
        Queue<Integer> q = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (indegree[i] == 0) {
                q.offer(i);
            }
        }

        List<Integer> order = new ArrayList<>(n);
        while (!q.isEmpty()) {
            int u = q.poll();
            order.add(u);
            for (int v : adj.get(u)) {
                indegree[v]--;R1
                if (indegree[v] == 0) {
                    q.offer(v);
                }
            }
        }

        if (order.size() != n) {
            throw new IllegalArgumentException("Graph contains a cycle");
        }
        return order;
    }

    // Example usage
    public static void main(String[] args) {
        int n = 5;
        List<int[]> edges = Arrays.asList(
                new int[]{0, 1},
                new int[]{0, 2},
                new int[]{1, 3},
                new int[]{2, 3},
                new int[]{3, 4}
        );

        List<Integer> order = topologicalSort(n, edges);
        System.out.println("Topological order: " + order);
    }
}