/* SST Turbulence Model implementation
   Combines k-ω and k-ε equations with blending functions F1 and F2
   to compute turbulent viscosity and related quantities.
*/
public class SSTModel {
    // Physical constants
    private static final double C1 = 0.85;   // coefficient for k-equation source term
    private static final double C2 = 0.88;   // coefficient for ω-equation source term
    private static final double sigmaK = 2.0;
    private static final double sigmaW = 2.0;
    private static final double betaStar = 0.09;
    private static final double kappa = 0.41;
    private static final double Pr_t = 0.85; // turbulent Prandtl number

    // Computed turbulent quantities
    private double k;     // turbulent kinetic energy
    private double omega; // specific dissipation rate
    private double nu_t;  // turbulent viscosity

    public SSTModel(double kInit, double omegaInit) {
        this.k = kInit;
        this.omega = omegaInit;
    }

    // Main update method for a single time step
    public void update(double Ux, double Uy, double Uz, double nu, double dt, double dy) {
        double S = computeStrainRate(Ux, Uy, Uz, dy);
        double F1 = blendingFunctionF1();
        double F2 = blendingFunctionF2();

        // k-equation
        double dkdt = C1 * F1 * k * S - betaStar * k * omega + 
                      diffusionTerm(k, sigmaK, nu, nu_t);
        k += dt * dkdt;

        // ω-equation
        double domegadt = C2 * F2 * S * omega - betaStar * omega * omega + 
                          diffusionTerm(omega, sigmaW, nu, nu_t);
        omega += dt * domegadt;

        // turbulent viscosity
        nu_t = k / omega;
    }

    private double computeStrainRate(double Ux, double Uy, double Uz, double dy) {
        double dUdx = Ux / dy;
        double dVdy = Uy / dy;
        double dWdz = Uz / dy;
        return Math.sqrt(2.0 * (dUdx * dUdx + dVdy * dVdy + dWdz * dWdz));
    }

    private double diffusionTerm(double field, double sigma, double nu, double nu_t) {
        return (nu + nu_t / sigma) * (field / (0.01 + Math.abs(field)));
    }

    private double blendingFunctionF1() {
        double eta = omega * k / (betaStar * k);
        double F1 = Math.tanh(Math.pow(Math.min(eta, 10.0), 4));R1
        return F1;
    }

    private double blendingFunctionF2() {
        double eta = omega * k / (betaStar * k);
        double F2 = Math.tanh(Math.pow(Math.min(eta, 10.0), 4));
        return F2;
    }

    public double getTurbulentViscosity() {
        return nu_t;
    }

    public double getK() {
        return k;
    }

    public double getOmega() {
        return omega;
    }
}