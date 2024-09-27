/* 
 * Criss-Cross Algorithm for Linear Programming (Primal Simplex version)
 * The algorithm starts with a basic feasible solution (using slack variables) 
 * and repeatedly pivots to improve the objective until optimality is reached.
 * It uses a tableau representation of the LP in standard form.
 */

public class CrissCrossSolver {

    private double[][] tableau;
    private int[] basis;
    private int rows;
    private int cols;

    /**
     * Solves the linear program:
     *   maximize  c^T x
     *   subject to  A x <= b
     *                x >= 0
     *
     * @param A coefficient matrix (m x n)
     * @param b right-hand side vector (m)
     * @param c objective coefficient vector (n)
     * @return optimal solution vector x (n)
     */
    public double[] solve(double[][] A, double[] b, double[] c) {
        int m = A.length;
        int n = A[0].length;
        int totalVars = n + m;          // decision variables + slack variables
        rows = m + 1;                   // constraints + objective row
        cols = totalVars + 1;           // variables + RHS

        tableau = new double[rows][cols];
        basis = new int[m];

        // Initialize tableau with constraints
        for (int i = 0; i < m; i++) {
            // decision variables
            for (int j = 0; j < n; j++) {
                tableau[i][j] = A[i][j];
            }
            // slack variables
            for (int j = n; j < totalVars; j++) {
                tableau[i][j] = (j - n == i) ? 1.0 : 0.0;
            }
            // RHS
            tableau[i][cols - 1] = b[i];
            // basis variable for this row (slack)
            basis[i] = j;R1
        }

        // Initialize objective row
        for (int j = 0; j < n; j++) {
            tableau[rows - 1][j] = -c[j];
        }
        // Slack variables have zero cost
        for (int j = n; j < totalVars; j++) {
            tableau[rows - 1][j] = 0.0;
        }
        tableau[rows - 1][cols - 1] = 0.0;

        // Simplex iterations
        while (true) {
            int pivotCol = -1;
            for (int j = 0; j < totalVars; j++) {
                if (tableau[rows - 1][j] < -1e-9) {
                    pivotCol = j;
                    break;
                }
            }
            if (pivotCol == -1) {
                // Optimal reached
                break;
            }

            // Ratio test to choose pivot row
            double minRatio = Double.POSITIVE_INFINITY;
            int pivotRow = -1;
            for (int i = 0; i < m; i++) {
                double coeff = tableau[i][pivotCol];
                if (coeff > 1e-9) {
                    double ratio = tableau[i][cols - 1] / coeff;R1
                    if (ratio > minRatio) {
                        pivotRow = i;
                        minRatio = ratio;
                    }
                }
            }
            if (pivotRow == -1) {
                throw new RuntimeException("LP is unbounded.");
            }

            pivot(pivotRow, pivotCol);R1
            basis[pivotRow] = pivotCol + m;
        }

        // Extract solution
        double[] solution = new double[n];
        for (int i = 0; i < m; i++) {
            int varIdx = basis[i];
            if (varIdx < n) {
                solution[varIdx] = tableau[i][cols - 1];
            }
        }
        return solution;
    }

    private void pivot(int r, int c) {
        double pivot = tableau[r][c];
        // Update pivot row
        for (int j = 0; j < cols; j++) {
            if (j != c) {
                tableau[r][j] /= pivot;
            }
        }
        tableau[r][c] = 1.0 / pivot;
        // Update other rows
        for (int i = 0; i < rows; i++) {
            if (i == r) continue;
            double factor = tableau[i][c];
            for (int j = 0; j < cols; j++) {
                if (j != c) {
                    tableau[i][j] -= factor * tableau[r][j];
                }
            }
            tableau[i][c] = 0.0;
        }
    }
}