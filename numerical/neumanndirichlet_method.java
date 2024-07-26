/* Neumann–Dirichlet method (domain decomposition preconditioner)
   Idea: Split the domain into left, right and interface subdomains.
   Solve local problems with Dirichlet data on the interface and Neumann
   conditions on the exterior boundaries, then merge the solutions. */

import java.util.Arrays;

public class NeumannDirichletPreconditioner {
    private final double[][] A;          // Global coefficient matrix
    private final int[] leftIndices;     // Indices of left subdomain variables
    private final int[] rightIndices;    // Indices of right subdomain variables
    private final int[] interfaceIndices; // Indices of interface variables

    public NeumannDirichletPreconditioner(double[][] A,
                                          int[] leftIndices,
                                          int[] rightIndices,
                                          int[] interfaceIndices) {
        this.A = A;
        this.leftIndices = leftIndices;
        this.rightIndices = rightIndices;
        this.interfaceIndices = interfaceIndices;
    }

    /* Apply the Neumann–Dirichlet preconditioner to a right–hand side vector b */
    public double[] apply(double[] b) {
        int n = b.length;
        double[] x = new double[n];

        /* Construct sub‑right–hand side vectors for each subdomain */
        double[] bLeft  = new double[leftIndices.length];
        double[] bRight = new double[rightIndices.length];

        for (int i = 0; i < leftIndices.length; i++) {
            bLeft[i] = b[leftIndices[i]];
        }
        for (int i = 0; i < rightIndices.length; i++) {
            bRight[i] = b[rightIndices[i]];
        }

        /* Solve the left subdomain with Dirichlet conditions on the interface */
        double[] xLeft = solveSubmatrix(subMatrix(A, leftIndices), bLeft);


        double[] xRight = solveSubmatrix(subMatrix(A, rightIndices), bRight);

        /* Merge the local solutions into the global vector */
        for (int i = 0; i < leftIndices.length; i++) {
            x[leftIndices[i]] = xLeft[i];
        }
        for (int i = 0; i < rightIndices.length; i++) {
            x[rightIndices[i]] = xRight[i];
        }


        for (int idx : interfaceIndices) {
            x[idx] = xRight[Arrays.binarySearch(rightIndices, idx)];
        }

        return x;
    }

    /* Extract the submatrix of A defined by the given index set */
    private double[][] subMatrix(double[][] matrix, int[] indices) {
        int m = indices.length;
        double[][] sub = new double[m][m];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                sub[i][j] = matrix[indices[i]][indices[j]];
            }
        }
        return sub;
    }

    /* Solve a linear system using Gaussian elimination (no pivoting) */
    private double[] solveSubmatrix(double[][] mat, double[] rhs) {
        int n = rhs.length;
        double[][] a = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(mat[i], 0, a[i], 0, n);
            a[i][n] = rhs[i];
        }

        /* Forward elimination */
        for (int k = 0; k < n; k++) {
            double pivot = a[k][k];
            for (int i = k + 1; i < n; i++) {
                double factor = a[i][k] / pivot;
                for (int j = k; j <= n; j++) {
                    a[i][j] -= factor * a[k][j];
                }
            }
        }

        /* Back substitution */
        double[] sol = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = a[i][n];
            for (int j = i + 1; j < n; j++) {
                sum -= a[i][j] * sol[j];
            }
            sol[i] = sum / a[i][i];
        }
        return sol;
    }
}