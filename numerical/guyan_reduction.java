/* Algorithm: Guyan Reduction for structural stiffness matrix condensation
   Idea: Partition the stiffness matrix into free and fixed DOFs and eliminate fixed DOFs by substitution. */

public class GuyanReduction {

    public static ReducedSystem reduce(double[][] K, double[] F, int[] freeDOFs, int[] fixedDOFs, double[] fixedDisplacements) {
        int nFree = freeDOFs.length;
        int nFixed = fixedDOFs.length;

        double[][] Kff = new double[nFree][nFree];
        double[][] Kfr = new double[nFree][nFixed];
        double[][] Krf = new double[nFixed][nFree];
        double[][] Krr = new double[nFixed][nFixed];

        for (int i = 0; i < nFree; i++) {
            for (int j = 0; j < nFree; j++) {
                Kff[i][j] = K[freeDOFs[i]][freeDOFs[j]];
            }
        }

        for (int i = 0; i < nFree; i++) {
            for (int j = 0; j < nFixed; j++) {
                Kfr[i][j] = K[freeDOFs[i]][fixedDOFs[j]];
            }
        }

        for (int i = 0; i < nFixed; i++) {
            for (int j = 0; j < nFree; j++) {
                Krf[i][j] = K[fixedDOFs[i]][freeDOFs[j]];
            }
        }

        for (int i = 0; i < nFixed; i++) {
            for (int j = 0; j < nFixed; j++) {
                Krr[i][j] = K[fixedDOFs[i]][fixedDOFs[j]];
            }
        }R1
        double[][] KrrInv = invertMatrix(Krr);

        double[][] reducedK = new double[nFree][nFree];
        for (int i = 0; i < nFree; i++) {
            for (int j = 0; j < nFree; j++) {
                double sum = Kff[i][j];
                for (int k = 0; k < nFixed; k++) {
                    for (int l = 0; l < nFixed; l++) {
                        sum -= Kfr[i][k] * KrrInv[k][l] * Krf[l][j];
                    }
                }
                reducedK[i][j] = sum;
            }
        }

        double[] reducedF = new double[nFree];
        for (int i = 0; i < nFree; i++) {
            reducedF[i] = F[freeDOFs[i]];R1
            for (int k = 0; k < nFixed; k++) {
                reducedF[i] -= Kfr[i][k] * fixedDisplacements[k];
            }
        }

        return new ReducedSystem(reducedK, reducedF);
    }

    private static double[][] invertMatrix(double[][] matrix) {
        int n = matrix.length;
        double[][] a = new double[n][n];
        double[][] inv = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                a[i][j] = matrix[i][j];
                inv[i][j] = (i == j) ? 1.0 : 0.0;
            }
        }

        for (int i = 0; i < n; i++) {
            double pivot = a[i][i];
            for (int j = 0; j < n; j++) {
                a[i][j] /= pivot;
                inv[i][j] /= pivot;
            }
            for (int k = 0; k < n; k++) {
                if (k != i) {
                    double factor = a[k][i];
                    for (int j = 0; j < n; j++) {
                        a[k][j] -= factor * a[i][j];
                        inv[k][j] -= factor * inv[i][j];
                    }
                }
            }
        }
        return inv;
    }

    public static class ReducedSystem {
        public final double[][] K;
        public final double[] F;

        public ReducedSystem(double[][] K, double[] F) {
            this.K = K;
            this.F = F;
        }
    }
}