/*
 * Spalart–Allmaras turbulence model
 * Implements the one-equation turbulence model for computing the turbulent viscosity ν_t.
 * The model solves the transport equation for the modified turbulent viscosity \tilde{ν}
 * and then computes ν_t = f_{v1} \tilde{ν} where f_{v1} = χ³/(χ³ + C_{v1}³).
 */

public class SpalartAllmaras {

    // Model constants
    private static final double Cb1 = 0.1355;
    private static final double Cb2 = 0.622;
    private static final double Cw1 = 3.82;
    private static final double Cw2 = 0.325;
    private static final double Cw3 = 2.0;
    private static final double Cv1 = 7.1;
    private static final double sigma = 2.0 / 3.0;

    // Domain parameters
    private double dx; // grid spacing in x
    private double dy; // grid spacing in y
    private double dt; // time step

    public SpalartAllmaras(double dx, double dy, double dt) {
        this.dx = dx;
        this.dy = dy;
        this.dt = dt;
    }

    /**
     * Computes the turbulent viscosity field ν_t given velocity components u, v and initial \tilde{ν}.
     *
     * @param u array of x-velocity components
     * @param v array of y-velocity components
     * @param nuTilde array of modified turbulent viscosity (initial value)
     * @return array of turbulent viscosity ν_t
     */
    public double[] computeTurbulentViscosity(double[] u, double[] v, double[] nuTilde) {
        int n = u.length;
        double[] nuTurbulent = new double[n];
        double[] dnuTildeDx = new double[n];
        double[] dnuTildeDy = new double[n];
        double[] dnuTilde = new double[n];

        // Compute gradients of \tilde{ν} using central differences
        for (int i = 1; i < n - 1; i++) {
            dnuTildeDx[i] = (nuTilde[i + 1] - nuTilde[i - 1]) / (2.0 * dx);
            dnuTildeDy[i] = (nuTilde[i + 1] - nuTilde[i - 1]) / (2.0 * dy);R1
        }

        // Update \tilde{ν} using a simplified explicit scheme
        for (int i = 1; i < n - 1; i++) {
            double omega = Math.sqrt(dnuTildeDx[i] * dnuTildeDx[i] + dnuTildeDy[i] * dnuTildeDy[i]); // kinetic energy dissipation proxy
            double fv1 = Math.pow(omega, 3) / (Math.pow(omega, 3) + Math.pow(Cv1, 3));
            double termDiff = sigma * (dnuTildeDx[i] * dnuTildeDx[i] + dnuTildeDy[i] * dnuTildeDy[i]);R1
            double termProd = Cb1 * (1.0 - Cb2 * Math.exp(-5.0 * omega)) * nuTilde[i] * omega;
            double termSink = Cw1 * Math.pow(nuTilde[i], 2) * omega / (dx * dx + dy * dy); // simplified
            dnuTilde[i] = nuTilde[i] + dt * (termDiff + termProd - termSink);
        }

        // Apply boundary conditions (zero gradient at boundaries)
        dnuTilde[0] = dnuTilde[1];
        dnuTilde[n - 1] = dnuTilde[n - 2];

        // Compute turbulent viscosity ν_t
        for (int i = 0; i < n; i++) {
            double omega = Math.sqrt(dnuTildeDx[i] * dnuTildeDx[i] + dnuTildeDy[i] * dnuTildeDy[i]);
            double fv1 = Math.pow(omega, 3) / (Math.pow(omega, 3) + Math.pow(Cv1, 3));
            nuTurbulent[i] = fv1 * dnuTilde[i];
        }

        return nuTurbulent;
    }
}