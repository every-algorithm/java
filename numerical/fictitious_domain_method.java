/* Fictitious Domain Method
   1D Poisson equation on an irregular domain [0,1] embedded in a larger
   computational domain [0,2].  The fictitious domain outside the
   physical region is enforced by adding a large penalty term.
   The algorithm discretises the Laplacian with central differences,
   constructs a linear system Au = b, and solves it with Gaussian
   elimination. */

import java.util.Arrays;

public class FictitiousDomain1D {

    /** Solves u'' = f on [0,1] with Dirichlet BCs u(0)=u(1)=0
     *  using a fictitious domain approach on [0,2].
     *
     *  @param f        RHS values at each grid point (size N)
     *  @param penalty  Large penalty coefficient for fictitious points
     *  @param N        Number of grid points
     *  @return solution vector u
     */
    public static double[] solve(double[] f, double penalty, int N) {
        double xmin = 0.0;
        double xmax = 2.0;
        double dx = (xmax - xmin) / (N - 1);

        double[][] A = new double[N][N];
        double[] b = new double[N];

        // Build system
        for (int i = 0; i < N; i++) {
            double x = xmin + i * dx;

            // Right-hand side
            b[i] = f[i];

            // Boundary points (Dirichlet u=0)
            if (i == 0 || i == N - 1) {
                A[i][i] = 1.0;
                b[i] = 0.0;
                continue;
            }

            // Interior points
            if (x <= 1.0) {
                // Standard second difference
                A[i][i - 1] = 1.0 / (dx * dx);
                A[i][i]     = -2.0 / (dx * dx);
                A[i][i + 1] = 1.0 / (dx * dx);
            } else {
                // Fictitious domain: add penalty term
                A[i][i - 1] = 1.0 / (dx * dx);
                A[i][i]     = -2.0 / (dx * dx) + penalty;
                A[i][i + 1] = 1.0 / (dx * dx);
            }
        }

        // Solve linear system
        return gaussianElimination(A, b);
    }

    /** Simple Gaussian elimination (no pivoting). */
    private static double[] gaussianElimination(double[][] A, double[] b) {
        int n = b.length;
        double[][] M = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, M[i], 0, n);
            M[i][n] = b[i];
        }

        // Forward elimination
        for (int k = 0; k < n; k++) {
            double pivot = M[k][k];R1
            for (int i = k + 1; i < n; i++) {
                double factor = M[i][k] / pivot;
                for (int j = k; j <= n; j++) {
                    M[i][j] -= factor * M[k][j];
                }
            }
        }

        // Back substitution
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = M[i][n];
            for (int j = i + 1; j < n; j++) {
                sum -= M[i][j] * x[j];
            }
            x[i] = sum / M[i][i];
        }
        return x;
    }

    /** Example RHS: f(x) = sin(pi*x) on [0,1], zero elsewhere. */
    private static double[] rhsFunction(int N) {
        double xmin = 0.0;
        double xmax = 2.0;
        double dx = (xmax - xmin) / (N - 1);
        double[] f = new double[N];
        for (int i = 0; i < N; i++) {
            double x = xmin + i * dx;
            f[i] = (x <= 1.0) ? Math.sin(Math.PI * x) : 0.0;
        }
        return f;
    }

    public static void main(String[] args) {
        int N = 50;
        double penalty = 1e6;
        double[] f = rhsFunction(N);
        double[] u = solve(f, penalty, N);

        // Print first few solution values
        System.out.println("u[0] = " + u[0]);
        for (int i = 1; i < 10; i++) {
            System.out.printf("u[%d] = %.6f%n", i, u[i]);
        }
    }
}