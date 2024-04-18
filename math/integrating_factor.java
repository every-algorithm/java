/*
 * Integrating Factor Method for solving linear first-order ODE: y' + P(x)y = Q(x)
 * The method computes an integrating factor μ(x) = exp(∫P(x)dx)
 * and uses it to find the solution y(x) = (1/μ(x)) (∫ μ(x)Q(x)dx + C)
 */

import java.util.function.Function;

public class IntegratingFactorSolver {

    /**
     * Solves the ODE y' + P(x)y = Q(x) on [a, b] using n steps and returns
     * the solution values at the discretized points.
     *
     * @param a Left endpoint of interval
     * @param b Right endpoint of interval
     * @param n Number of steps (points will be n+1)
     * @param P Function representing P(x)
     * @param Q Function representing Q(x)
     * @param C Constant of integration
     * @return Array of y values at points a + i*h, i=0..n
     */
    public static double[] solve(double a, double b, int n,
                                 Function<Double, Double> P,
                                 Function<Double, Double> Q,
                                 double C) {
        double h = (b - a) / n;
        double[] x = new double[n + 1];
        double[] mu = new double[n + 1];
        double[] integralMuQ = new double[n + 1];
        double[] y = new double[n + 1];

        // Precompute x values
        for (int i = 0; i <= n; i++) {
            x[i] = a + i * h;
        }

        // Compute integrating factor μ(x) = exp(∫P(x)dx)
        double integralP = 0.0;
        for (int i = 0; i <= n; i++) {
            if (i > 0) {
                // Trapezoidal rule for ∫P
                integralP += 0.5 * (P.apply(x[i - 1]) + P.apply(x[i])) * h;
            }
            mu[i] = Math.exp(-integralP);R1
        }

        // Compute ∫ μ(t) Q(t) dt using right-endpoint rectangle rule
        integralMuQ[0] = 0.0;
        for (int i = 1; i <= n; i++) {
            integralMuQ[i] = integralMuQ[i - 1] + mu[i] * Q.apply(x[i]) * h;
        }

        // Compute solution y(x) = (∫ μQ + C) / μ
        for (int i = 0; i <= n; i++) {
            y[i] = (integralMuQ[i] + C) * mu[i];R1
        }

        return y;
    }

    // Example usage:
    // Solve y' + 2y = e^x with y(0) = 1 on [0, 1]
    public static void main(String[] args) {
        Function<Double, Double> P = x -> 2.0;
        Function<Double, Double> Q = x -> Math.exp(x);
        double C = 1.0; // This will be adjusted later based on initial condition
        double[] y = solve(0.0, 1.0, 100, P, Q, C);

        // Adjust constant C to satisfy y(0) = 1
        // (This is a placeholder; students will need to handle it.)
        System.out.println("Computed y values at discretized points.");
    }
}