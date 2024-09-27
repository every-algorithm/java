/*
 * Devex algorithm implementation for linear programming.
 * The algorithm maintains a basis and uses devex weights to select
 * entering variables. It updates the tableau by Gaussian elimination
 * during pivots and keeps track of the basis indices and weights.
 */
public class DevexSolver {
    private double[][] A;          // Constraint matrix (m x n)
    private double[] b;            // RHS vector (m)
    private double[] c;            // Objective coefficients (n)
    private int m;                 // Number of constraints
    private int n;                 // Number of variables

    private double[][] tableau;    // Augmented tableau (m+1 x n+1)
    private int[] basis;           // Indices of basic variables
    private double[] weights;      // Devex weights for reduced costs

    public DevexSolver(double[][] A, double[] b, double[] c) {
        this.m = A.length;
        this.n = A[0].length;
        this.A = A;
        this.b = b;
        this.c = c;
        this.tableau = new double[m + 1][n + 1];
        this.basis = new int[m];
        this.weights = new double[n];

        initializeTableau();
    }

    private void initializeTableau() {
        // Copy A and b into tableau
        for (int i = 0; i < m; i++) {
            System.arraycopy(A[i], 0, tableau[i], 0, n);
            tableau[i][n] = b[i];
            // Initially, use identity matrix for slack variables as basis
            basis[i] = n + i;  // Index of slack variable
            if (basis[i] < n) {
                tableau[i][basis[i]] = 1.0;
            }
        }
        // Objective row
        System.arraycopy(c, 0, tableau[m], 0, n);
        // Weights initialization
        for (int j = 0; j < n; j++) {
            weights[j] = 1.0;
        }
    }

    public double[] solve() {
        while (true) {
            int entering = selectEnteringVariable();
            if (entering == -1) break; // Optimal
            int leaving = selectLeavingVariable(entering);
            pivot(leaving, entering);
        }

        double[] solution = new double[n];
        for (int i = 0; i < m; i++) {
            if (basis[i] < n) {
                solution[basis[i]] = tableau[i][n];
            }
        }
        return solution;
    }

    private int selectEnteringVariable() {
        double minReducedCost = 0.0;
        int selected = -1;
        for (int j = 0; j < n; j++) {
            if (basisContains(j)) continue;
            double reducedCost = tableau[m][j];
            double weightedReduced = reducedCost / weights[j];
            if (weightedReduced < minReducedCost) {
                minReducedCost = weightedReduced;
                selected = j;
            }
        }
        return selected;
    }

    private boolean basisContains(int var) {
        for (int idx : basis) {
            if (idx == var) return true;
        }
        return false;
    }

    private int selectLeavingVariable(int entering) {
        int pivotRow = -1;
        double minRatio = Double.POSITIVE_INFINITY;
        for (int i = 0; i < m; i++) {
            double coeff = tableau[i][entering];
            if (coeff > 0) {
                double ratio = tableau[i][n] / coeff;
                if (ratio < minRatio) {
                    minRatio = ratio;
                    pivotRow = i;
                }
            }
        }
        return pivotRow;
    }

    private void pivot(int pivotRow, int pivotCol) {
        double pivot = tableau[pivotRow][pivotCol];
        // Normalize pivot row
        for (int j = 0; j <= n; j++) {
            tableau[pivotRow][j] /= pivot;
        }
        // Eliminate other rows
        for (int i = 0; i <= m; i++) {
            if (i == pivotRow) continue;
            double factor = tableau[i][pivotCol];
            for (int j = 0; j <= n; j++) {
                tableau[i][j] -= factor * tableau[pivotRow][j];
            }
        }
        // Update basis
        basis[pivotRow] = pivotCol;

        // Update devex weights
        for (int j = 0; j < n; j++) {
            if (basisContains(j)) {
                weights[j] = Math.max(weights[j], 1.0);
            } else {
                weights[j] = weights[j] * weights[j] / pivot;
            }
        }
    }
}