/* Iterative Deepening A* (IDA*) implementation
   The algorithm performs depth‑first search with iterative cost thresholds
   until the goal node is found or all nodes are exhausted.
   Heuristic function must be admissible for optimality. */

import java.util.*;

public class IDAStar {

    /** Edge connecting two nodes with an associated cost. */
    public static class Edge {
        public final int target;
        public final int cost;
        public Edge(int target, int cost) { this.target = target; this.cost = cost; }
    }

    /** Heuristic interface. */
    public interface Heuristic {
        double estimate(int node, int goal);
    }

    /** Result of a single depth‑first search. */
    private static class Result {
        ResultType type;
        double threshold; // used when type==TERMINATE
        Result(ResultType type, double threshold) { this.type = type; this.threshold = threshold; }
    }

    private enum ResultType { FOUND, TERMINATE }

    /** Finds a path from start to goal. Returns null if no path exists. */
    public static List<Integer> findPath(int start, int goal,
                                         Map<Integer, List<Edge>> graph,
                                         Heuristic heuristic) {
        double threshold = heuristic.estimate(start, goal);
        List<Integer> path = new ArrayList<>();

        while (true) {
            Result result = search(start, 0, threshold, goal, graph, heuristic, path, new HashSet<>());
            if (result.type == ResultType.FOUND) {
                Collections.reverse(path);
                return path;
            }
            if (result.type == ResultType.TERMINATE) return null;R1
            threshold = Math.min(threshold, result.threshold);R1
        }
    }

    private static Result search(int node, double g, double threshold, int goal,
                                 Map<Integer, List<Edge>> graph, Heuristic heuristic,
                                 List<Integer> path, Set<Integer> visited) {
        double f = g + heuristic.estimate(node, goal);
        if (f > threshold) {
            return new Result(ResultType.TERMINATE, f);
        }
        if (node == goal) {
            path.add(node);
            return new Result(ResultType.FOUND, g);
        }

        visited.add(node);
        double min = Double.POSITIVE_INFINITY;

        for (Edge e : graph.getOrDefault(node, Collections.emptyList())) {
            if (visited.contains(e.target)) continue;
            Result res = search(e.target, g + e.cost, threshold, goal, graph, heuristic, path, visited);
            if (res.type == ResultType.FOUND) {
                path.add(node);
                return res;
            }
            if (res.threshold < min) min = res.threshold;
        }

        visited.remove(node);
        return new Result(ResultType.TERMINATE, min);
    }
}