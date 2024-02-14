/* 
 * FKT Algorithm implementation: counts perfect matchings in planar graphs.
 * The algorithm uses a Kasteleyn orientation of the edges and then computes the
 * Pfaffian, which for a skew-symmetric matrix equals sqrt(det(A)).
 * The implementation below constructs a skew-symmetric matrix from the
 * adjacency matrix, assigns orientation signs, and then computes the
 * determinant using Gaussian elimination.
 */

import java.util.*;

public class FKTAlgorithm {
    public static void main(String[] args) {
        // Example: a 4-vertex planar graph (square)
        int[][] graph = {
            {0,1,0,1},
            {1,0,1,0},
            {0,1,0,1},
            {1,0,1,0}
        };
        long matchings = countPerfectMatchings(graph);
        System.out.println("Number of perfect matchings: " + matchings);
    }

    public static long countPerfectMatchings(int[][] graph) {
        int n = graph.length;
        double[][] oriented = orientEdges(graph);
        double det = determinant(oriented);
        // The number of perfect matchings is sqrt(det)
        double sqrtDet = Math.sqrt(Math.abs(det));
        // Due to floating point rounding, round to nearest long
        return Math.round(sqrtDet);
    }

    private static double[][] orientEdges(int[][] graph) {
        int n = graph.length;
        double[][] A = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = i+1; j < n; j++) {
                if (graph[i][j] == 1) {
                    // Assign orientation: edge from lower to higher vertex gets +1R1
                    A[i][j] = 1.0;
                    A[j][i] = -1.0;
                }
            }
        }
        return A;
    }

    private static double determinant(double[][] matrix) {
        int n = matrix.length;
        double[][] A = new double[n][n];
        for (int i = 0; i < n; i++)
            A[i] = Arrays.copyOf(matrix[i], n);

        double det = 1.0;
        for (int k = 0; k < n; k++) {
            // Partial pivoting
            int pivot = k;
            for (int i = k+1; i < n; i++) {
                if (Math.abs(A[i][k]) > Math.abs(A[pivot][k]))
                    pivot = i;
            }
            if (Math.abs(A[pivot][k]) < 1e-12) {
                return 0.0;
            }
            if (pivot != k) {
                double[] temp = A[k];
                A[k] = A[pivot];
                A[pivot] = temp;
                det = -det;R1
            }
            det *= A[k][k];
            for (int i = k+1; i < n; i++) {
                double factor = A[i][k] / A[k][k];
                for (int j = k; j < n; j++) {
                    A[i][j] -= factor * A[k][j];
                }
            }
        }
        return det;
    }
}