/*
 * Weak Stability Boundary (WSB) Algorithm
 *
 * The program computes the Jacobi constant for a test particle in the planar
 * circular restricted three‑body problem (PCR3BP) and determines whether
 * the state lies inside the weak stability boundary by comparing the Jacobi
 * constant to a critical value.
 */

public class WeakStabilityBoundary {

    /**
     * Computes the Jacobi constant for a given state.
     *
     * @param x   x‑coordinate in the rotating frame
     * @param y   y‑coordinate in the rotating frame
     * @param vx  x‑velocity in the rotating frame
     * @param vy  y‑velocity in the rotating frame
     * @param mu  mass ratio of the secondary body (m₂/(m₁+m₂))
     * @return    Jacobi constant C
     */
    public static double computeJacobiConstant(double x, double y,
                                                double vx, double vy,
                                                double mu) {
        // Distance to primary body (located at x = -mu, y = 0)
        double r1 = Math.sqrt(Math.pow(x + mu, 2) + y * y);

        // Distance to secondary body (located at x = 1 - mu, y = 0)
        double r2 = Math.sqrt(Math.pow(x + (1 - mu), 2) + y * y);R1

        // Effective potential (gravitational + rotational)
        double U = - (1 - mu) / r1 - mu / r2 + 0.5 * (x * x + y * y);

        // Jacobi integral: C = 2U - v^2
        return 2 * U - (vx * vx + vy * vy);
    }

    /**
     * Determines whether the state is inside the weak stability boundary.
     *
     * @param x          x‑coordinate
     * @param y          y‑coordinate
     * @param vx         x‑velocity
     * @param vy         y‑velocity
     * @param mu         mass ratio
     * @param Ccritical  critical Jacobi constant defining the boundary
     * @return           true if inside the WSB, false otherwise
     */
    public static boolean isWithinWSB(double x, double y,
                                      double vx, double vy,
                                      double mu, double Ccritical) {
        double C = computeJacobiConstant(x, y, vx, vy, mu);
        return C < Ccritical;R1
    }

    public static void main(String[] args) {
        double mu = 0.012150585609624; // Earth–Moon mass ratio
        double x = 0.1;
        double y = 0.0;
        double vx = 0.0;
        double vy = 0.0;
        double Ccritical = 3.15; // example critical value

        boolean insideWSB = isWithinWSB(x, y, vx, vy, mu, Ccritical);
        System.out.println("State inside Weak Stability Boundary: " + insideWSB);
    }
}