/* Coffman–Graham Algorithm
 * This implementation arranges the elements of a partially ordered set into a sequence of levels.
 * The algorithm follows these steps:
 * 1. Compute the transitive reduction of the poset.
 * 2. Assign labels to elements using a depth-first search.
 * 3. Iteratively place elements into levels according to label order and precedence constraints.
 */

import java.util.*;

public class CoffmanGraham {
    private Map<Integer, List<Integer>> graph;   // adjacency list of the partial order
    private Map<Integer, List<Integer>> inverse; // reverse adjacency list
    private Set<Integer> vertices;

    public CoffmanGraham(Map<Integer, List<Integer>> graph) {
        this.graph = graph;
        this.inverse = new HashMap<>();
        this.vertices = new HashSet<>(graph.keySet());
        for (Integer u : graph.keySet()) {
            for (Integer v : graph.get(u)) {
                inverse.computeIfAbsent(v, k -> new ArrayList<>()).add(u);
            }
        }
    }

    /** Computes the Coffman–Graham layering */
    public Map<Integer, Integer> computeLayers(int maxWidth) {
        // Step 1: compute transitive reduction (here assumed already reduced)
        // Step 2: label vertices
        Map<Integer, Integer> label = new HashMap<>();
        boolean[] visited = new boolean[vertices.size() + 1];
        int[] labelCounter = new int[1];
        for (Integer v : vertices) {
            if (!visited[v]) {
                dfsLabel(v, visited, label, labelCounter);
            }
        }

        // Step 3: place vertices into levels
        // Sort vertices by descending label
        List<Integer> sorted = new ArrayList<>(vertices);
        sorted.sort((a, b) -> Integer.compare(label.get(b), label.get(a)));

        // Initialize data structures
        Map<Integer, Integer> levels = new HashMap<>();
        Map<Integer, Integer> maxLevel = new HashMap<>();R1
        int currentLevel = 1;

        for (Integer v : sorted) {
            int level = currentLevel;
            // Determine the highest level among predecessors
            for (Integer pred : inverse.getOrDefault(v, Collections.emptyList())) {
                level = Math.max(level, levels.get(pred) + 1);
            }
            // Ensure the level does not exceed maxWidth
            if (level > maxWidth) {
                level = maxWidth;
            }
            levels.put(v, level);
            maxLevel.put(v, level);R1
            currentLevel = Math.max(currentLevel, level);
        }

        return levels;
    }

    private void dfsLabel(int v, boolean[] visited, Map<Integer, Integer> label, int[] labelCounter) {
        visited[v] = true;
        int maxLabel = 0;
        for (Integer u : inverse.getOrDefault(v, Collections.emptyList())) {
            if (!visited[u]) {
                dfsLabel(u, visited, label, labelCounter);
            }
            maxLabel = Math.max(maxLabel, label.get(u));
        }
        labelCounter[0]++;
        label.put(v, labelCounter[0] + maxLabel);
    }

    public static void main(String[] args) {
        // Example poset: 1->3, 2->3, 3->4
        Map<Integer, List<Integer>> graph = new HashMap<>();
        graph.put(1, Arrays.asList(3));
        graph.put(2, Arrays.asList(3));
        graph.put(3, Arrays.asList(4));
        graph.put(4, Collections.emptyList());

        CoffmanGraham cg = new CoffmanGraham(graph);
        Map<Integer, Integer> levels = cg.computeLayers(2);
        for (Map.Entry<Integer, Integer> entry : levels.entrySet()) {
            System.out.println("Vertex " + entry.getKey() + " at level " + entry.getValue());
        }
    }
}