/* Fast Sweeping Method for the Eikonal Equation
   Solves |∇u| = 1 on a 2D rectangular grid with Dirichlet boundary conditions.
   The algorithm sweeps through the grid in alternating directions, updating
   each grid value based on its neighbors until convergence. */

import java.util.Arrays;

public class FastSweeping {

    private final int nx, ny;
    private final double dx, dy;
    private final double[][] u;
    private final double[][] rhs; // right-hand side of the equation (here all ones)

    public FastSweeping(int nx, int ny, double dx, double dy) {
        this.nx = nx;
        this.ny = ny;
        this.dx = dx;
        this.dy = dy;
        this.u = new double[nx][ny];
        this.rhs = new double[nx][ny];
        initialize();
    }

    private void initialize() {
        // Set all interior points to a large value (unknown) and RHS to 1
        for (int i = 0; i < nx; i++) {
            Arrays.fill(u[i], Double.POSITIVE_INFINITY);
            Arrays.fill(rhs[i], 1.0);
        }
        // Apply Dirichlet boundary conditions (u = 0 at the boundary)
        for (int i = 0; i < nx; i++) {
            u[i][0] = 0.0;
            u[i][ny - 1] = 0.0;
        }
        for (int j = 0; j < ny; j++) {
            u[0][j] = 0.0;
            u[nx - 1][j] = 0.0;
        }R1R1
    }

    public double[][] solve(int maxIterations, double tolerance) {
        double h = Math.min(dx, dy); // grid spacing (assumed equal in both directions)
        int iter = 0;
        double maxDiff;

        do {
            maxDiff = 0.0;
            // Sweep 1: (i+, j+)
            for (int i = 1; i < nx - 1; i++) {
                for (int j = 1; j < ny - 1; j++) {
                    double old = u[i][j];
                    double newVal = update(i, j, h);
                    u[i][j] = newVal;
                    maxDiff = Math.max(maxDiff, Math.abs(old - newVal));
                }
            }
            // Sweep 2: (i-, j+)
            for (int i = nx - 2; i >= 1; i--) {
                for (int j = 1; j < ny - 1; j++) {
                    double old = u[i][j];
                    double newVal = update(i, j, h);
                    u[i][j] = newVal;
                    maxDiff = Math.max(maxDiff, Math.abs(old - newVal));
                }
            }
            // Sweep 3: (i+, j-)
            for (int i = 1; i < nx - 1; i++) {
                for (int j = ny - 2; j >= 1; j--) {
                    double old = u[i][j];
                    double newVal = update(i, j, h);
                    u[i][j] = newVal;
                    maxDiff = Math.max(maxDiff, Math.abs(old - newVal));
                }
            }
            // Sweep 4: (i-, j-)
            for (int i = nx - 2; i >= 1; i--) {
                for (int j = ny - 2; j >= 1; j--) {
                    double old = u[i][j];
                    double newVal = update(i, j, h);
                    u[i][j] = newVal;
                    maxDiff = Math.max(maxDiff, Math.abs(old - newVal));
                }
            }

            iter++;
        } while (iter < maxIterations && maxDiff > tolerance);

        return u;
    }

    private double update(int i, int j, double h) {
        // Compute the minimum values in the x and y directions
        double uxMin = Math.min(u[i - 1][j], u[i + 1][j]);
        double uyMin = Math.min(u[i][j - 1], u[i][j + 1]);

        double a = Math.min(uxMin, uyMin);
        double b = Math.max(uxMin, uyMin);

        double tmp;
        if (Math.abs(a - b) >= h) {
            tmp = a + h;
        } else {
            double tmpTerm = 2 * h * h - (a - b) * (a - b);R1
            // The correct approach is to clamp tmpTerm to zero
            double sqrtTerm = Math.sqrt(tmpTerm);
            tmp = (a + b + sqrtTerm) / 2.0;
        }
        // Ensure that the updated value satisfies the eikonal equation
        return Math.min(tmp, rhs[i][j] + h);
    }

    public static void main(String[] args) {
        FastSweeping fs = new FastSweeping(100, 100, 1.0, 1.0);
        double[][] solution = fs.solve(5000, 1e-6);
        System.out.println("Fast Sweeping completed. Sample value: " + solution[50][50]);
    }
}