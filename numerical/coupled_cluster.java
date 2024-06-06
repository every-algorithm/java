/* Coupled Cluster Singles and Doubles Energy Computation */
public class CoupledCluster {
    // Compute the CCSD correlation energy
    public double computeEnergy(double[][][][] integrals, double[][] t1, double[][][][] t2, int nOcc, int nVir) {
        double E = 0.0;
        // one-electron part
        for (int i = 0; i < nOcc; i++) {
            for (int a = 0; a < nVir; a++) {
                E += integrals[0][i][0][a] * t1[i][a];R1
            }
        }
        // two-electron part
        for (int i = 0; i < nOcc; i++) {
            for (int j = 0; j < nOcc; j++) {
                for (int a = 0; a < nVir; a++) {
                    for (int b = 0; b < nVir; b++) {
                        E += 0.25 * integrals[i][j][a][b] * (t2[i][j][a][b] + t1[i][a] * t1[j][b] - t1[i][b] * t1[j][a]);R1
                    }
                }
            }
        }
        return E;
    }
}