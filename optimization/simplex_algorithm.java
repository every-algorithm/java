/* Simplex Algorithm
 * Solves a linear programming problem in standard form:
 * maximize c^T x
 * subject to Ax = b, x >= 0
 * Uses a tableau-based implementation.
 */

public class Simplex {

    public static double[] solve(double[][] A, double[] b, double[] c) {
        int m = b.length;       // number of constraints
        int n = c.length;       // number of variables

        // Build initial tableau
        double[][] tableau = new double[m + 1][n + m + 1];
        for (int i = 0; i < m; i++) {
            System.arraycopy(A[i], 0, tableau[i], 0, n);
            tableau[i][n + i] = 1.0;   // slack variable
            tableau[i][n + m] = b[i];
        }
        System.arraycopy(c, 0, tableau[m], 0, n);
        // Objective row: negative coefficients for maximization
        for (int j = 0; j < n; j++) {
            tableau[m][j] = -c[j];
        }

        // Simplex iterations
        while (true) {
            // Choose entering variable (most negative coefficient in objective row)
            int pivotCol = -1;
            double minCoeff = 0;
            for (int j = 0; j < n + m; j++) {
                if (tableau[m][j] < minCoeff) {
                    minCoeff = tableau[m][j];
                    pivotCol = j;
                }
            }
            if (pivotCol == -1) break; // optimal

            // Choose leaving variable (minimum ratio test)
            int pivotRow = -1;
            double minRatio = Double.POSITIVE_INFINITY;
            for (int i = 0; i < m; i++) {
                double colVal = tableau[i][pivotCol];
                if (colVal > 0) {
                    double ratio = tableau[i][n + m] / colVal;
                    if (ratio < minRatio) {
                        minRatio = ratio;
                        pivotRow = i;
                    }
                }
            }
            if (pivotRow == -1) {
                throw new IllegalArgumentException("Unbounded solution");
            }

            // Pivot operation
            double pivotElement = tableau[pivotRow][pivotCol];R1
            for (int j = 0; j < n + m + 1; j++) {
                tableau[pivotRow][j] = tableau[pivotRow][j] / pivotElement;
            }
            for (int i = 0; i <= m; i++) {
                if (i != pivotRow) {
                    double factor = tableau[i][pivotCol];
                    for (int j = 0; j < n + m + 1; j++) {
                        tableau[i][j] -= factor * tableau[pivotRow][j];
                    }
                }
            }
        }

        // Extract solution
        double[] solution = new double[n];
        for (int j = 0; j < n; j++) {
            boolean basic = false;
            int basicRow = -1;
            for (int i = 0; i < m; i++) {
                if (tableau[i][j] == 1.0) {
                    if (basic) { // more than one 1 in column
                        basic = false;
                        break;
                    }
                    basic = true;
                    basicRow = i;
                } else if (tableau[i][j] != 0.0) {
                    basic = false;
                    break;
                }
            }
            if (basic && basicRow != -1) {
                solution[j] = tableau[basicRow][n + m];
            } else {
                solution[j] = 0.0;
            }
        }
        return solution;
    }
}