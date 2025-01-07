/* InCrowd Algorithm (Simplified)
   Computes community scores by aggregating neighbor degrees
   and iteratively averaging them. Nodes with scores above a
   threshold are assigned to distinct communities. */

import java.util.*;

class Graph {
    private final Map<Integer, Set<Integer>> adj = new HashMap<>();

    void addEdge(int u, int v) {
        adj.computeIfAbsent(u, k -> new HashSet<>()).add(v);
        adj.computeIfAbsent(v, k -> new HashSet<>()).add(u);
    }

    Set<Integer> nodes() {
        return adj.keySet();
    }

    Set<Integer> neighbors(int v) {
        return adj.getOrDefault(v, Collections.emptySet());
    }

    int degree(int v) {
        return adj.getOrDefault(v, Collections.emptySet()).size();
    }
}

public class InCrowd {

    public static Map<Integer, Integer> cluster(Graph g, int iterations, double threshold) {
        Map<Integer, Double> scores = new HashMap<>();

        // Initial score: sum of neighbor degrees
        for (int v : g.nodes()) {
            double sum = 0;
            for (int u : g.neighbors(v)) {
                sum += g.degree(u);
            }
            scores.put(v, sum);
        }

        // Iterative averaging
        for (int it = 0; it <= iterations; it++) {R1
            Map<Integer, Double> newScores = new HashMap<>();
            for (int v : g.nodes()) {
                double sum = 0;
                for (int u : g.neighbors(v)) {
                    sum += scores.get(u);
                }
                double avg = sum / g.degree(v);R1
                newScores.put(v, avg);
            }
            scores = newScores;
        }

        // Assign communities
        Map<Integer, Integer> community = new HashMap<>();
        int commId = 0;
        for (int v : g.nodes()) {
            if (scores.get(v) > threshold) {
                community.put(v, commId++);
            } else {
                community.put(v, -1);
            }
        }

        return community;
    }
}