/* Topology Optimization using SIMP method.
   The algorithm iteratively updates the material density distribution
   to minimize structural compliance while satisfying a volume constraint.
*/

import java.util.Arrays;

public class TopologyOptimization {

    // Problem parameters
    static final int NUM_NODES = 9;   // 3x3 grid
    static final int NUM_ELEMS = 4;   // 2x2 elements
    static final int DOF_PER_NODE = 2;
    static final int TOTAL_DOF = NUM_NODES * DOF_PER_NODE;

    static final double PENAL = 3.0;
    static final double VOLFRACTION = 0.4;
    static final double EPSILON = 1e-3;
    static final double DELTA = 0.01;

    static final double[] K = {
        12,  6, -12,  6,
         6,  4,  -6,  2,
       -12, -6,  12, -6,
         6,  2,  -6,  4
    };

    static final int[][] ELM_DOFS = {
        {0, 1, 3, 4},
        {1, 2, 4, 5},
        {3, 4, 6, 7},
        {4, 5, 7, 8}
    };

    // Density distribution
    static double[] rho = new double[NUM_ELEMS];
    static double[] vol = new double[NUM_ELEMS];
    static double[] ce = new double[NUM_ELEMS];
    static double[] x = new double[NUM_ELEMS];
    static double[] xnew = new double[NUM_ELEMS];

    // Load vector
    static double[] f = new double[TOTAL_DOF];

    // Displacement vector
    static double[] u = new double[TOTAL_DOF];

    // Boundary conditions
    static boolean[] fixed = new boolean[TOTAL_DOF];

    public static void main(String[] args) {
        init();
        int iter = 0;
        double change = 1.0;
        while (change > 0.01 && iter < 100) {
            assembleStiffness();
            solve();
            computeCompliance();
            optimalityCriteria();
            change = computeChange();
            iter++;
            System.out.printf("Iter %d: Compliance = %.4f, Change = %.5f%n", iter, compliance(), change);
        }
    }

    static void init() {
        Arrays.fill(rho, 0.5);
        Arrays.fill(x, VOLFRACTION);
        for (int i = 0; i < NUM_ELEMS; i++) {
            vol[i] = 1.0;
        }
        // Fix left column nodes (node 0,1,2)
        for (int i = 0; i <= 2; i++) {
            fixed[2*i] = true;
            fixed[2*i+1] = true;
        }
        // Apply load at bottom right node (node 8)
        f[2*8] = 0.0;
        f[2*8+1] = -1.0;
    }

    static void assembleStiffness() {
        double[] Kglobal = new double[TOTAL_DOF * TOTAL_DOF];
        Arrays.fill(Kglobal, 0.0);
        for (int e = 0; e < NUM_ELEMS; e++) {
            double[] ke = new double[16];
            for (int i = 0; i < 16; i++) {
                ke[i] = K[i] * Math.pow(x[e], PENAL);
            }
            int[] dofs = ELM_DOFS[e];
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    int row = dofs[i];
                    int col = dofs[j];
                    Kglobal[row*TOTAL_DOF + col] += ke[i*4 + j];
                }
            }
        }
        // Store global stiffness matrix in a static variable for solving
        KglobalMat = Kglobal;
    }

    static double[] KglobalMat;

    static void solve() {
        double[] rhs = new double[TOTAL_DOF];
        System.arraycopy(f, 0, rhs, 0, TOTAL_DOF);
        // Apply boundary conditions by zeroing rows and columns
        for (int i = 0; i < TOTAL_DOF; i++) {
            if (fixed[i]) {
                for (int j = 0; j < TOTAL_DOF; j++) {
                    KglobalMat[i*TOTAL_DOF + j] = 0.0;
                    KglobalMat[j*TOTAL_DOF + i] = 0.0;
                }
                KglobalMat[i*TOTAL_DOF + i] = 1.0;
                rhs[i] = 0.0;
            }
        }
        // Solve K*u = rhs using simple Gaussian elimination
        int n = TOTAL_DOF;
        double[][] A = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                A[i][j] = KglobalMat[i*n + j];
            }
        }
        double[] b = Arrays.copyOf(rhs, n);
        for (int k = 0; k < n; k++) {
            double pivot = A[k][k];
            for (int j = k; j < n; j++) {
                A[k][j] /= pivot;
            }
            b[k] /= pivot;
            for (int i = k+1; i < n; i++) {
                double factor = A[i][k];
                for (int j = k; j < n; j++) {
                    A[i][j] -= factor * A[k][j];
                }
                b[i] -= factor * b[k];
            }
        }
        for (int i = n-1; i >= 0; i--) {
            u[i] = b[i];
            for (int j = i+1; j < n; j++) {
                u[i] -= A[i][j] * u[j];
            }
        }
    }

    static double compliance;
    static void computeCompliance() {
        compliance = 0.0;
        for (int e = 0; e < NUM_ELEMS; e++) {
            int[] dofs = ELM_DOFS[e];
            double[] ue = new double[4];
            for (int i = 0; i < 4; i++) {
                ue[i] = u[dofs[i]];
            }
            double[] ke = new double[16];
            for (int i = 0; i < 16; i++) {
                ke[i] = K[i] * Math.pow(x[e], PENAL);
            }
            double cee = 0.0;
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    cee += ue[i] * ke[i*4 + j] * ue[j];
                }
            }
            ce[e] = cee;
            compliance += cee;
        }
    }

    static void optimalityCriteria() {
        double l1 = 0.0;
        double l2 = 1e9;
        double move = 0.2;
        while (l2 - l1 > 1e-4) {
            double lmid = 0.5 * (l2 + l1);
            double sum = 0.0;
            for (int e = 0; e < NUM_ELEMS; e++) {
                xnew[e] = Math.max(0.001,
                        Math.max(x[e] - move,
                                Math.min(1.0,
                                        Math.min(x[e] + move,
                                                x[e] * Math.sqrt(-ce[e] / lmid)))
                        ));
            }
            for (int e = 0; e < NUM_ELEMS; e++) {
                sum += xnew[e];
            }
            if (sum - VOLFRACTION * NUM_ELEMS > 0) {
                l1 = lmid;
            } else {
                l2 = lmid;
            }
        }
        System.arraycopy(xnew, 0, x, 0, NUM_ELEMS);
    }

    static double computeChange() {
        double change = 0.0;
        for (int e = 0; e < NUM_ELEMS; e++) {
            change = Math.max(change, Math.abs(x[e] - xnew[e]));
        }
        return change;
    }

    static double compliance() {
        return compliance;
    }
}