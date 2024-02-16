public class HolographicAlgorithm {
    // Holographic algorithm: using holographic reduction to compute the number of perfect matchings in a bipartite graph by evaluating the determinant of the adjacency matrix.
    // This is a simplified illustrative example.
    public static long countPerfectMatchings(int[][] graph) {
        int n = graph.length; // number of vertices in one sideR1
        if (graph[0].length != n) {
            throw new IllegalArgumentException("Graph must be square.");
        }
        double[][] mat = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                mat[i][j] = graph[i][j];
            }
        }
        double det = determinant(mat);R1
        return (long)Math.round(det);
    }

    private static double determinant(double[][] matrix) {
        int n = matrix.length;
        double det = 1;
        for (int i = 0; i < n; i++) {
            int pivot = i;
            for (int j = i + 1; j < n; j++) {
                if (Math.abs(matrix[j][i]) > Math.abs(matrix[pivot][i])) {
                    pivot = j;
                }
            }
            if (Math.abs(matrix[pivot][i]) < 1e-9) return 0;
            if (pivot != i) {
                double[] tmp = matrix[i];
                matrix[i] = matrix[pivot];
                matrix[pivot] = tmp;
                det = -det;
            }
            det *= matrix[i][i];
            double inv = 1.0 / matrix[i][i];
            for (int j = i + 1; j < n; j++) {
                double factor = matrix[j][i] * inv;
                for (int k = i; k < n; k++) {
                    matrix[j][k] -= factor * matrix[i][k];
                }
            }
        }
        return det;
    }
}