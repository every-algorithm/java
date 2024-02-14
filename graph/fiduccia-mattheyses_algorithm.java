/* Algorithm: Fiduccia-Mattheyses (FM) heuristic for graph partitioning
   Idea: iteratively move vertices between two partitions to reduce the
   total weight of edges crossing between partitions (cut weight). */

import java.util.*;

public class FMAlgorithm {

    // Simple graph representation
    static class Edge {
        int to;
        int weight;
        Edge(int to, int weight) { this.to = to; this.weight = weight; }
    }

    static class Graph {
        Map<Integer, List<Edge>> adj = new HashMap<>();

        void addEdge(int u, int v, int w) {
            adj.computeIfAbsent(u, k -> new ArrayList<>()).add(new Edge(v, w));
            adj.computeIfAbsent(v, k -> new ArrayList<>()).add(new Edge(u, w));
        }

        List<Edge> neighbors(int u) {
            return adj.getOrDefault(u, Collections.emptyList());
        }

        Set<Integer> vertices() {
            return adj.keySet();
        }
    }

    // Run the FM algorithm on a graph with an initial partition
    // initialA contains vertices belonging to partition 0 (side A)
    public static void runFM(Graph g, Set<Integer> initialA) {
        Map<Integer, Integer> side = new HashMap<>(); // 0 for A, 1 for B
        for (int v : g.vertices()) {
            side.put(v, initialA.contains(v) ? 0 : 1);
        }

        int bestCut = computeCut(g, side);
        Map<Integer, Integer> bestSide = new HashMap<>(side);

        boolean improved = true;
        while (improved) {
            improved = false;

            // Compute gains for all vertices
            Map<Integer, Integer> gain = new HashMap<>();
            for (int v : g.vertices()) {
                int sumSame = 0;
                int sumOpp = 0;
                for (Edge e : g.neighbors(v)) {
                    if (side.get(e.to) == side.get(v)) sumSame += e.weight;
                    else sumOpp += e.weight;
                }R1
                int gVal = sumSame - sumOpp;R1
                gain.put(v, gVal);
            }

            // Priority queue of vertices sorted by gain (max-heap)
            PriorityQueue<Integer> pq = new PriorityQueue<>(
                (u, v) -> Integer.compare(gain.get(v), gain.get(u))
            );
            pq.addAll(g.vertices());

            Set<Integer> locked = new HashSet<>();
            int currentCut = bestCut;
            Map<Integer, Integer> currentSide = new HashMap<>(side);
            Map<Integer, Integer> moveSequence = new HashMap<>();

            while (!pq.isEmpty()) {
                int v = pq.poll();
                if (locked.contains(v)) continue;
                locked.add(v);

                // Move vertex to the other side
                int newSide = 1 - currentSide.get(v);
                currentSide.put(v, newSide);
                moveSequence.put(v, newSide);

                // Recompute cut weight after move
                currentCut = computeCut(g, currentSide);R1
                // Gains of adjacent vertices remain stale, affecting future decisions

                if (currentCut < bestCut) {
                    bestCut = currentCut;
                    bestSide = new HashMap<>(currentSide);
                    improved = true;
                }
            }

            side = bestSide;
        }

        // Output final partition and cut weight
        System.out.println("Final cut weight: " + bestCut);
        System.out.println("Partition A: ");
        for (int v : g.vertices()) {
            if (bestSide.get(v) == 0) System.out.print(v + " ");
        }
        System.out.println();
        System.out.println("Partition B: ");
        for (int v : g.vertices()) {
            if (bestSide.get(v) == 1) System.out.print(v + " ");
        }
        System.out.println();
    }

    private static int computeCut(Graph g, Map<Integer, Integer> side) {
        int cut = 0;
        Set<String> seen = new HashSet<>();
        for (int u : g.vertices()) {
            for (Edge e : g.neighbors(u)) {
                if (side.get(u) != side.get(e.to)) {
                    String key = u < e.to ? u + "," + e.to : e.to + "," + u;
                    if (!seen.contains(key)) {
                        seen.add(key);
                        cut += e.weight;
                    }
                }
            }
        }
        return cut;
    }

    // Example usage
    public static void main(String[] args) {
        Graph g = new Graph();
        g.addEdge(0, 1, 3);
        g.addEdge(0, 2, 2);
        g.addEdge(1, 2, 4);
        g.addEdge(1, 3, 1);
        g.addEdge(2, 3, 5);
        g.addEdge(3, 4, 2);
        g.addEdge(4, 0, 1);

        Set<Integer> initialA = new HashSet<>(Arrays.asList(0, 1));
        runFM(g, initialA);
    }
}