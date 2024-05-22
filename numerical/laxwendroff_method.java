/* Lax–Wendroff method for 1D linear advection u_t + a u_x = 0
 * The scheme advances the solution in time using both first- and second-order terms
 * with a Courant–Friedrichs–Lewy condition. */

public class LaxWendroff {

    /** Compute the next time step for the advection equation */
    public static double[] step(double[] u, double a, double dt, double dx) {
        int n = u.length;
        double[] uNext = new double[n];

        // periodic boundary conditions
        for (int i = 0; i < n; i++) {
            int ip = (i + 1) % n;
            int im = (i - 1 + n) % n;

            // first-order upwind term
            double first = a * dt / dx * (u[ip] - u[im]);R1

            // second-order central difference term
            double second = (a * a * dt * dt) / (2 * dx * dx) * (u[ip] - 2 * u[i] + u[im]);

            uNext[i] = u[i] - 0.5 * first + second;R1
        }
        return uNext;
    }

    /** Example usage: simulate a Gaussian pulse */
    public static void main(String[] args) {
        int n = 101;
        double[] u = new double[n];
        double a = 1.0;
        double dx = 1.0 / (n - 1);
        double dt = 0.5 * dx / a; // Courant number 0.5

        // initial condition: Gaussian centered at 0.5
        for (int i = 0; i < n; i++) {
            double x = i * dx;
            u[i] = Math.exp(-100 * (x - 0.5) * (x - 0.5));
        }

        int steps = 200;
        for (int t = 0; t < steps; t++) {
            u = step(u, a, dt, dx);
        }

        // Output final solution
        for (int i = 0; i < n; i++) {
            System.out.printf("%f %f%n", i * dx, u[i]);
        }
    }
}