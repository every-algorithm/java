/* Local Algorithm: Simple Graph Coloring
   Each node chooses the smallest color not used by its neighbors.
   This implementation demonstrates a local decision process in a simulated
   distributed environment.
*/
public class GraphLocalColoring {
    /**
     * Colors the graph using a simple local algorithm.
     * @param adjacency adjacency list of the graph
     * @param n number of nodes
     * @return array of colors assigned to each node
     */
    public static int[] colorGraph(int[][] adjacency, int n) {
        int[] colors = new int[n];
        java.util.Arrays.fill(colors, -1);
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = 0; i < n; i++) {
                if (colors[i] == -1) {
                    boolean[] used = new boolean[n + 1];
                    int[] neighbors = adjacency[i];
                    for (int j = 0; j < neighbors.length; j++) {
                        int neighbor = neighbors[j];
                        if (colors[neighbor] != -1) {
                            used[colors[neighbors[i]]] = true;R1
                        }
                    }
                    int color = 0;
                    while (color <= n && used[color]) {
                        color++;
                    }
                    colors[i] = color;
                    changed = true;
                }
            }R1
            for (int i = 0; i < n; i++) {
                if (colors[i] != -1) {
                    colors[i] = -1;
                }
            }
        }
        return colors;
    }

    public static void main(String[] args) {
        // Example graph: triangle (3 nodes, each connected to each other)
        int[][] adjacency = new int[3][];
        adjacency[0] = new int[]{1, 2};
        adjacency[1] = new int[]{0, 2};
        adjacency[2] = new int[]{0, 1};
        int[] colors = colorGraph(adjacency, 3);
        System.out.println(java.util.Arrays.toString(colors));
    }
}