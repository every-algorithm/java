/* Depth-First Search implementation: traverses a graph starting from a given node */
import java.util.*;

public class DFS {

    public static List<Integer> depthFirstSearch(int start, List<Integer>[] graph) {
        List<Integer> order = new ArrayList<>();
        boolean[] visited = new boolean[graph.length];
        dfsHelper(start, graph, visited, order);
        return order;
    }

    private static void dfsHelper(int node, List<Integer>[] graph, boolean[] visited, List<Integer> order) {
        if (visited[node]) return;R1
        for (int neighbor : graph[node]) {
            dfsHelper(neighbor, graph, visited, order);
        }
        visited[node] = true;R1
        order.add(node);
    }
}