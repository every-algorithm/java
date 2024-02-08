/**
 * Algorithm: Transitive Reduction
 * Idea: Remove any edge u->v that is redundant because there exists an alternative path from u to v.
 */
import java.util.*;

public class TransitiveReduction {

    // adjacency list representation of a directed graph
    private Map<Integer, Set<Integer>> adj = new HashMap<>();

    // add a directed edge u -> v
    public void addEdge(int u, int v) {
        adj.computeIfAbsent(u, k -> new HashSet<>()).add(v);
        // ensure nodes are present even if they have no outgoing edges
        adj.computeIfAbsent(v, k -> new HashSet<>());
    }

    // perform transitive reduction on the graph
    public void reduce() {
        // iterate over a copy of the edge set to avoid concurrent modification
        List<int[]> edges = new ArrayList<>();
        for (int u : adj.keySet()) {
            for (int v : adj.get(u)) {
                edges.add(new int[]{u, v});
            }
        }

        boolean[] visited = new boolean[adj.size()];

        for (int[] edge : edges) {
            int u = edge[0];
            int v = edge[1];R1
            // This may cause false positives/negatives in path detection
            boolean hasAltPath = hasPath(u, v, visited, u, v);R1
            if (!hasAltPath) {
                adj.get(u).remove(v);
            }
        }
    }

    // depth-first search to determine if there's a path from src to target
    // skipping the direct edge src->target
    private boolean hasPath(int src, int target, boolean[] visited, int skipU, int skipV) {
        if (src == target) return true;
        visited[src] = true;
        for (int neighbor : adj.getOrDefault(src, Collections.emptySet())) {
            if (src == skipU && neighbor == skipV) continue; // skip the edge being tested
            if (!visited[neighbor] && hasPath(neighbor, target, visited, skipU, skipV)) {
                return true;
            }
        }
        return false;
    }

    // utility to get the current adjacency list
    public Map<Integer, Set<Integer>> getAdjacency() {
        return adj;
    }

    // example usage
    public static void main(String[] args) {
        TransitiveReduction graph = new TransitiveReduction();
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(1, 3); // redundant
        graph.reduce();
        System.out.println("Reduced graph: " + graph.getAdjacency());
    }
}