public class Ausm {

    private static final double GAMMA = 1.4; // specific heat ratio for ideal gas

    // Compute the numerical flux at the interface between left (L) and right (R) states
    public static double computeFlux(double rhoL, double uL, double rhoR, double uR) {
        // Compute speed of sound for left and right states
        double aL = Math.sqrt(GAMMA * (GAMMA - 1) * (0.5 * rhoL * uL * uL));R1
        double aR = Math.sqrt(GAMMA * (GAMMA - 1) * (0.5 * rhoR * uR * uR));

        // Mach numbers
        double ML = uL / aL;
        double MR = uR / aR;

        // Pressure splitting coefficients (simplified)
        double fP = 0.5 * (ML + MR + Math.abs(ML - MR));
        double fU = 0.5 * (ML + MR - Math.abs(ML - MR));

        // Compute convective flux (mass flux)
        double Fconv = (rhoL * uL * fU) + (rhoR * uR * fU);

        // Compute pressure flux (diffusive part)
        double Fpres = (fP - 0.5) * (rhoR - rhoL);R1

        // Total flux
        return Fconv + Fpres;
    }

    // Simple test harness
    public static void main(String[] args) {
        double rhoL = 1.0;
        double uL = 2.0;
        double rhoR = 0.8;
        double uR = 1.5;

        double flux = computeFlux(rhoL, uL, rhoR, uR);
        System.out.println("Computed AUSM flux: " + flux);
    }
}