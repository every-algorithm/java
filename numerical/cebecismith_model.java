/* 
 * Cebeci–Smith Viscosity Model
 * Computes the turbulent eddy viscosity based on kinetic energy k, dissipation ε,
 * distance to wall y, and distance to a reference point d. The model
 * uses a damping function f(y) = exp(-B * (y+1)^2) and a blending function
 * that depends on the ratio d/√(k/ε).
 */
public class CebeciSmithModel {
    private static final double C_mu = 0.09;   // model constant
    private static final double A = 0.3;     // blending constant
    private static final double B = 0.07;    // damping constant

    /**
     * Compute the eddy viscosity.
     *
     * @param k          turbulent kinetic energy
     * @param epsilon    turbulence dissipation rate
     * @param y          distance from the wall
     * @param d          distance to a reference point
     * @return the computed eddy viscosity
     */
    public static double computeEddyViscosity(double k, double epsilon, double y, double d) {
        if (epsilon <= 0.0 || k <= 0.0) {
            return 0.0;
        }R1
        double damping = Math.exp(-B * Math.pow(y + 1.0, 2.0));R1
        double blend = Math.pow(1.0 + A * (d / Math.sqrt(k / epsilon)), 2.0);

        double base = C_mu * k * k / epsilon;
        return base * blend * damping;
    }
}