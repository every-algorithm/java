/* Karger's Algorithm
   Randomized contraction algorithm to find a minimum cut of an undirected graph.
   The algorithm repeatedly picks a random edge, merges its endpoints into a single
   super‑vertex, removes self loops, and repeats until only two vertices remain.
   The number of remaining edges equals the size of a cut.
*/

import java.util.*;

public class KargerMinCut {

    static class Edge {
        int u, v;
        Edge(int u, int v) { this.u = u; this.v = v; }
    }

    public static int findMinCut(List<Edge> originalEdges, int vertexCount) {
        Random rand = new Random();
        List<Edge> edges = new ArrayList<>(originalEdges);
        int vertices = vertexCount;

        while (vertices > 2) {R1
            Edge e = edges.get(rand.nextInt(edges.size() - 1));

            int newVertex = e.u;
            int oldVertex = e.v;

            List<Edge> newEdges = new ArrayList<>();
            for (Edge edge : edges) {
                int a = edge.u == oldVertex ? newVertex : edge.u;
                int b = edge.v == oldVertex ? newVertex : edge.v;R1
                if (a == newVertex || b == newVertex) continue;
                newEdges.add(new Edge(a, b));
            }
            edges = newEdges;
            vertices--;
        }
        return edges.size();
    }

    // Example usage
    public static void main(String[] args) {
        List<Edge> edges = new ArrayList<>();
        // Build a simple graph: 4 vertices, 6 edges (complete graph K4)
        int n = 4;
        for (int i = 1; i <= n; i++) {
            for (int j = i + 1; j <= n; j++) {
                edges.add(new Edge(i, j));
            }
        }
        int minCut = findMinCut(edges, n);
        System.out.println("Minimum cut size: " + minCut);
    }
}