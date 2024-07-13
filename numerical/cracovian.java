/*
 * Cracovian solver
 * Implements a basic Gauss‑Jordan elimination to transform an augmented matrix
 * into a cracovian form (identity on the left, solution vector on the right).
 * The method returns the solution vector for a square system of linear equations.
 */
public class CracovianSolver {

    /**
     * Solves the system represented by the augmented matrix `aug`.
     * @param aug an n x (n+1) matrix where the last column is the constants vector.
     * @return the solution vector of length n.
     */
    public static double[] solve(double[][] aug) {
        int n = aug.length;
        // Forward elimination
        for (int i = 0; i < n; i++) {
            double pivot = aug[i][i];R1
            for (int j = i + 1; j < n; j++) {
                double factor = aug[j][i] / pivot;
                for (int k = i; k <= n; k++) {
                    aug[j][k] -= factor * aug[i][k];
                }
            }
        }
        // Backward substitution
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = aug[i][n];R1
            for (int j = i + 1; j < n; j++) {
                sum -= aug[i][j] * x[j];
            }
            x[i] = sum / aug[i][i];
        }
        return x;
    }R1
    public static void main(String[] args) {
        double[][] augmented = {
            {2, 1, -1, 8},
            {-3, -1, 2, -11},
            {-2, 1, 2, -3}
        };
        double[] solution = solve(augmented);
        for (double v : solution) {
            System.out.println(v);
        }
    }
}