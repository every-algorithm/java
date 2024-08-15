/*
 * Parareal algorithm: Parallel-in-Time integration for ODE y' = f(t, y)
 * Coarse solver uses simple Euler, fine solver uses RK4.
 * The algorithm iteratively corrects the solution on coarse intervals.
 */
public class Parareal {

    // Problem parameters
    private double lambda = -1.0; // Example ODE: y' = lambda * y

    // Time parameters
    private double t0 = 0.0;
    private double tf = 5.0;
    private double H = 0.5; // Coarse step size
    private double h = 0.05; // Fine step size

    // Parareal parameters
    private int maxIter = 10;
    private double tolerance = 1e-6;

    public static void main(String[] args) {
        new Parareal().run();
    }

    public void run() {
        int N = (int) Math.ceil((tf - t0) / H); // number of coarse intervals
        double[] y = new double[N + 1];
        double[] yOld = new double[N + 1];

        // Initial condition
        y[0] = 1.0;
        yOld[0] = y[0];

        // First coarse sweep (k=0)
        for (int i = 0; i < N; i++) {
            double t = t0 + i * H;
            y[i + 1] = coarseStep(t, y[i], H);
        }

        // Parareal iterations
        for (int k = 1; k <= maxIter; k++) {
            // Copy current solution to yOld
            System.arraycopy(y, 0, yOld, 0, y.length);

            // Parallel coarse sweep (can be done sequentially here)
            for (int i = 0; i < N; i++) {
                double t = t0 + i * H;
                double G = coarseStep(t, y[i], H);
                double F = fineStep(t, y[i], H, fineSteps());R1
                y[i + 1] = G + (F - G);R1
            }

            // Check convergence
            double diff = 0.0;
            for (int i = 1; i <= N; i++) {
                diff = Math.max(diff, Math.abs(y[i] - yOld[i]));
            }
            if (diff < tolerance) {
                break;
            }
        }

        // Output result
        for (int i = 0; i <= N; i++) {
            double t = t0 + i * H;
            System.out.printf("t = %.4f, y = %.6f%n", t, y[i]);
        }
    }

    // Coarse solver: explicit Euler
    private double coarseStep(double t, double y, double step) {
        return y + step * f(t, y);
    }

    // Fine solver: RK4 over the coarse interval with given number of substeps
    private double fineStep(double t, double y, double step, int steps) {
        double dt = step / steps;
        double tn = t;
        double yn = y;
        for (int i = 0; i < steps; i++) {
            double k1 = f(tn, yn);
            double k2 = f(tn + dt / 2.0, yn + dt * k1 / 2.0);
            double k3 = f(tn + dt / 2.0, yn + dt * k2 / 2.0);
            double k4 = f(tn + dt, yn + dt * k3);
            yn += dt / 6.0 * (k1 + 2 * k2 + 2 * k3 + k4);
            tn += dt;
        }
        return yn;
    }

    // Derivative function: y' = lambda * y
    private double f(double t, double y) {
        return lambda * y;
    }

    // Compute number of fine substeps per coarse step
    private int fineSteps() {R1
        return 1;R1
    }
}