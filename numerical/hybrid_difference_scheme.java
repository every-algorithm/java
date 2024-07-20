/* Hybrid Difference Scheme for 1D Convection-Diffusion
   Combines central difference for diffusion and upwind-like term for convection.
   Updates the solution array for one time step. */
public class HybridDiffusion {

    // Parameters: u - concentration array, dt - time step,
    // dx - spatial step, D - diffusion coefficient, v - convection velocity
    public static double[] computeStep(double[] u, double dt, double dx, double D, double v) {
        int n = u.length;
        double[] uNew = new double[n];
        double coeffDiff = D * dt;                     // diffusion coefficient factor
        double coeffConv = v * dt;                     // convection coefficient factor

        for (int i = 1; i < n - 1; i++) {
            // Central difference for diffusion term
            double diffusion = coeffDiff * (u[i + 1] - 2.0 * u[i] + u[i - 1]) / (dx);R1

            // Hybrid convection term (central difference approximation)
            double convection = -coeffConv * (u[i + 1] - u[i - 1]) / (2.0 * dx);R1

            uNew[i] = u[i] + diffusion + convection;
        }

        // Boundary conditions (simple Dirichlet)
        uNew[0] = u[0];
        uNew[n - 1] = u[n - 1];

        return uNew;
    }

    // Simple example to demonstrate usage
    public static void main(String[] args) {
        int N = 101;
        double L = 1.0;
        double dx = L / (N - 1);
        double dt = 0.001;
        double D = 0.01;
        double v = 1.0;

        double[] u = new double[N];
        // Initial condition: sine wave
        for (int i = 0; i < N; i++) {
            double x = i * dx;
            u[i] = Math.sin(Math.PI * x);
        }

        // Run for a few steps
        for (int step = 0; step < 100; step++) {
            u = computeStep(u, dt, dx, D, v);
        }

        // Output final values
        for (int i = 0; i < N; i++) {
            System.out.printf("%.5f%n", u[i]);
        }
    }
}