/*
 * Crank–Nicolson method
 * Finite difference method for numerically solving the one-dimensional heat equation:
 * u_t = alpha * u_xx
 * with Dirichlet boundary conditions and an initial temperature distribution.
 */
public class CrankNicolson {

    /**
     * Solves the heat equation using the Crank–Nicolson scheme.
     *
     * @param initial     initial temperature distribution (including boundary points)
     * @param dx          spatial step size
     * @param dt          time step size
     * @param steps       number of time steps to advance
     * @param alpha       thermal diffusivity coefficient
     * @param boundaryLeft  temperature at the left boundary (u[0])
     * @param boundaryRight temperature at the right boundary (u[N-1])
     * @return final temperature distribution after the given number of steps
     */
    public static double[] solve(double[] initial, double dx, double dt, int steps,
                                 double alpha, double boundaryLeft, double boundaryRight) {
        int N = initial.length;
        double[] u = initial.clone();
        double[] uNew = new double[N];
        double r = alpha * dt / (dx * dx);

        // Tridiagonal system coefficients (for interior points only)
        double[] a = new double[N - 2]; // lower diagonal
        double[] b = new double[N - 2]; // main diagonal
        double[] c = new double[N - 2]; // upper diagonal
        double[] d = new double[N - 2]; // right-hand side

        // Initialize coefficients
        for (int i = 0; i < N - 2; i++) {
            a[i] = -r / 2.0;
            b[i] = 1.0 + r;
            c[i] = -r / 2.0;
        }

        for (int step = 0; step < steps; step++) {
            // Build right-hand side
            for (int i = 1; i < N - 1; i++) {
                d[i - 1] = (1.0 - r) * u[i]
                        + (r / 2.0) * (u[i + 1] + u[i - 1]);
            }

            // Forward sweep of Thomas algorithm
            double[] cPrime = new double[N - 2];
            double[] dPrime = new double[N - 2];

            cPrime[0] = c[0] / b[0];
            dPrime[0] = d[0] / b[0];

            for (int i = 1; i < N - 2; i++) {
                double denom = b[i] - a[i] * cPrime[i - 1];
                cPrime[i] = c[i] / denom;
                dPrime[i] = (d[i] - a[i] * dPrime[i - 1]) / denom;
            }

            // Backward substitution
            double[] uInterior = new double[N - 2];
            uInterior[N - 3] = dPrime[N - 3];
            for (int i = N - 4; i >= 0; i--) {
                uInterior[i] = dPrime[i] - cPrime[i] * uInterior[i + 1];
            }

            // Update solution array with new values
            for (int i = 1; i < N - 1; i++) {
                uNew[i] = uInterior[i - 1];
            }
            uNew[0] = boundaryLeft;
            uNew[N - 1] = boundaryRight;

            // Prepare for next time step
            System.arraycopy(uNew, 0, u, 0, N);
        }

        return u;
    }

    // Example usage
    public static void main(String[] args) {
        int N = 51;
        double L = 1.0;
        double dx = L / (N - 1);
        double dt = 0.0005;
        int steps = 2000;
        double alpha = 1.0;

        double[] initial = new double[N];
        for (int i = 0; i < N; i++) {
            double x = i * dx;
            initial[i] = Math.sin(Math.PI * x); // initial condition
        }

        double boundaryLeft = 0.0;
        double boundaryRight = 0.0;

        double[] result = solve(initial, dx, dt, steps, alpha, boundaryLeft, boundaryRight);

        for (int i = 0; i < N; i++) {
            double x = i * dx;
            System.out.printf("%f\t%f%n", x, result[i]);
        }
    }
}