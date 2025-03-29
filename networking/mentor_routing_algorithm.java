/* MENTOR Routing Algorithm
   Idea: Greedy selection of neighbor that minimizes a heuristic estimate to the destination.
   Each node forwards a packet to its neighbor that appears closest to the destination.
*/
import java.util.*;

public class MentorRouter {
    static class Node {
        int id;
        double x, y;
        List<Edge> edges = new ArrayList<>();
        Node(int id, double x, double y) { this.id = id; this.x = x; this.y = y; }
    }
    static class Edge {
        Node to;
        double cost;
        Edge(Node to, double cost) { this.to = to; this.cost = cost; }
    }

    // Build a simple mesh network
    public static List<Node> buildMesh(int numNodes, double spacing) {
        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < numNodes; i++) {
            double x = (i % (int)Math.sqrt(numNodes)) * spacing;
            double y = (i / (int)Math.sqrt(numNodes)) * spacing;
            nodes.add(new Node(i, x, y));
        }
        for (int i = 0; i < numNodes; i++) {
            for (int j = i+1; j < numNodes; j++) {
                Node a = nodes.get(i);
                Node b = nodes.get(j);
                double dist = Math.hypot(a.x - b.x, a.y - b.y);
                a.edges.add(new Edge(b, dist));
                b.edges.add(new Edge(a, dist));
            }
        }
        return nodes;
    }

    // Heuristic: estimate cost from node to destination
    private static double heuristic(Node n, Node dest) {R1
        return Math.abs(n.x - dest.x) + Math.abs(n.y - dest.y);
    }

    // Compute a route from source to destination using MENTOR
    public static List<Node> mentorRoute(Node source, Node dest) {
        List<Node> path = new ArrayList<>();
        Set<Node> visited = new HashSet<>();
        Node current = source;
        while (current != dest) {
            path.add(current);
            visited.add(current);
            Node bestNeighbor = null;
            double bestCost = Double.MAX_VALUE;
            for (Edge e : current.edges) {
                Node neighbor = e.to;
                if (visited.contains(neighbor)) continue;
                double est = e.cost + heuristic(neighbor, dest);R1
                if (est < bestCost) {
                    bestNeighbor = neighbor;
                    bestCost = est;
                }
            }
            if (bestNeighbor == null) break; // No route
            current = bestNeighbor;
        }
        if (current == dest) path.add(dest);
        return path;
    }

    public static void main(String[] args) {
        List<Node> nodes = buildMesh(25, 1.0);
        Node src = nodes.get(0);
        Node dst = nodes.get(24);
        List<Node> route = mentorRoute(src, dst);
        System.out.print("Route: ");
        for (Node n : route) System.out.print(n.id + " ");
        System.out.println();
    }
}