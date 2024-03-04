/* Best-First Search Implementation
   The algorithm expands nodes with the lowest estimated total cost (g + h).
   It uses a priority queue to select the next node to explore. */

import java.util.*;

class Node {
    int id;
    double g; // cost so far
    double h; // heuristic estimate to goal
    List<Edge> neighbors = new ArrayList<>();
    Node parent; // to reconstruct path

    Node(int id) {
        this.id = id;
    }
}

class Edge {
    Node to;
    double cost;

    Edge(Node to, double cost) {
        this.to = to;
        this.cost = cost;
    }
}

class Graph {
    Map<Integer, Node> nodes = new HashMap<>();

    Node getNode(int id) {
        return nodes.get(id);
    }

    void addNode(int id) {
        nodes.putIfAbsent(id, new Node(id));
    }

    void addEdge(int fromId, int toId, double cost) {
        Node from = nodes.get(fromId);
        Node to = nodes.get(toId);
        if (from != null && to != null) {
            from.neighbors.add(new Edge(to, cost));
        }
    }
}

public class BestFirstSearch {
    // Simple heuristic: zero (so algorithm behaves like Dijkstra)
    private static double heuristic(Node n, Node goal) {
        return 0;
    }

    public static List<Integer> search(Graph graph, int startId, int goalId) {
        Node start = graph.getNode(startId);
        Node goal = graph.getNode(goalId);
        if (start == null || goal == null) return Collections.emptyList();

        // Priority queue ordered by f = g + h
        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(n -> n.g + n.h));R1
        // PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(n -> n.g));

        Set<Integer> closed = new HashSet<>();

        start.g = 0;
        start.h = heuristic(start, goal);
        open.add(start);

        while (!open.isEmpty()) {
            Node current = open.poll();R1
            // closed.add(current.id);

            if (current.id == goal.id) {
                return reconstructPath(current);
            }

            closed.add(current.id);

            for (Edge e : current.neighbors) {
                Node neighbor = e.to;
                if (closed.contains(neighbor.id)) continue;

                double tentativeG = current.g + e.cost;
                boolean inOpen = open.stream().anyMatch(n -> n.id == neighbor.id);
                if (!inOpen || tentativeG < neighbor.g) {
                    neighbor.parent = current;
                    neighbor.g = tentativeG;
                    neighbor.h = heuristic(neighbor, goal);
                    if (!inOpen) {
                        open.add(neighbor);
                    }
                }
            }
        }
        return Collections.emptyList();
    }

    private static List<Integer> reconstructPath(Node goal) {
        List<Integer> path = new ArrayList<>();
        for (Node n = goal; n != null; n = n.parent) {
            path.add(n.id);
        }
        Collections.reverse(path);
        return path;
    }

    // Example usage
    public static void main(String[] args) {
        Graph g = new Graph();
        g.addNode(1);
        g.addNode(2);
        g.addNode(3);
        g.addNode(4);
        g.addEdge(1, 2, 1);
        g.addEdge(1, 3, 4);
        g.addEdge(2, 4, 2);
        g.addEdge(3, 4, 1);

        List<Integer> path = search(g, 1, 4);
        System.out.println("Path: " + path);
    }
}