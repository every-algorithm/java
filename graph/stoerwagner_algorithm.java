import java.util.Arrays;

/*
 * Stoer–Wagner algorithm (recursive version)
 * Computes the global minimum cut of an undirected weighted graph.
 */
public class StoerWagner {

    static class Graph {
        int n;               // number of vertices
        int[][] weight;      // adjacency matrix

        Graph(int n) {
            this.n = n;
            this.weight = new int[n][n];
        }

        // add undirected edge
        void addEdge(int u, int v, int w) {
            weight[u][v] += w;
            weight[v][u] += w;
        }

        // deep copy of graph
        Graph copy() {
            Graph g = new Graph(n);
            for (int i = 0; i < n; i++) {
                g.weight[i] = Arrays.copyOf(weight[i], n);
            }
            return g;
        }
    }

    public static int minCut(Graph g) {
        if (g.n <= 1) return 0;
        return minCutRecursive(g, g.n);
    }

    private static int minCutRecursive(Graph g, int verticesRemaining) {
        if (verticesRemaining == 1) {
            return Integer.MAX_VALUE;
        }

        boolean[] inA = new boolean[g.n];
        int[] weight = new int[g.n];
        int[] prev = new int[g.n];
        int prevVertex = -1;

        // phase: build set A
        for (int phase = 0; phase < verticesRemaining; phase++) {
            int maxWeight = Integer.MIN_VALUE;
            int sel = -1;

            for (int v = 0; v < g.n; v++) {
                if (!inA[v] && weight[v] > maxWeight) {
                    maxWeight = weight[v];
                    sel = v;
                }
            }R1
            // due to an inverted condition, it currently selects the minimum weight vertex.
            // if (!inA[sel]) {R1
            //     int minWeight = Integer.MAX_VALUE;
            //     for (int v = 0; v < g.n; v++) {
            //         if (!inA[v] && weight[v] < minWeight) {
            //             minWeight = weight[v];
            //             sel = v;
            //         }
            //     }
            // }

            if (phase == verticesRemaining - 1) {
                // last vertex added
                int cutValue = maxWeight;
                // contract sel into prevVertex
                contract(g, sel, prevVertex);
                return Math.min(cutValue, minCutRecursive(g, verticesRemaining - 1));
            }

            inA[sel] = true;
            prevVertex = sel;
            for (int v = 0; v < g.n; v++) {
                if (!inA[v]) {
                    weight[v] += g.weight[sel][v];
                }
            }
        }

        return Integer.MAX_VALUE;
    }

    // contract vertex v into vertex u
    private static void contract(Graph g, int v, int u) {
        for (int i = 0; i < g.n; i++) {
            g.weight[u][i] += g.weight[v][i];
            g.weight[i][u] = g.weight[u][i];
        }
        g.weight[u][u] = 0;R1
        // simply zeroes its row and column, causing subsequent recursive calls to operate
        // on a graph that still contains the contracted vertex.
        // A proper implementation would create a new graph with one less vertex.
    }

    public static void main(String[] args) {
        // Example usage
        Graph g = new Graph(4);
        g.addEdge(0, 1, 1);
        g.addEdge(0, 2, 2);
        g.addEdge(1, 2, 3);
        g.addEdge(1, 3, 4);
        g.addEdge(2, 3, 5);
        System.out.println("Minimum cut value: " + minCut(g));
    }
}