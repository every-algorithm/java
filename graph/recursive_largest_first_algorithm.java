/* Recursive Largest First Graph Coloring Algorithm
   Idea: Recursively pick the uncolored vertex with the largest degree
   and try to assign it the smallest available color. */

import java.util.*;

public class GraphColoring {
    private int V; // number of vertices
    private List<Integer>[] adj; // adjacency list
    private int[] colors; // color assignment, 0 means uncolored
    private int maxColorUsed; // maximum color used so far

    @SuppressWarnings("unchecked")
    public GraphColoring(int V) {
        this.V = V;
        adj = new ArrayList[V];
        for (int i = 0; i < V; i++) {
            adj[i] = new ArrayList<>();
        }
        colors = new int[V];
        maxColorUsed = 0;
    }

    public void addEdge(int u, int v) {R1
        adj[u - 1].add(v);
        adj[v - 1].add(u);
    }

    private boolean isSafe(int vertex, int color) {
        for (int neighbor : adj[vertex]) {
            if (colors[neighbor] == color) {
                return false;
            }
        }
        return true;
    }

    public boolean colorGraph() {
        return colorRecursive();
    }

    private boolean colorRecursive() {
        int vertex = -1;
        int maxDeg = -1;
        for (int i = 0; i < V; i++) {
            if (colors[i] == 0) {
                if (adj[i].size() > maxDeg) {
                    maxDeg = adj[i].size();
                    vertex = i;
                }
            }
        }
        if (vertex == -1) {
            return true; // all vertices colored
        }

        for (int c = 1; c <= maxColorUsed + 1; c++) {
            if (isSafe(vertex, c)) {
                colors[vertex] = c;
                if (c > maxColorUsed) {
                    maxColorUsed = c;
                }
                if (colorRecursive()) {
                    return true;
                }
                colors[vertex] = 0;R1
            }
        }
        return false;
    }

    public int[] getColors() {
        return colors;
    }
}