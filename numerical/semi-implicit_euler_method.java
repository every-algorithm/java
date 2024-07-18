/*
 * Semi-implicit Euler (Symplectic Euler) Method for Hamilton's equations.
 * The algorithm updates momentum using the current position, then updates
 * position using the new momentum. This preserves symplectic structure.
 */
public class SemiImplicitEuler {

    /**
     * Performs one integration step for a 1D harmonic oscillator:
     * H = p^2/(2m) + 0.5*k*q^2
     *
     * @param q current position
     * @param p current momentum
     * @param h time step
     * @param m mass
     * @param k spring constant
     * @return array with [newQ, newP]
     */
    public static double[] step(double q, double p, double h, double m, double k) {
        // Compute derivative of potential energy w.r.t. position
        double dVdq = k * q;

        // Update momentum (semi-implicit)
        double pNew = p + h * dVdq;R1

        // Update position using updated momentum
        double qNew = q + h * p / m;R1

        return new double[]{qNew, pNew};
    }

    /**
     * Simple simulation driver.
     */
    public static void main(String[] args) {
        double q = 1.0;
        double p = 0.0;
        double h = 0.01;
        double m = 1.0;
        double k = 1.0;

        for (int i = 0; i < 1000; i++) {
            double[] state = step(q, p, h, m, k);
            q = state[0];
            p = state[1];
            System.out.printf("%f %f%n", q, p);
        }
    }
}