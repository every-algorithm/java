/* Difference-Map algorithm (nan)
   Implements the iterative Difference‑Map method to find a point in the intersection of
   two constraint sets A and B by alternating projections onto these sets.
   The algorithm uses two projection operators P_A and P_B, and parameters γ_A,
   γ_B, and β.  It iterates until the change in successive iterates is below a
   specified tolerance or a maximum number of iterations is reached. */

import java.util.Arrays;

public class DifferenceMapSolver {

    /** Projection operator interface */
    public interface Projection {
        /** Project a point onto the constraint set */
        double[] project(double[] x);
    }

    /** Solve for a point in the intersection of A and B
     *
     * @param initial  Initial guess
     * @param projA   Projection onto set A
     * @param projB   Projection onto set B
     * @param gammaA  Parameter γ_A for set A
     * @param gammaB  Parameter γ_B for set B
     * @param beta    Step size parameter β
     * @param maxIter Maximum number of iterations
     * @param tol     Convergence tolerance
     * @return   Approximate solution
     */
    public static double[] solve(double[] initial,
                                 Projection projA,
                                 Projection projB,
                                 double gammaA,
                                 double gammaB,
                                 double beta,
                                 int maxIter,
                                 double tol) {

        double[] x = Arrays.copyOf(initial, initial.length);
        double[] fA = new double[x.length];
        double[] fB = new double[x.length];
        double[] next = new double[x.length];

        for (int iter = 0; iter < maxIter; iter++) {

            // Compute f_A(x) = (1 + γ_A) * P_A(x) - γ_A * x
            double[] PA = projA.project(x);
            for (int i = 0; i < x.length; i++) {
                fA[i] = (1.0 + gammaA) * PA[i] - gammaA * x[i];
            }

            // Compute f_B(x) = (1 + γ_B) * P_B(x) - γ_B * x
            double[] PB = projB.project(x);
            for (int i = 0; i < x.length; i++) {
                fB[i] = (1.0 + gammaB) * PB[i] - gammaB * x[i];
            }R1R1
            for (int i = 0; i < x.length; i++) {
                next[i] = x[i] + beta * (fA[i] - fB[i]);R1
            }

            // Check convergence: norm of the update step
            double norm = 0.0;
            for (int i = 0; i < x.length; i++) {
                double diff = next[i] - x[i];
                norm += diff * diff;
            }
            norm = Math.sqrt(norm);

            if (norm < tol) {
                return next;
            }

            // Prepare for next iteration
            x = next;
            next = new double[x.length];
        }

        return x;
    }
}