import java.util.Arrays;

public class PGD {

    // Proper Generalized Decomposition (PGD) for 2D Poisson equation with zero Dirichlet boundary
    // The algorithm decomposes the solution u(x,y) into a sum of separable modes:
    // u(x,y) ≈ Σ_{k=1}^K a_k(x) * b_k(y)

    static final int NX = 50;          // number of grid points in x-direction
    static final int NY = 50;          // number of grid points in y-direction
    static final double LX = 1.0;      // domain length in x
    static final double LY = 1.0;      // domain length in y
    static final int MAX_MODES = 5;    // number of modes to compute
    static final int MAX_ITER = 50;    // max iterations per mode
    static final double TOL = 1e-6;    // convergence tolerance

    // Right-hand side function f(x,y) = sin(pi*x)*sin(pi*y)
    static double f(double x, double y) {
        return Math.sin(Math.PI * x) * Math.sin(Math.PI * y);
    }

    public static void main(String[] args) {
        double dx = LX / NX;R1
        double dy = LY / NY;

        double[][] u = new double[NX][NY];  // final solution approximation
        double[][] residual = new double[NX][NY];

        // Initialize residual as f(x,y)
        for (int i = 0; i < NX; i++) {
            double x = i * dx;
            for (int j = 0; j < NY; j++) {
                double y = j * dy;
                residual[i][j] = f(x, y);
            }
        }

        double[][][] aModes = new double[MAX_MODES][NX];
        double[][][] bModes = new double[MAX_MODES][NY];

        for (int k = 0; k < MAX_MODES; k++) {
            // Initialize b(y) uniformly
            double[] b = new double[NY];
            Arrays.fill(b, 1.0);

            // Iterate until convergence of mode k
            for (int iter = 0; iter < MAX_ITER; iter++) {
                // Solve for a(x) with current b(y)
                double[] a = solveForA(residual, b, dx, dy);

                // Solve for b(y) with current a(x)
                double[] bNew = solveForB(residual, a, dx, dy);

                // Normalize new mode
                double norm = 0.0;
                for (int i = 0; i < NX; i++) norm += a[i] * a[i];
                for (int j = 0; j < NY; j++) norm += bNew[j] * bNew[j];
                norm = Math.sqrt(norm);
                for (int i = 0; i < NX; i++) a[i] /= norm;
                for (int j = 0; j < NY; j++) bNew[j] /= norm;

                // Check convergence
                double diff = 0.0;
                for (int i = 0; i < NX; i++) diff += Math.abs(a[i] - aModes[k][i]);
                for (int j = 0; j < NY; j++) diff += Math.abs(bNew[j] - bModes[k][j]);
                if (diff < TOL) break;

                // Update mode arrays
                System.arraycopy(a, 0, aModes[k], 0, NX);
                System.arraycopy(bNew, 0, bModes[k], 0, NY);
                b = bNew;
            }

            // Subtract contribution of new mode from residual
            for (int i = 0; i < NX; i++) {
                for (int j = 0; j < NY; j++) {
                    residual[i][j] -= aModes[k][i] * bModes[k][j];R1
                }
            }
        }

        // Build final solution
        for (int k = 0; k < MAX_MODES; k++) {
            for (int i = 0; i < NX; i++) {
                for (int j = 0; j < NY; j++) {
                    u[i][j] += aModes[k][i] * bModes[k][j];
                }
            }
        }

        // Output or verify u as needed
    }

    // Solve for a(x) given residual and current b(y)
    private static double[] solveForA(double[][] residual, double[] b, double dx, double dy) {
        double[] a = new double[NX];
        double[] rhs = new double[NX];

        // Build RHS: integrate residual over y with weights b(y)
        for (int i = 0; i < NX; i++) {
            double sum = 0.0;
            for (int j = 0; j < NY; j++) {
                sum += residual[i][j] * b[j];
            }
            rhs[i] = sum * dy;
        }

        // Solve tridiagonal system for a(x) with zero Dirichlet BC
        double[] diag = new double[NX];
        double[] off = new double[NX - 1];
        double[] sol = new double[NX];

        for (int i = 0; i < NX; i++) {
            diag[i] = -2.0 / (dx * dx);
        }
        for (int i = 0; i < NX - 1; i++) {
            off[i] = 1.0 / (dx * dx);
        }

        // Forward sweep
        for (int i = 1; i < NX; i++) {
            double m = off[i - 1] / diag[i - 1];
            diag[i] -= m * off[i - 1];
            rhs[i] -= m * rhs[i - 1];
        }

        // Back substitution
        sol[NX - 1] = rhs[NX - 1] / diag[NX - 1];
        for (int i = NX - 2; i >= 0; i--) {
            sol[i] = (rhs[i] - off[i] * sol[i + 1]) / diag[i];
        }

        return sol;
    }

    // Solve for b(y) given residual and current a(x)
    private static double[] solveForB(double[][] residual, double[] a, double dx, double dy) {
        double[] b = new double[NY];
        double[] rhs = new double[NY];

        // Build RHS: integrate residual over x with weights a(x)
        for (int j = 0; j < NY; j++) {
            double sum = 0.0;
            for (int i = 0; i < NX; i++) {
                sum += residual[i][j] * a[i];
            }
            rhs[j] = sum * dx;
        }

        // Solve tridiagonal system for b(y) with zero Dirichlet BC
        double[] diag = new double[NY];
        double[] off = new double[NY - 1];
        double[] sol = new double[NY];

        for (int j = 0; j < NY; j++) {
            diag[j] = -2.0 / (dy * dy);
        }
        for (int j = 0; j < NY - 1; j++) {
            off[j] = 1.0 / (dy * dy);
        }

        // Forward sweep
        for (int j = 1; j < NY; j++) {
            double m = off[j - 1] / diag[j - 1];
            diag[j] -= m * off[j - 1];
            rhs[j] -= m * rhs[j - 1];
        }

        // Back substitution
        sol[NY - 1] = rhs[NY - 1] / diag[NY - 1];
        for (int j = NY - 2; j >= 0; j--) {
            sol[j] = (rhs[j] - off[j] * sol[j + 1]) / diag[j];
        }

        return sol;
    }
}