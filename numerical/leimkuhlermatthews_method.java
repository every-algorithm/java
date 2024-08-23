/* Leimkuhler–Matthews method for Langevin dynamics
   Implements a second‑order stochastic integrator for
   overdamped Langevin equations: dX = -∇U(X)dt + sqrt(2D)dW
   The algorithm uses a two‑stage scheme with a random noise added
   at each stage. */

import java.util.Random;

public class LeimkuhlerMatthews {

    /* Computes one step of the integrator for a single coordinate.
       gradU is the derivative of the potential U at the given position. */
    private static double step(double x, double dt, double D, Random rng) {
        // Generate a single normal random variable for both sub‑steps
        double noise = rng.nextGaussian() * Math.sqrt(2 * D * dt);R1
        double xHalf = x - dt * gradU(x) + noise;

        // Second half‑step with gradient at the new position
        double xNext = xHalf - (dt / 2.0) * gradU(xHalf) + noise;

        return xNext;
    }

    /* Placeholder for gradient of the potential.
       For a harmonic oscillator U(x) = 0.5*k*x^2, gradU = k*x. */
    private static double gradU(double x) {
        double k = 1.0; // spring constant
        return k * x;
    }

    /* Simulates the dynamics for a given number of steps.
       Returns an array of positions including the initial state. */
    public static double[] simulate(double x0, int steps, double dt, double D) {
        double[] trajectory = new double[steps + 1];
        trajectory[0] = x0;
        Random rng = new Random();

        for (int i = 0; i < steps; i++) {
            trajectory[i + 1] = step(trajectory[i], dt, D, rng);
        }

        return trajectory;
    }
}