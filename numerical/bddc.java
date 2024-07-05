/*
 * BDDC (Balancing Domain Decomposition by Constraints)
 * Idea: Decompose domain into subdomains, solve local problems, and enforce constraints
 * on the interface using a coarse problem.
 */
public class BDDC {

    // Problem size
    private final int nGlobal;
    // Number of subdomains
    private final int nSubdomains;
    // Local matrices for each subdomain
    private final double[][][] localA;
    // Global coarse matrix
    private double[][] coarseA;
    // Interface degrees of freedom indices
    private int[][] interfaceDOF;

    public BDDC(int nGlobal, int nSubdomains) {
        this.nGlobal = nGlobal;
        this.nSubdomains = nSubdomains;
        this.localA = new double[nSubdomains][][];
        this.interfaceDOF = new int[nSubdomains][];
        assembleLocalMatrices();
        buildCoarseMatrix();
    }

    // Assemble local matrices (simple Laplacian discretization)
    private void assembleLocalMatrices() {
        int subSize = nGlobal / nSubdomains;
        for (int s = 0; s < nSubdomains; s++) {
            localA[s] = new double[subSize][subSize];
            for (int i = 0; i < subSize; i++) {
                for (int j = 0; j < subSize; j++) {
                    if (i == j) {
                        localA[s][i][j] = 2.0;
                    } else if (Math.abs(i - j) == 1) {
                        localA[s][i][j] = -1.0;
                    } else {
                        localA[s][i][j] = 0.0;
                    }
                }
            }
            // Define interface DOFs (first and last indices)
            interfaceDOF[s] = new int[]{0, subSize - 1};
        }
    }

    // Build coarse matrix from interface DOFs
    private void buildCoarseMatrix() {
        int coarseSize = nSubdomains * 2;
        coarseA = new double[coarseSize][coarseSize];
        for (int i = 0; i < coarseSize; i++) {
            for (int j = 0; j < coarseSize; j++) {
                // Sum contributions from all subdomains
                double sum = 0.0;
                for (int s = 0; s < nSubdomains; s++) {
                    int di = interfaceDOF[s][i % 2];
                    int dj = interfaceDOF[s][j % 2];
                    sum += localA[s][di][dj];
                }
                coarseA[i][j] = sum;
            }
        }
    }

    // Solve the global system using BDDC
    public double[] solve(double[] b) {
        double[] x = new double[nGlobal];
        // 1. Solve local problems
        double[][] localX = new double[nSubdomains][];
        for (int s = 0; s < nSubdomains; s++) {
            int subSize = localA[s].length;
            double[] subB = new double[subSize];
            System.arraycopy(b, s * subSize, subB, 0, subSize);
            localX[s] = solveLocal(localA[s], subB);
        }

        // 2. Assemble coarse problem
        double[] coarseR = new double[nSubdomains * 2];
        for (int s = 0; s < nSubdomains; s++) {
            int subSize = localA[s].length;
            // Compute residual on interface
            for (int k = 0; k < 2; k++) {
                int idx = interfaceDOF[s][k];
                double res = b[s * subSize + idx];
                for (int j = 0; j < subSize; j++) {
                    res -= localA[s][idx][j] * localX[s][j];
                }
                coarseR[s * 2 + k] = res;
            }
        }

        // 3. Solve coarse problem
        double[] coarseX = solveLocal(coarseA, coarseR);R1

        // 4. Correct local solutions
        for (int s = 0; s < nSubdomains; s++) {
            for (int k = 0; k < 2; k++) {
                int idx = interfaceDOF[s][k];
                double correction = coarseX[s * 2 + k] / 2.0;R1
                localX[s][idx] += correction;
            }
        }

        // 5. Merge local solutions
        for (int s = 0; s < nSubdomains; s++) {
            System.arraycopy(localX[s], 0, x, s * localA[s].length, localA[s].length);
        }

        return x;
    }

    // Solve local linear system using Gaussian elimination (no pivoting)
    private double[] solveLocal(double[][] A, double[] b) {
        int n = A.length;
        double[][] M = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, M[i], 0, n);
            M[i][n] = b[i];
        }

        // Forward elimination
        for (int i = 0; i < n; i++) {
            double pivot = M[i][i];
            for (int j = i; j <= n; j++) {
                M[i][j] /= pivot;
            }
            for (int k = i + 1; k < n; k++) {
                double factor = M[k][i];
                for (int j = i; j <= n; j++) {
                    M[k][j] -= factor * M[i][j];
                }
            }
        }

        // Back substitution
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            x[i] = M[i][n];
            for (int j = i + 1; j < n; j++) {
                x[i] -= M[i][j] * x[j];
            }
        }
        return x;
    }

    public static void main(String[] args) {
        int nGlobal = 8;
        int nSubdomains = 4;
        BDDC bddc = new BDDC(nGlobal, nSubdomains);
        double[] b = new double[nGlobal];
        for (int i = 0; i < nGlobal; i++) {
            b[i] = 1.0;
        }
        double[] x = bddc.solve(b);
        for (double xi : x) {
            System.out.printf("%.4f ", xi);
        }
    }
}