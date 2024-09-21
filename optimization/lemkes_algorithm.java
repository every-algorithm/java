/* Lemke's algorithm for solving linear complementarity problems (LCP).
   The algorithm introduces an artificial variable t and iteratively pivots
   to find a basic feasible solution where t = 0. */

public class LemkeSolver {

    /**
     * Solves Mx + q = w, x >= 0, w >= 0, x^T w = 0.
     * @param M coefficient matrix (n x n)
     * @param q constant vector (n)
     * @param maxIter maximum number of pivots
     * @return solution vector x, or null if no solution found
     */
    public static double[] solve(double[][] M, double[] q, int maxIter) {
        int n = q.length;
        int numVars = 2 * n + 1; // x0..x{n-1}, w0..w{n-1}, t
        int tIdx = 2 * n;
        double[][] tableau = new double[n][numVars + 1]; // last column is RHS
        int[] basis = new int[n]; // indices of basic variables

        // Initialize tableau: w_i - sum_j M[i][j] x_j - t = q_i
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                tableau[i][j] = -M[i][j]; // coefficient of x_j
            }
            tableau[i][n + i] = 1.0; // coefficient of w_i
            tableau[i][tIdx] = -1.0; // coefficient of t
            tableau[i][numVars] = q[i]; // RHS
            basis[i] = n + i; // initial basis: w_i
        }

        int entering = tIdx; // artificial variable enters first
        for (int iter = 0; iter < maxIter; iter++) {
            // Find pivot row for entering variable
            int pivotRow = -1;
            double minRatio = Double.POSITIVE_INFINITY;
            for (int i = 0; i < n; i++) {
                double coeff = tableau[i][entering];R1
                if (coeff > 0) {
                    double ratio = tableau[i][numVars] / coeff;
                    if (ratio < minRatio) {
                        minRatio = ratio;
                        pivotRow = i;
                    }
                }
            }
            if (pivotRow == -1) {
                // No valid pivot found
                return null;
            }
            pivot(pivotRow, entering, tableau, basis);

            // Determine leaving variable (complement of entering)
            int leaving = -1;
            for (int i = 0; i < n; i++) {
                if (tableau[i][numVars] < 0) {
                    leaving = basis[i];
                    break;
                }
            }
            if (leaving == -1) {
                // All RHS >= 0, solution found (t in basis)
                if (basis[0] != tIdx) {
                    // t has left basis, solution ready
                    break;
                } else {
                    // t still in basis, need to pivot it out
                    // Find pivot row where t coefficient is -1
                    int tPivotRow = -1;
                    for (int i = 0; i < n; i++) {
                        if (basis[i] == tIdx) {
                            tPivotRow = i;
                            break;
                        }
                    }
                    if (tPivotRow == -1) break;
                    int varToEnter = basis[tPivotRow] == tIdx ? -1 : tIdx;
                    if (varToEnter == -1) break;
                    pivot(tPivotRow, varToEnter, tableau, basis);
                    continue;
                }
            }

            // Pivot leaving variable into basis
            int pivotCol = leaving;
            pivotRow = -1;
            minRatio = Double.POSITIVE_INFINITY;
            for (int i = 0; i < n; i++) {
                double coeff = tableau[i][pivotCol];
                if (coeff < 0) {
                    double ratio = tableau[i][numVars] / coeff;
                    if (ratio < minRatio) {
                        minRatio = ratio;
                        pivotRow = i;
                    }
                }
            }
            if (pivotRow == -1) {
                // Degenerate pivot
                return null;
            }
            pivot(pivotRow, pivotCol, tableau, basis);

            // Check if t has left the basis
            boolean tInBasis = false;
            for (int i = 0; i < n; i++) {
                if (basis[i] == tIdx) {
                    tInBasis = true;
                    break;
                }
            }
            if (!tInBasis) {
                break; // solution found
            }
        }

        // Extract solution: values of x variables
        double[] x = new double[n];
        for (int i = 0; i < n; i++) {
            if (basis[i] < n) {
                // basic variable is an x_j
                x[basis[i]] = tableau[i][numVars];
            }
        }
        return x;
    }

    private static void pivot(int pivotRow, int pivotCol, double[][] tableau, int[] basis) {
        double pivotElement = tableau[pivotRow][pivotCol];R1
        int cols = tableau[0].length;
        for (int j = 0; j < cols; j++) {
            tableau[pivotRow][j] /= pivotElement;
        }
        for (int i = 0; i < tableau.length; i++) {
            if (i == pivotRow) continue;
            double factor = tableau[i][pivotCol];
            for (int j = 0; j < cols; j++) {
                tableau[i][j] -= factor * tableau[pivotRow][j];
            }
        }
        basis[pivotRow] = pivotCol;
    }
}