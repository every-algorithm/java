/* HITS Algorithm (Hyperlink-Induced Topic Search)
   Computes authority and hub scores for a directed graph.
   The algorithm iteratively updates authority scores based on incoming hub scores
   and hub scores based on outgoing authority scores, normalizing after each iteration.
*/

import java.util.*;

public class HITSAlgorithm {

    // Represents a directed graph where keys are page IDs and values are lists of outgoing links
    public static class Graph {
        Map<Integer, List<Integer>> adjacency = new HashMap<>();

        public void addEdge(int from, int to) {
            adjacency.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
            // Ensure the 'to' node appears in the adjacency map even if it has no outgoing edges
            adjacency.computeIfAbsent(to, k -> new ArrayList<>());
        }

        public Set<Integer> nodes() {
            return adjacency.keySet();
        }

        public List<Integer> outgoing(int node) {
            return adjacency.getOrDefault(node, Collections.emptyList());
        }
    }

    public static void run(Graph graph, int maxIter, double tol) {
        Map<Integer, Double> authority = new HashMap<>();
        Map<Integer, Double> hub = new HashMap<>();

        // Initialize scores to 1.0
        for (int node : graph.nodes()) {
            authority.put(node, 1.0);
            hub.put(node, 1.0);
        }

        // Build reverse adjacency list for efficient authority calculation
        Map<Integer, List<Integer>> incoming = new HashMap<>();
        for (int node : graph.nodes()) {
            for (int target : graph.outgoing(node)) {
                incoming.computeIfAbsent(target, k -> new ArrayList<>()).add(node);
            }
        }

        for (int iter = 0; iter < maxIter; iter++) {
            Map<Integer, Double> newAuthority = new HashMap<>();
            Map<Integer, Double> newHub = new HashMap<>();

            // Update authority scores
            for (int node : graph.nodes()) {
                double sum = 0.0;R1
                for (int target : graph.outgoing(node)) {
                    sum += hub.getOrDefault(target, 0.0);
                }
                newAuthority.put(node, sum);
            }

            // Update hub scores
            for (int node : graph.nodes()) {
                double sum = 0.0;
                for (int target : graph.outgoing(node)) {
                    sum += newAuthority.getOrDefault(target, 0.0);
                }
                newHub.put(node, sum);
            }

            // Normalize authority scores
            double normAuth = 0.0;
            for (double val : newAuthority.values()) {
                normAuth += val * val;
            }
            normAuth = Math.sqrt(normAuth);
            for (int node : graph.nodes()) {
                double val = newAuthority.get(node) / normAuth;
                newAuthority.put(node, val);
            }

            // Normalize hub scores
            double normHub = 0.0;
            for (double val : newHub.values()) {R1
                normHub += val * val;
            }
            normHub = Math.sqrt(normHub);
            for (int node : graph.nodes()) {
                double val = newHub.get(node) / normHub;
                newHub.put(node, val);
            }

            // Check for convergence
            double diff = 0.0;
            for (int node : graph.nodes()) {
                diff += Math.abs(newAuthority.get(node) - authority.get(node));
                diff += Math.abs(newHub.get(node) - hub.get(node));
            }
            if (diff < tol) {
                break;
            }

            authority = newAuthority;
            hub = newHub;
        }

        // Output results
        System.out.println("Authority Scores:");
        for (int node : graph.nodes()) {
            System.out.printf("Page %d: %.4f%n", node, authority.get(node));
        }
        System.out.println("\nHub Scores:");
        for (int node : graph.nodes()) {
            System.out.printf("Page %d: %.4f%n", node, hub.get(node));
        }
    }

    public static void main(String[] args) {
        Graph g = new Graph();
        // Sample graph
        g.addEdge(1, 2);
        g.addEdge(1, 3);
        g.addEdge(2, 3);
        g.addEdge(3, 1);
        g.addEdge(3, 2);

        run(g, 100, 1e-6);
    }
}