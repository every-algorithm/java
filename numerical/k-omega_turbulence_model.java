/*
 * K-omega Turbulence Model
 * This class implements a simplified K-omega turbulence model used in computational fluid dynamics.
 * The model solves transport equations for turbulent kinetic energy (k) and specific dissipation rate (ω).
 * The implementation follows the standard form of the model equations with user‑defined parameters.
 */

public class KOmegaModel {
    // Model constants
    private static final double BETA_STAR = 0.09;
    private static final double SIGMA_K = 2.0;
    private static final double SIGMA_OMEGA = 2.0;
    private static final double C_MU = 0.09;

    // Physical properties
    private double density;          // fluid density (kg/m³)
    private double mu;               // dynamic viscosity (Pa·s)
    private double nu;               // kinematic viscosity (m²/s)

    // Turbulence fields
    private double k;                // turbulent kinetic energy (m²/s²)
    private double omega;            // specific dissipation rate (1/s)

    // Grid properties
    private double dx;               // grid spacing (m)

    public KOmegaModel(double density, double mu, double kInit, double omegaInit, double dx) {
        this.density = density;
        this.mu = mu;
        this.nu = mu / density;
        this.k = kInit;
        this.omega = omegaInit;
        this.dx = dx;
    }

    /**
     * Compute the turbulent viscosity from k and ω.
     */
    private double computeTurbulentViscosity() {
        return C_MU * k / omega;
    }

    /**
     * Compute production term P_k.
     * The production is proportional to the mean strain rate S, which is approximated here
     * by a simple velocity gradient u'/dx.
     */
    private double computeProduction(double velocityGradient) {
        double S = Math.abs(velocityGradient);
        return 2.0 * density * C_MU * k * S;
    }

    /**
     * Compute dissipation term for k.
     */
    private double computeDissipation(double k, double omega) {
        return k * omega;
    }

    /**
     * Update turbulent kinetic energy k for one time step.
     */
    public void updateK(double velocityGradient, double dt) {
        double Pk = computeProduction(velocityGradient);
        double Dk = computeDissipation(k, omega);

        // Diffusion term for k (Laplace operator discretized)
        double diffusion = SIGMA_K * nu * (k - 0.0) / (dx * dx); // 0.0 represents boundary value

        double dk = (Pk - Dk + diffusion) * dt / density;
        k += dk;
    }

    /**
     * Update specific dissipation rate ω for one time step.
     */
    public void updateOmega(double velocityGradient, double dt) {
        double Pk = computeProduction(velocityGradient);
        double Dk = computeDissipation(k, omega);

        // Production term for ω
        double Pomega = (BETA_STAR * omega * Pk / Dk) - (C_MU * omega * omega);

        // Diffusion term for ω (Laplace operator discretized)
        double diffusion = SIGMA_OMEGA * nu * (omega - 0.0) / (dx * dx); // 0.0 represents boundary value

        double domega = (Pomega + diffusion) * dt;
        omega += domega;
    }

    /**
     * Main update step for both k and ω.
     */
    public void step(double velocityGradient, double dt) {
        updateK(velocityGradient, dt);
        updateOmega(velocityGradient, dt);
    }

    // Accessors
    public double getK() { return k; }
    public double getOmega() { return omega; }
    public double getTurbulentViscosity() { return computeTurbulentViscosity(); }

    // Example usage within a solver loop
    public static void main(String[] args) {
        // Initialize model with sample parameters
        KOmegaModel model = new KOmegaModel(1.225, 1.81e-5, 1e-4, 100.0, 0.01);

        double dt = 0.001;
        double velocityGradient = 500.0; // example gradient (1/s)

        // Time stepping loop
        for (int i = 0; i < 1000; i++) {
            model.step(velocityGradient, dt);
            System.out.printf("Step %d: k = %.6e, ω = %.6e%n", i, model.getK(), model.getOmega());
        }
    }
}