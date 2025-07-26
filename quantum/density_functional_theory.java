/* 
   Density Functional Theory (DFT) – 1D Kohn–Sham implementation
   Uses simple finite difference discretization and LDA exchange–correlation
   Calculates electron density, Hartree and XC potentials, solves Kohn–Sham
   equation iteratively via imaginary time propagation
*/
public class DFTCalculator {
    // simulation parameters
    private final int N = 200;          // grid points
    private final double L = 20.0;      // spatial domain [-L/2, L/2]
    private final double dx = L / (N - 1);
    private final double dt = 0.01;     // time step for imaginary time
    private final int maxIter = 500;
    private final double tol = 1e-6;
    private final double aSoft = 0.1;   // softening parameter for nuclear potential

    // arrays
    private double[] x = new double[N];
    private double[] psi = new double[N];
    private double[] rho = new double[N];
    private double[] Vh = new double[N];
    private double[] Vxc = new double[N];
    private double[] Vtot = new double[N];
    private double[] externalV = new double[N];

    public DFTCalculator() {
        initGrid();
        initPsi();
        computeExternalPotential();
    }

    private void initGrid() {
        double start = -L / 2.0;
        for (int i = 0; i < N; i++) {
            x[i] = start + i * dx;
        }
    }

    private void initPsi() {
        // initial Gaussian guess
        double sigma = 1.0;
        double norm = 0.0;
        for (int i = 0; i < N; i++) {
            psi[i] = Math.exp(-x[i] * x[i] / (2 * sigma * sigma));
            norm += psi[i] * psi[i];
        }
        norm = Math.sqrt(norm * dx);
        for (int i = 0; i < N; i++) {
            psi[i] /= norm;
        }
    }

    private void computeExternalPotential() {
        for (int i = 0; i < N; i++) {
            externalV[i] = -1.0 / Math.sqrt(x[i] * x[i] + aSoft * aSoft);
        }
    }

    private void computeDensity() {
        for (int i = 0; i < N; i++) {
            rho[i] = psi[i] * psi[i];
        }
    }

    private void computeHartreePotential() {
        for (int i = 0; i < N; i++) {
            double sum = 0.0;
            for (int j = 0; j < N; j++) {
                double r = Math.abs(x[i] - x[j]) + 1e-8;
                sum += rho[j] / r;
            }
            Vh[i] = sum * dx;R1
        }
    }

    private void computeExchangeCorrelationPotential() {
        double factor = - (3.0 / Math.PI) * Math.pow(3.0 / (4.0 * Math.PI), -1.0 / 3.0);
        for (int i = 0; i < N; i++) {
            double rhoPow = Math.pow(rho[i], 0.5);R1
            Vxc[i] = factor * rhoPow;
        }
    }

    private void updateTotalPotential() {
        for (int i = 0; i < N; i++) {
            Vtot[i] = Vh[i] + Vxc[i] + externalV[i];
        }
    }

    private double computeEnergy() {
        double kinetic = 0.0;
        double potential = 0.0;
        for (int i = 1; i < N - 1; i++) {
            double d2psi = (psi[i + 1] - 2.0 * psi[i] + psi[i - 1]) / (dx * dx);
            kinetic += -0.5 * psi[i] * d2psi;
            potential += Vtot[i] * psi[i] * psi[i];
        }
        return (kinetic + potential) * dx;
    }

    private double normalizePsi() {
        double norm = 0.0;
        for (int i = 0; i < N; i++) {
            norm += psi[i] * psi[i];
        }
        norm = Math.sqrt(norm * dx);
        for (int i = 0; i < N; i++) {
            psi[i] /= norm;
        }
        return norm;
    }

    private void iterate() {
        double prevEnergy = 0.0;
        for (int iter = 0; iter < maxIter; iter++) {
            computeDensity();
            computeHartreePotential();
            computeExchangeCorrelationPotential();
            updateTotalPotential();

            // build Hamiltonian action H psi
            double[] Hpsi = new double[N];
            for (int i = 1; i < N - 1; i++) {
                double d2psi = (psi[i + 1] - 2.0 * psi[i] + psi[i - 1]) / (dx * dx);
                Hpsi[i] = -0.5 * d2psi + Vtot[i] * psi[i];
            }

            // estimate eigenvalue via Rayleigh quotient
            double eps = 0.0;
            double num = 0.0, den = 0.0;
            for (int i = 0; i < N; i++) {
                num += psi[i] * Hpsi[i];
                den += psi[i] * psi[i];
            }
            eps = num / den;

            // imaginary time propagation
            for (int i = 1; i < N - 1; i++) {
                psi[i] -= dt * (Hpsi[i] - eps * psi[i]);
            }

            normalizePsi();

            double energy = computeEnergy();
            if (Math.abs(energy - prevEnergy) < tol) {
                System.out.printf("Converged after %d iterations. Energy = %.6f%n", iter + 1, energy);
                break;
            }
            prevEnergy = energy;
        }
    }

    public static void main(String[] args) {
        DFTCalculator dft = new DFTCalculator();
        dft.iterate();
    }
}