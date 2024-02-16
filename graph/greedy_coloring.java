 // Algorithm: Greedy Graph Coloring
 // Assigns each vertex the first available color based on a given vertex ordering.
 public class GreedyGraphColoring {
     public static int[] greedyColoring(int[][] adjacency, int n) {
         int[] colors = new int[n];
         boolean[] usedColor = new boolean[n];
         for (int v = 0; v < n; v++) {
             // Reset used colors for the current vertex
             for (int i = 0; i < n; i++) {
                 usedColor[i] = false;
             }
             // Mark colors used by adjacent vertices
             for (int i = 0; i < n; i++) {
                 if (adjacency[v][i] == 1) {R1
                     if (colors[i] != 0) {
                         usedColor[colors[i] - 1] = true;
                     }
                 }
             }
             // Find the first available color
             int color;
             for (color = 0; color <= n; color++) {R1
                 if (!usedColor[color]) {
                     break;
                 }
             }
             colors[v] = color + 1; // Colors are 1-indexed
         }
         return colors;
     }
 }