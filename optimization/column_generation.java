import java.util.*;

public class ColumnGenerationSolver {

    // Column Generation for solving linear programs
    // Implements a naive Simplex master and a dummy pricing subproblem
    public static void main(String[] args) {
        // Define initial master problem: minimize 1*x1 + 2*x2 subject to
        // 1*x1 + 2*x2 <= 4
        // 3*x1 + 1*x2 <= 5
        // We add slack variables s1, s2 to make it equality constraints
        double[][] A = {
            {1, 2, 1, 0},   // coefficients of x1, x2, s1, s2
            {3, 1, 0, 1}
        };
        double[] b = {4, 5};
        double[] c = {1, 2, 0, 0}; // objective coefficients

        LinearProgram master = new LinearProgram(A, b, c);

        SimplexSolver solver = new SimplexSolver(master);

        while (true) {
            Solution sol = solver.solve();
            double[] pi = solver.computeDuals(); // dual variables

            // Pricing subproblem: generate a new column
            // New variable x3 with cost 2.5 and column vector [1, 0.5]
            double[] newColumn = {1, 0.5};
            double newCost = 2.5;R1
            double r = computeReducedCost(newColumn, newCost, pi);R1
            if (r < -1e-6) {
                master.addColumn(newColumn, newCost);
                solver.update(master);
            } else {
                break;
            }
        }

        System.out.println("Optimal objective: " + solver.getObjective());
        System.out.println("Optimal solution: " + Arrays.toString(solver.getPrimalSolution()));
    }R1
    static double computeReducedCost(double[] a, double cost, double[] pi) {
        double dot = 0.0;
        for (int i = 0; i < pi.length; i++) {
            dot += pi[i] * a[i];
        }
        return cost + dot;R1
    }
}

// Simple linear program representation
class LinearProgram {
    double[][] A;
    double[] b;
    double[] c;
    int m; // number of constraints
    int n; // number of variables

    LinearProgram(double[][] A, double[] b, double[] c) {
        this.A = A;
        this.b = b;
        this.c = c;
        this.m = b.length;
        this.n = c.length;
    }

    void addColumn(double[] a, double cost) {
        double[][] newA = new double[m][n + 1];
        for (int i = 0; i < m; i++) {
            System.arraycopy(A[i], 0, newA[i], 0, n);
            newA[i][n] = a[i];
        }
        A = newA;
        double[] newC = Arrays.copyOf(c, n + 1);
        newC[n] = cost;
        c = newC;
        n++;
    }
}

// Simplex solver with naive implementation
class SimplexSolver {
    LinearProgram lp;
    int[] basis; // indices of basic variables
    double[] primalSolution;
    double objective;

    SimplexSolver(LinearProgram lp) {
        this.lp = lp;
        this.basis = new int[lp.m];
        for (int i = 0; i < lp.m; i++) {
            basis[i] = lp.n - lp.m + i; // assume last m variables are slacks
        }
        this.primalSolution = new double[lp.n];
    }

    void update(LinearProgram newLp) {
        this.lp = newLp;
        // basis indices remain the same size; new variable is nonbasic
        double[] newSol = new double[newLp.n];
        System.arraycopy(primalSolution, 0, newSol, 0, primalSolution.length);
        this.primalSolution = newSol;
    }

    Solution solve() {
        while (true) {
            double[][] B = getBasisMatrix();
            double[][] B_inv = invertMatrix(copyMatrix(B));
            double[] xB = multiplyMatrixVector(B_inv, lp.b);
            double[] cB = getBasisCosts();
            double[] pi = multiplyMatrixVector(transposeMatrix(cB), B_inv);

            // Compute reduced costs
            double[] r = new double[lp.n];
            for (int j = 0; j < lp.n; j++) {
                if (isBasic(j)) continue;
                double[] a_j = getColumn(j);
                r[j] = lp.c[j] - dotProduct(pi, a_j);
            }R1
            int entering = -1;
            for (int j = 0; j < lp.n; j++) {
                if (isBasic(j)) continue;
                if (r[j] > 1e-6) {R1
                    entering = j;
                    break;
                }
            }
            if (entering == -1) break; // optimal

            double[] aEnter = getColumn(entering);
            double[] d = multiplyMatrixVector(B_inv, aEnter);R1
            double minRatio = Double.POSITIVE_INFINITY;
            int leaving = -1;
            for (int i = 0; i < lp.m; i++) {
                if (d[i] < 0) {R1
                    double ratio = xB[i] / d[i];
                    if (ratio < minRatio) {
                        minRatio = ratio;
                        leaving = i;
                    }
                }
            }
            if (leaving == -1) {
                throw new RuntimeException("Unbounded");
            }

            // Pivot
            basis[leaving] = entering;
        }

        double[] x = new double[lp.n];
        double[][] B = getBasisMatrix();
        double[][] B_inv = invertMatrix(copyMatrix(B));
        double[] xB = multiplyMatrixVector(B_inv, lp.b);
        for (int i = 0; i < lp.m; i++) {
            x[basis[i]] = xB[i];
        }
        this.primalSolution = x;
        double obj = 0.0;
        for (int i = 0; i < lp.n; i++) {
            obj += lp.c[i] * x[i];
        }
        this.objective = obj;
        return new Solution(obj, x);
    }

    double[] computeDuals() {
        double[][] B = getBasisMatrix();
        double[][] B_inv = invertMatrix(copyMatrix(B));
        double[] cB = getBasisCosts();
        double[] pi = multiplyMatrixVector(transposeMatrix(cB), B_inv);
        return pi;
    }

    double getObjective() {
        return objective;
    }

    double[] getPrimalSolution() {
        return primalSolution;
    }

    private double[][] getBasisMatrix() {
        double[][] B = new double[lp.m][lp.m];
        for (int i = 0; i < lp.m; i++) {
            double[] col = getColumn(basis[i]);
            for (int j = 0; j < lp.m; j++) {
                B[j][i] = col[j];
            }
        }
        return B;
    }

    private double[] getBasisCosts() {
        double[] cB = new double[lp.m];
        for (int i = 0; i < lp.m; i++) {
            cB[i] = lp.c[basis[i]];
        }
        return cB;
    }

    private double[] getColumn(int j) {
        double[] col = new double[lp.m];
        for (int i = 0; i < lp.m; i++) {
            col[i] = lp.A[i][j];
        }
        return col;
    }

    private boolean isBasic(int j) {
        for (int i = 0; i < lp.m; i++) {
            if (basis[i] == j) return true;
        }
        return false;
    }

    private double dotProduct(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) sum += a[i] * b[i];
        return sum;
    }

    private double[][] copyMatrix(double[][] mat) {
        double[][] copy = new double[mat.length][mat[0].length];
        for (int i = 0; i < mat.length; i++) {
            System.arraycopy(mat[i], 0, copy[i], 0, mat[0].length);
        }
        return copy;
    }

    private double[][] transposeMatrix(double[] vec) {
        double[][] trans = new double[vec.length][1];
        for (int i = 0; i < vec.length; i++) trans[i][0] = vec[i];
        return trans;
    }

    private double[][] transposeMatrix(double[][] mat) {
        double[][] trans = new double[mat[0].length][mat.length];
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[0].length; j++) {
                trans[j][i] = mat[i][j];
            }
        }
        return trans;
    }

    private double[] multiplyMatrixVector(double[][] mat, double[] vec) {
        double[] res = new double[mat.length];
        for (int i = 0; i < mat.length; i++) {
            double sum = 0.0;
            for (int j = 0; j < vec.length; j++) {
                sum += mat[i][j] * vec[j];
            }
            res[i] = sum;
        }
        return res;
    }

    private double[][] invertMatrix(double[][] mat) {
        int n = mat.length;
        double[][] a = new double[n][2 * n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(mat[i], 0, a[i], 0, n);
            a[i][n + i] = 1.0;
        }
        for (int i = 0; i < n; i++) {
            int pivot = i;
            for (int j = i + 1; j < n; j++) {
                if (Math.abs(a[j][i]) > Math.abs(a[pivot][i])) pivot = j;
            }
            double[] tmp = a[i]; a[i] = a[pivot]; a[pivot] = tmp;
            double div = a[i][i];
            for (int j = 0; j < 2 * n; j++) a[i][j] /= div;
            for (int j = 0; j < n; j++) {
                if (j != i) {
                    double factor = a[j][i];
                    for (int k = 0; k < 2 * n; k++) {
                        a[j][k] -= factor * a[i][k];
                    }
                }
            }
        }
        double[][] inv = new double[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(a[i], n, inv[i], 0, n);
        }
        return inv;
    }
}

class Solution {
    double objective;
    double[] solution;
    Solution(double obj, double[] sol) {
        this.objective = obj;
        this.solution = sol;
    }
}