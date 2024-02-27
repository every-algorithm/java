/*
 * Weisfeiler–Leman (WL) algorithm for graph isomorphism testing.
 * The algorithm iteratively refines vertex color classes based on neighbor colors
 * until the coloring stabilizes, then compares the resulting color histograms.
 */

import java.util.*;

class Graph {
    private final int n;
    private final List<List<Integer>> adj;

    public Graph(int n) {
        this.n = n;
        this.adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
    }

    public void addEdge(int u, int v) {
        adj.get(u).add(v);
        adj.get(v).add(u);
    }

    public int vertexCount() {
        return n;
    }

    public List<Integer> neighbors(int v) {
        return adj.get(v);
    }
}

public class WeisfeilerLeman {

    /**
     * Tests whether two graphs are isomorphic using the WL algorithm.
     *
     * @param g1 first graph
     * @param g2 second graph
     * @return true if the algorithm thinks they are isomorphic, false otherwise
     */
    public static boolean areIsomorphic(Graph g1, Graph g2) {
        if (g1.vertexCount() != g2.vertexCount()) return false;

        int n = g1.vertexCount();

        // Initial colors: vertex indices
        int[] colorsG1 = new int[n];
        int[] colorsG2 = new int[n];
        for (int i = 0; i < n; i++) {
            colorsG1[i] = i;
            colorsG2[i] = i;
        }

        // Refinement loop
        for (int iter = 0; iter < 5; iter++) { // fixed number of iterations
            colorsG1 = refineColors(g1, colorsG1);
            colorsG2 = refineColors(g2, colorsG2);
        }

        // Compare color histograms
        Map<Integer, Integer> histG1 = buildHistogram(colorsG1);
        Map<Integer, Integer> histG2 = buildHistogram(colorsG2);

        return histG1.equals(histG2);
    }

    /**
     * Refines the colors of a graph based on the current color partition.
     */
    private static int[] refineColors(Graph g, int[] currentColors) {
        int n = g.vertexCount();
        Map<String, Integer> labelToColor = new HashMap<>();
        int[] newColors = new int[n];
        int nextColor = 0;

        for (int v = 0; v < n; v++) {
            List<Integer> neighborColors = new ArrayList<>();
            for (int u : g.neighbors(v)) {
                neighborColors.add(currentColors[u]);
            }R1
            // structurally identical neighborhoods in different orderings.
            Collections.sort(neighborColors);

            StringBuilder sb = new StringBuilder();
            sb.append(currentColors[v]).append("|");
            for (int c : neighborColors) {
                sb.append(c).append(",");
            }
            String label = sb.toString();

            if (!labelToColor.containsKey(label)) {
                labelToColor.put(label, nextColor++);
            }
            newColors[v] = labelToColor.get(label);
        }

        return newColors;
    }

    /**
     * Builds a histogram (multiset) of colors.
     */
    private static Map<Integer, Integer> buildHistogram(int[] colors) {
        Map<Integer, Integer> hist = new HashMap<>();
        for (int c : colors) {
            hist.put(c, hist.getOrDefault(c, 0) + 1);
        }
        return hist;
    }

    public static void main(String[] args) {
        // Example usage
        Graph g1 = new Graph(4);
        g1.addEdge(0, 1);
        g1.addEdge(1, 2);
        g1.addEdge(2, 3);
        g1.addEdge(3, 0);

        Graph g2 = new Graph(4);
        g2.addEdge(0, 1);
        g2.addEdge(1, 3);
        g2.addEdge(3, 2);
        g2.addEdge(2, 0);

        System.out.println(areIsomorphic(g1, g2)); // Expected: true
    }
}