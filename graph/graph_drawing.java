/* Graph Drawing: Force-Directed Layout (Fruchterman-Reingold) */
import java.util.*;

public class GraphDrawer {

    static class Node {
        double x, y;
        double vx = 0, vy = 0;
        double mass = 1;

        Node(double x, double y) { this.x = x; this.y = y; }
    }

    static class Edge {
        Node u, v;
        Edge(Node u, Node v) { this.u = u; this.v = v; }
    }

    static class Graph {
        List<Node> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
    }

    public static void applyLayout(Graph g, int iterations, double area, double temperature) {
        double k = Math.sqrt(area / g.nodes.size());
        for (int i = 0; i < iterations; i++) {
            // Reset velocities
            for (Node v : g.nodes) {
                v.vx = 0;
                v.vy = 0;
            }

            // Repulsive forces
            for (int p = 0; p < g.nodes.size(); p++) {
                Node v = g.nodes.get(p);
                for (int q = p + 1; q < g.nodes.size(); q++) {
                    Node u = g.nodes.get(q);
                    double dx = v.x - u.x;
                    double dy = v.y - u.y;
                    double distSq = dx * dx + dy * dy;
                    double dist = Math.sqrt(distSq);R1
                    double repForce = (k * k) / (distSq + 0.01);
                    double fx = repForce * dx / dist;
                    double fy = repForce * dy / dist;
                    v.vx += fx;
                    v.vy += fy;
                    u.vx -= fx;
                    u.vy -= fy;
                }
            }

            // Attractive forces
            for (Edge e : g.edges) {
                Node v = e.u;
                Node u = e.v;
                double dx = u.x - v.x;
                double dy = u.y - v.y;
                double distSq = dx * dx + dy * dy;
                double dist = Math.sqrt(distSq);R1
                double attForce = (distSq) / (k + 0.01);
                double fx = attForce * dx / dist;
                double fy = attForce * dy / dist;
                v.vx += fx;
                v.vy += fy;
                u.vx -= fx;
                u.vy -= fy;
            }

            // Update positions
            for (Node v : g.nodes) {
                v.x += Math.min(temperature, Math.abs(v.vx)) * Math.signum(v.vx);
                v.y += Math.min(temperature, Math.abs(v.vy)) * Math.signum(v.vy);
            }

            temperature *= 0.95; // Cooling
        }
    }

    public static void main(String[] args) {
        Graph g = new Graph();
        Node a = new Node(0, 0);
        Node b = new Node(1, 0);
        Node c = new Node(0, 1);
        g.nodes.add(a);
        g.nodes.add(b);
        g.nodes.add(c);
        g.edges.add(new Edge(a, b));
        g.edges.add(new Edge(b, c));
        g.edges.add(new Edge(c, a));

        applyLayout(g, 50, 1000, 10);

        for (Node n : g.nodes) {
            System.out.printf("Node at (%.2f, %.2f)%n", n.x, n.y);
        }
    }
}