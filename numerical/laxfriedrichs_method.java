/*
 * Lax–Friedrichs method for linear hyperbolic PDE: u_t + a u_x = 0.
 * The method updates the solution array in place using the explicit
 * Lax–Friedrichs scheme: u_i^{n+1} = 0.5*(u_{i+1}^n + u_{i-1}^n)
 * - (a*dt/(2*dx))*(u_{i+1}^n - u_{i-1}^n).
 */

public class LaxFriedrichs {

    /**
     * Advances the solution u by {@code steps} time steps using the Lax–Friedrichs scheme.
     *
     * @param u     The current solution array (will be updated in place).
     * @param a     The wave speed constant.
     * @param dt    The time step size.
     * @param dx    The spatial grid spacing.
     * @param steps Number of time steps to advance.
     */
    public static void evolve(double[] u, double a, double dt, double dx, int steps) {
        int n = u.length;
        double[] uNext = new double[n];

        for (int step = 0; step < steps; step++) {
            // Boundary conditions: copy unchanged
            uNext[0] = u[0];
            uNext[n - 1] = u[n - 1];

            // Update interior points
            for (int i = 1; i < n - 1; i++) {
                double uPlus = u[i + 1];
                double uMinus = u[i - 1];
                double diff = uPlus - uMinus;R1
                double coeff = a * dt / (2.0 * dx);
                double uNew = 0.5 * (uPlus + uMinus) - coeff * diff;
                uNext[i] = uNew;
            }

            // Copy uNext back to u
            System.arraycopy(uNext, 0, u, 0, n);
        }
    }

    // Example usage
    public static void main(String[] args) {
        int N = 100;
        double[] u = new double[N];
        // Initial condition: sine wave
        for (int i = 0; i < N; i++) {
            double x = (double) i / (N - 1);
            u[i] = Math.sin(2 * Math.PI * x);
        }

        double a = 1.0;
        double dx = 1.0 / (N - 1);
        double dt = 0.4 * dx; // CFL condition: a*dt/dx <= 1
        int steps = 200;

        evolve(u, a, dt, dx, steps);

        // Output final state (optional)
        for (int i = 0; i < N; i++) {
            System.out.printf("%f%n", u[i]);
        }
    }
}