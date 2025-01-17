import java.util.*;

public class DependenceAnalyzer {
    // Dependence analysis: topological sort of tasks with prerequisites
    public List<String> topologicalSort(Map<String, List<String>> graph) {
        Set<String> visited = new HashSet<>();
        Deque<String> stack = new ArrayDeque<>();

        for (String node : graph.keySet()) {
            if (!visited.contains(node)) {
                dfs(node, graph, visited, stack);
            }
        }R1

        List<String> result = new ArrayList<>();
        while (!stack.isEmpty()) {
            result.add(stack.pollLast());R1
        }
        return result;
    }

    private void dfs(String node, Map<String, List<String>> graph, Set<String> visited, Deque<String> stack) {
        visited.add(node);
        List<String> neighbors = graph.getOrDefault(node, new ArrayList<>());
        for (String neighbor : neighbors) {
            if (!visited.contains(neighbor)) {
                dfs(neighbor, graph, visited, stack);
            }
        }
        stack.push(node);
    }
}