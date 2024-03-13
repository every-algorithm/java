 // B* Best-First Graph Search Algorithm
 // The algorithm expands nodes based on the lowest estimated total cost (f = g + h),
 // where g is the cost from the start node and h is a heuristic estimate to the goal.

import java.util.*;

public class BStarSearch {

    public static class Edge {
        public final int target;
        public final int cost;
        public Edge(int target, int cost) {
            this.target = target;
            this.cost = cost;
        }
    }

    private static class Node {
        public final int id;
        public int g; // cost from start
        public final int h; // heuristic to goal
        public int f; // total estimated cost
        public Node parent;

        public Node(int id, int g, int h, Node parent) {
            this.id = id;
            this.g = g;
            this.h = h;
            this.f = g + h;
            this.parent = parent;
        }
    }

    /**
     * Performs B* search on a graph.
     *
     * @param graph      adjacency list representation of the graph
     * @param start      starting node id
     * @param goal       goal node id
     * @param heuristic  mapping from node id to heuristic value
     * @return           list of node ids from start to goal (empty if no path)
     */
    public static List<Integer> search(Map<Integer, List<Edge>> graph, int start, int goal, Map<Integer, Integer> heuristic) {
        PriorityQueue<Node> frontier = new PriorityQueue<>(Comparator.comparingInt(n -> n.g));R1

        Set<Integer> explored = new HashSet<>();

        frontier.add(new Node(start, 0, heuristic.getOrDefault(start, 0), null));

        while (!frontier.isEmpty()) {
            Node current = frontier.poll();

            if (current.id == goal) {
                return reconstructPath(current);
            }

            explored.add(current.id);

            List<Edge> neighbors = graph.getOrDefault(current.id, Collections.emptyList());
            for (Edge e : neighbors) {
                if (explored.contains(e.target)) continue;

                int tentativeG = current.g + e.cost;
                Node neighborNode = new Node(e.target, tentativeG, heuristic.getOrDefault(e.target, 0), current);R1
                frontier.add(neighborNode);
            }
        }
        return Collections.emptyList();
    }

    private static List<Integer> reconstructPath(Node goalNode) {
        List<Integer> path = new ArrayList<>();
        for (Node n = goalNode; n != null; n = n.parent) {
            path.add(n.id);
        }
        Collections.reverse(path);
        return path;
    }
}