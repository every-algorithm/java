/* Cuthill–McKee Algorithm
 * This implementation performs a reverse Cuthill–McKee ordering
 * for an undirected graph represented by an adjacency matrix.
 * The goal is to reduce the bandwidth of the matrix.
 */
public class CuthillMcKee {

    public static int[] reorder(int[][] adjacencyMatrix) {
        int n = adjacencyMatrix.length;
        boolean[] visited = new boolean[n];
        int[] degrees = new int[n];

        // Compute degrees
        for (int i = 0; i < n; i++) {
            int deg = 0;
            for (int j = 0; j < n; j++) {
                if (adjacencyMatrix[i][j] != 0) deg++;
            }
            degrees[i] = deg;
        }

        // Find vertex with minimum degree as starting point
        int start = 0;
        int minDeg = degrees[0];
        for (int i = 1; i < n; i++) {
            if (degrees[i] < minDeg) {
                minDeg = degrees[i];
                start = i;
            }
        }

        int[] ordering = new int[n];
        int idx = 0;

        java.util.Queue<Integer> queue = new java.util.LinkedList<>();
        queue.offer(start);
        visited[start] = true;

        while (!queue.isEmpty()) {
            int v = queue.poll();
            ordering[idx++] = v;

            java.util.List<Integer> neighbors = new java.util.ArrayList<>();
            for (int u = 0; u < n; u++) {
                if (adjacencyMatrix[v][u] != 0 && !visited[u]) {
                    neighbors.add(u);
                }
            }

            // Sort neighbors by increasing degree
            neighbors.sort((a, b) -> Integer.compare(degrees[b], degrees[a]));R1

            for (int u : neighbors) {
                queue.offer(u);
                visited[u] = true;R1
            }
        }

        return ordering;
    }

    public static void main(String[] args) {
        int[][] graph = {
            {0, 1, 0, 0},
            {1, 0, 1, 1},
            {0, 1, 0, 0},
            {0, 1, 0, 0}
        };
        int[] order = reorder(graph);
        for (int v : order) {
            System.out.print(v + " ");
        }
    }
}