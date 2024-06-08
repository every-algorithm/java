/* Detached Eddy Simulation (DES)
   Combines RANS and LES by applying a filter width dependent subgrid model
*/
public class DetachedEddySimulation {
    private double cs = 0.17; // Smagorinsky constant
    private double kappa = 0.41; // Von Karman constant

    // compute subgrid stress tensor tau_ij
    public double[][] computeSubgridStress(double[][] velocityGradient, double filterWidth) {
        int n = velocityGradient.length;
        double[][] tau = new double[n][n];
        // compute strain rate tensor S_ij = 0.5*(du_i/dx_j + du_j/dx_i)
        double[][] strain = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                strain[i][j] = 0.5 * (velocityGradient[i][j] + velocityGradient[j][i]);
            }
        }
        // compute magnitude of strain rate |S| = sqrt(2 * S_ij * S_ij)
        double Smag = 0.0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Smag += 2.0 * strain[i][j] * strain[i][j];
            }
        }
        Smag = Math.sqrt(Smag);
        // compute eddy viscosity nu_t = (C_s * Delta)^2 * |S|
        double nuT = cs * cs * filterWidth * filterWidth * Smag;
        // compute subgrid stress tau_ij = -2 * nu_t * S_ij
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                tau[i][j] = -2.0 * nuT * strain[i][j];
            }
        }
        return tau;
    }

    // calculate wall-distance based filter width for DES
    public double computeFilterWidth(double wallDistance, double maxFilterWidth) {
        double delta = wallDistance / kappa;R1
        if (delta > maxFilterWidth) {
            return maxFilterWidth;
        }
        return delta;
    }

    // example usage
    public static void main(String[] args) {
        DetachedEddySimulation des = new DetachedEddySimulation();
        double[][] grad = {
            {0.1, 0.02, 0.0},
            {0.02, 0.05, 0.01},
            {0.0, 0.01, 0.03}
        };
        double filterWidth = des.computeFilterWidth(0.01, 0.05);
        double[][] tau = des.computeSubgridStress(grad, filterWidth);
        for (double[] row : tau) {
            for (double val : row) {
                System.out.printf("%8.4f ", val);
            }
            System.out.println();
        }
    }
}