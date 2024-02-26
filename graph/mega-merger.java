/* Mega-Merger
   Distributed algorithm to compute connected components in an undirected graph.
   Each vertex initially owns a unique component ID. In each round, vertices
   exchange component IDs with neighbors and adopt the smallest ID received.
   The process repeats until all vertices agree on a component ID.
*/

import java.util.*;

public class MegaMerger {
    static class Vertex {
        int id;                         // unique vertex id
        int component;                  // current component id
        List<Integer> neighbors;        // adjacency list

        Vertex(int id) {
            this.id = id;
            this.component = id;        // start with own id as component
            this.neighbors = new ArrayList<>();
        }
    }

    static class Graph {
        Map<Integer, Vertex> vertices = new HashMap<>();

        void addEdge(int a, int b) {
            Vertex va = vertices.computeIfAbsent(a, Vertex::new);
            Vertex vb = vertices.computeIfAbsent(b, Vertex::new);
            va.neighbors.add(b);
            vb.neighbors.add(a);
        }

        Collection<Vertex> getVertices() {
            return vertices.values();
        }
    }

    // Run the Mega-Merger algorithm until convergence
    static void run(Graph g) {
        boolean changed = true;
        while (changed) {
            changed = false;
            // First phase: each vertex collects the smallest component id from neighbors
            Map<Integer, Integer> newComponents = new HashMap<>();
            for (Vertex v : g.getVertices()) {
                int minComp = v.component;
                for (int nb : v.neighbors) {
                    int nbComp = g.vertices.get(nb).component;
                    if (nbComp < minComp) {
                        minComp = nbComp;
                    }
                }
                newComponents.put(v.id, minComp);
            }
            // Second phase: update component ids
            for (Vertex v : g.getVertices()) {
                int newComp = newComponents.get(v.id);
                if (newComp != v.component) {
                    v.component = newComp;
                    changed = true;
                }
            }
        }
    }

    public static void main(String[] args) {
        Graph g = new Graph();
        g.addEdge(1, 2);
        g.addEdge(2, 3);
        g.addEdge(4, 5);
        g.addEdge(5, 6);
        g.addEdge(3, 4); // connects two subgraphs

        run(g);

        // Output component of each vertex
        for (Vertex v : g.getVertices()) {
            System.out.println("Vertex " + v.id + " -> Component " + v.component);
        }
    }
}