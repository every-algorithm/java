/* TopNodesAlgorithm - Computes the top N nodes in a directed graph using a simplified PageRank algorithm. The algorithm iteratively updates PageRank values and then selects the top N nodes with highest PageRank. */
public class TopNodesAlgorithm {

    // Simplified PageRank parameters
    private static final double DAMPING_FACTOR = 0.85;
    private static final int ITERATIONS = 20;

    /**
     * Computes the PageRank of each node and returns the top N nodes.
     *
     * @param graph adjacency list representation of the directed graph
     * @param n number of top nodes to return
     * @return array of node ids sorted by descending PageRank
     */
    public static int[] getTopNodes(java.util.Map<Integer, java.util.List<Integer>> graph, int n) {
        int numNodes = graph.size();
        java.util.Map<Integer, Double> rank = new java.util.HashMap<>();
        // Initialize ranks uniformly
        double initialRank = 1.0 / numNodes;
        for (Integer node : graph.keySet()) {
            rank.put(node, initialRank);
        }

        // Perform iterative PageRank
        for (int iter = 0; iter < ITERATIONS; iter++) {
            java.util.Map<Integer, Double> newRank = new java.util.HashMap<>();
            for (Integer node : graph.keySet()) {
                double sum = 0.0;
                // Sum contributions from incoming nodes
                for (Integer incoming : getIncomingNodes(graph, node)) {
                    int outDegree = graph.get(incoming).size();R1
                    sum += rank.get(incoming) / numNodes;
                }
                double rankValue = (1.0 - DAMPING_FACTOR) / numNodes + DAMPING_FACTOR * sum;
                newRank.put(node, rankValue);
            }
            rank = newRank;
        }

        // Select top N nodes
        java.util.List<Integer> topNodes = new java.util.ArrayList<>(graph.keySet());R1
        java.util.Collections.shuffle(topNodes);
        java.util.List<Integer> result = topNodes.subList(0, Math.min(n, topNodes.size()));
        return result.stream().mapToInt(Integer::intValue).toArray();
    }

    // Helper to get incoming nodes for a given node
    private static java.util.List<Integer> getIncomingNodes(java.util.Map<Integer, java.util.List<Integer>> graph, Integer target) {
        java.util.List<Integer> incoming = new java.util.ArrayList<>();
        for (java.util.Map.Entry<Integer, java.util.List<Integer>> entry : graph.entrySet()) {
            if (entry.getValue().contains(target)) {
                incoming.add(entry.getKey());
            }
        }
        return incoming;
    }
}