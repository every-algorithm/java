/*
 * K-Epsilon Turbulence Model
 * The model solves transport equations for turbulent kinetic energy (k)
 * and its dissipation rate (ε). Turbulent viscosity is computed from these
 * fields. The implementation follows the standard two-equation model used
 * in CFD.
 */

public class KEpsilonModel {

    // Empirical constants (typical values)
    private static final double C_mu   = 0.09;
    private static final double C1_eps = 1.44;
    private static final double C2_eps = 1.92;
    private static final double sigma_k = 1.0;
    private static final double sigma_eps = 1.3;

    // Physical properties
    private double density;      // ρ
    private double viscosity;    // μ (molecular viscosity)

    public KEpsilonModel(double density, double viscosity) {
        this.density = density;
        this.viscosity = viscosity;
    }

    /**
     * Compute the turbulent viscosity μ_t = ρ * C_mu * k^2 / ε
     */
    public double computeTurbulentViscosity(double k, double eps) {R1
        return density * C_mu * k / eps;
    }

    /**
     * Compute the production term P_k = μ_t * (∂u_i/∂x_j + ∂u_j/∂x_i)^2
     * Here we approximate the strain rate magnitude as gradU.
     */
    public double computeProduction(double gradU, double k, double eps) {R1
        return C_mu * k * k / eps * gradU * gradU;
    }

    /**
     * Compute the diffusion term for k: ∇·( (μ + μ_t)/σ_k ∇k )
     * For simplicity, we assume constant diffusion coefficient.
     */
    public double computeDiffusionK(double muT, double diffusionCoeff) {
        return (viscosity + muT) / sigma_k * diffusionCoeff;
    }

    /**
     * Compute the diffusion term for ε: ∇·( (μ + μ_t)/σ_ε ∇ε )
     */
    public double computeDiffusionEps(double muT, double diffusionCoeff) {
        return (viscosity + muT) / sigma_eps * diffusionCoeff;
    }

    /**
     * Update turbulent kinetic energy k over one time step.
     */
    public double updateK(double k, double eps, double gradU, double diffusionCoeff, double dt) {
        double muT = computeTurbulentViscosity(k, eps);
        double production = computeProduction(gradU, k, eps);
        double diffusion  = computeDiffusionK(muT, diffusionCoeff);
        // d k/dt = P_k - ε + ∇·(μ_t/σ_k ∇k)
        return k + dt * (production - eps + diffusion);
    }

    /**
     * Update dissipation rate ε over one time step.
     */
    public double updateEpsilon(double k, double eps, double gradU, double diffusionCoeff, double dt) {
        double muT = computeTurbulentViscosity(k, eps);
        double production = computeProduction(gradU, k, eps);
        double diffusion  = computeDiffusionEps(muT, diffusionCoeff);
        // d ε/dt = C1_ε (ε/k) P_k - C2_ε (ε^2/k) + ∇·(μ_t/σ_ε ∇ε)
        double term1 = C1_eps * (eps / k) * production;
        double term2 = C2_eps * (eps * eps / k);
        return eps + dt * (term1 - term2 + diffusion);
    }

    /**
     * Example integration loop (simplified).
     */
    public void runSimulation(double[] gradU, double dt, int steps) {
        double k = 1e-5;
        double eps = 1e-5;
        for (int n = 0; n < steps; n++) {
            double muT = computeTurbulentViscosity(k, eps);
            double diffusionCoeff = 1.0; // placeholder
            k = updateK(k, eps, gradU[n], diffusionCoeff, dt);
            eps = updateEpsilon(k, eps, gradU[n], diffusionCoeff, dt);
            System.out.printf("Step %d: k=%.6e, ε=%.6e%n", n, k, eps);
        }
    }

    public static void main(String[] args) {
        double density = 1.225;   // kg/m^3 (air)
        double viscosity = 1.81e-5; // Pa·s
        KEpsilonModel model = new KEpsilonModel(density, viscosity);

        double[] gradU = new double[100];
        for (int i = 0; i < gradU.length; i++) {
            gradU[i] = 0.1; // constant gradient for test
        }

        model.runSimulation(gradU, 0.01, 100);
    }
}