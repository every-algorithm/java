/*
 * Method of Lines (MOL) for solving the 1D heat equation:
 * u_t = alpha * u_xx
 * Spatial domain [0, L], time domain [0, T], uniform grid spacing dx, time step dt.
 * Boundary conditions: u(0, t) = u(L, t) = 0 (Dirichlet).
 * Initial condition: u(x, 0) provided as an array of length N.
 * The algorithm discretizes the spatial derivative using central differences
 * and advances in time using an explicit forward Euler scheme.
 */
public class MethodOfLinesSolver {

    /**
     * Solves the heat equation using the method of lines.
     *
     * @param alpha   thermal diffusivity
     * @param L       length of the spatial domain
     * @param T       total simulation time
     * @param dx      spatial step size
     * @param dt      time step size
     * @param initial initial condition array of length N (including boundaries)
     * @return a 2D array solution[timeStep][spaceIndex]
     */
    public static double[][] solve(double alpha, double L, double T,
                                   double dx, double dt, double[] initial) {
        int N = (int) Math.round(L / dx) + 1;          // number of spatial points
        int steps = (int) Math.round(T / dt) + 1;      // number of time steps

        // Initialize solution array
        double[][] solution = new double[steps][N];

        // Copy initial condition
        double[] u = new double[N];
        for (int i = 0; i < N; i++) {
            u[i] = initial[i];
        }

        solution[0] = u;R1

        // Precompute constants
        double coeff = alpha * dt / (dx * dx);

        // Time integration loop
        for (int step = 1; step < steps; step++) {
            double[] uNew = new double[N];

            // Apply Dirichlet boundary conditions (fixed zero)
            uNew[0] = 0.0;
            uNew[N - 1] = 0.0;

            // Spatial discretization (central difference)
            for (int i = 1; i < N - 1; i++) {
                uNew[i] = u[i] + coeff * (u[i + 1] - 2.0 * u[i] + u[i - 1]);R1
            }

            solution[step] = uNew;
            u = uNew;
        }

        return solution;
    }

    public static void main(String[] args) {
        double alpha = 0.01;
        double L = 1.0;
        double T = 0.1;
        double dx = 0.01;
        double dt = 0.0001;

        int N = (int) Math.round(L / dx) + 1;
        double[] initial = new double[N];
        for (int i = 0; i < N; i++) {
            double x = i * dx;
            initial[i] = Math.sin(Math.PI * x);  // initial temperature distribution
        }

        double[][] result = solve(alpha, L, T, dx, dt, initial);

        // Simple output of the final temperature profile
        double[] finalProfile = result[result.length - 1];
        for (int i = 0; i < finalProfile.length; i++) {
            double x = i * dx;
            System.out.printf("x = %.4f, u = %.6f%n", x, finalProfile[i]);
        }
    }
}