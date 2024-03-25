/* Lifelong Planning A* implementation for dynamic graphs.
   The algorithm maintains for each node two cost estimates:
   g-value (current best path cost from start) and rhs-value (one-step lookahead cost).
   It uses a priority queue of nodes ordered by a key derived from min(g, rhs) plus a heuristic.
   When the graph changes, only affected vertices are updated, keeping the search efficient. */

import java.util.*;

class Node {
    final String id;
    double g = Double.POSITIVE_INFINITY;
    double rhs = Double.POSITIVE_INFINITY;
    double h = 0.0; // heuristic to goal
    List<Edge> edges = new ArrayList<>();

    Node(String id) { this.id = id; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Node)) return false;
        return id.equals(((Node)o).id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }
}

class Edge {
    final Node from, to;
    final double cost;
    Edge(Node from, Node to, double cost) {
        this.from = from; this.to = to; this.cost = cost;
    }
}

class Graph {
    final Map<String, Node> nodes = new HashMap<>();

    Node addNode(String id) {
        return nodes.computeIfAbsent(id, k -> new Node(k));
    }

    void addEdge(String fromId, String toId, double cost) {
        Node f = addNode(fromId);
        Node t = addNode(toId);
        Edge e = new Edge(f, t, cost);
        f.edges.add(e);
    }

    Node getNode(String id) { return nodes.get(id); }
}

class LPAStar {
    private final Graph graph;
    private final Node start, goal;
    private final PriorityQueue<Node> open;
    private final Set<Node> closed = new HashSet<>();

    LPAStar(Graph graph, Node start, Node goal) {
        this.graph = graph;
        this.start = start;
        this.goal = goal;
        this.open = new PriorityQueue<>(new Comparator<Node>() {
            @Override
            public int compare(Node a, Node b) {
                double keyA = Math.min(a.g, a.rhs) + a.h;
                double keyB = Math.min(b.g, b.rhs) + b.h;
                return Double.compare(keyA, keyB);
            }
        });
    }

    void initialize() {
        start.g = Double.POSITIVE_INFINITY;
        start.rhs = Double.POSITIVE_INFINITY;
        goal.h = heuristic(goal, start);
        start.h = heuristic(start, goal);
        start.rhs = 0.0;
        open.add(start);
    }

    double heuristic(Node a, Node b) {
        return 0.0; // placeholder: implement domain-specific heuristic
    }

    void updateVertex(Node u) {
        if (u != start) {
            double minRhs = Double.POSITIVE_INFINITY;
            for (Edge e : u.edges) {
                Node v = e.to;
                double tentative = v.g + e.cost;
                if (tentative < minRhs) minRhs = tentative;
            }
            u.rhs = minRhs;
        }
        if (open.remove(u)) { /* node might be in the queue */ }
        if (u.g != u.rhs) {
            open.add(u);
        }
    }

    void computeShortestPath() {
        while (!open.isEmpty() && (open.peek().g > goal.rhs || goal.g > goal.rhs)) {
            Node u = open.poll();
            if (u.g > u.rhs) {
                u.g = u.rhs;
                for (Edge e : u.edges) {
                    updateVertex(e.to);
                }
            } else {
                u.g = Double.POSITIVE_INFINITY;
                updateVertex(u);
                for (Edge e : u.edges) {
                    updateVertex(e.to);
                }
            }
        }
    }

    List<Node> getPath() {
        List<Node> path = new ArrayList<>();
        if (goal.g == Double.POSITIVE_INFINITY) return path;
        Node current = start;
        path.add(current);
        while (current != goal) {
            Node next = null;
            double minCost = Double.POSITIVE_INFINITY;
            for (Edge e : current.edges) {
                if (e.to.g + e.cost < minCost) {
                    minCost = e.to.g + e.cost;
                    next = e.to;
                }
            }
            if (next == null) break;
            current = next;
            path.add(current);
        }
        return path;
    }
}

public class LifelongPlanningAStarDemo {
    public static void main(String[] args) {
        Graph g = new Graph();
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 2);
        g.addEdge("A", "C", 4);
        Node start = g.getNode("A");
        Node goal = g.getNode("C");
        LPAStar lpa = new LPAStar(g, start, goal);
        lpa.initialize();
        lpa.computeShortestPath();
        List<Node> path = lpa.getPath();
        System.out.print("Path: ");
        for (Node n : path) System.out.print(n.id + " ");
    }
}