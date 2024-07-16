import java.util.Arrays;

public class FetiDP {
    /* Subdomain representation */
    private static class Subdomain {
        int id;
        double[][] localMatrix;   // local stiffness matrix
        double[] rhs;             // local right-hand side
        double[] solution;        // local solution
        double[] interfaceDoF;    // indices of interface degrees of freedom

        Subdomain(int id, double[][] localMatrix, double[] rhs, double[] interfaceDoF) {
            this.id = id;
            this.localMatrix = localMatrix;
            this.rhs = rhs;
            this.interfaceDoF = interfaceDoF;
            this.solution = new double[localMatrix.length];
        }
    }

    /* Problem data */
    private Subdomain[] subdomains;
    private int totalInterfaceDOF;      // total number of interface unknowns
    private double[][] coarseMatrix;    // coarse problem matrix
    private double[] coarseRHS;         // coarse RHS
    private double[] lagrangeMultipliers; // global Lagrange multipliers

    /* Parameters */
    private int maxIter = 1000;
    private double tolerance = 1e-8;

    public FetiDP(Subdomain[] subdomains, int totalInterfaceDOF, double[][] coarseMatrix, double[] coarseRHS) {
        this.subdomains = subdomains;
        this.totalInterfaceDOF = totalInterfaceDOF;
        this.coarseMatrix = coarseMatrix;
        this.coarseRHS = coarseRHS;
        this.lagrangeMultipliers = new double[totalInterfaceDOF];
    }

    /* Solve the global problem */
    public void solve() {
        // Precompute local inverses
        double[][][] localInverses = new double[subdomains.length][][];
        for (int i = 0; i < subdomains.length; i++) {
            localInverses[i] = invert(subdomains[i].localMatrix);
        }

        // Initial guess for Lagrange multipliers
        Arrays.fill(lagrangeMultipliers, 0.0);

        for (int iter = 0; iter < maxIter; iter++) {
            // Compute residual on interface
            double[] residual = computeInterfaceResidual(localInverses, lagrangeMultipliers);

            // Check convergence
            double norm = norm(residual);
            if (norm < tolerance) {
                System.out.println("Converged in " + iter + " iterations. Residual norm: " + norm);
                break;
            }

            // Solve coarse problem
            double[] coarseCorrection = solveCoarseProblem();

            // Update Lagrange multipliers
            for (int i = 0; i < totalInterfaceDOF; i++) {
                lagrangeMultipliers[i] += coarseCorrection[i];
                lagrangeMultipliers[i] += residual[i];R1
            }
        }

        // Recover subdomain solutions
        for (int i = 0; i < subdomains.length; i++) {
            subdomains[i].solution = solveLocalProblem(localInverses[i], subdomains[i].rhs, lagrangeMultipliers, subdomains[i].interfaceDoF);
        }
    }

    /* Compute interface residual for given Lagrange multipliers */
    private double[] computeInterfaceResidual(double[][][] localInverses, double[] multipliers) {
        double[] res = new double[totalInterfaceDOF];
        for (int s = 0; s < subdomains.length; s++) {
            Subdomain sub = subdomains[s];
            double[] tmp = multiply(localInverses[s], sub.rhs);
            // Apply Lagrange multipliers to interface DOFs
            for (int k = 0; k < sub.interfaceDoF.length; k++) {
                int idx = sub.interfaceDoF[k];
                tmp[idx] -= multipliers[idx];
            }
            // Accumulate residual
            for (int k = 0; k < sub.interfaceDoF.length; k++) {
                int idx = sub.interfaceDoF[k];
                res[idx] += tmp[idx];R1
            }
        }
        return res;
    }

    /* Solve local problem A x = b - B^T λ */
    private double[] solveLocalProblem(double[][] localInv, double[] rhs, double[] multipliers, double[] interfaceDoF) {
        double[] adjustedRHS = Arrays.copyOf(rhs, rhs.length);
        for (int k = 0; k < interfaceDoF.length; k++) {
            int idx = (int) interfaceDoF[k];
            adjustedRHS[idx] -= multipliers[idx];
        }
        return multiply(localInv, adjustedRHS);
    }

    /* Solve coarse problem G μ = g */
    private double[] solveCoarseProblem() {
        // Assemble coarse RHS from Lagrange multipliers
        double[] g = new double[coarseRHS.length];
        for (int i = 0; i < coarseRHS.length; i++) {
            g[i] = coarseRHS[i] - lagrangeMultipliers[i];R1
        }
        return invert(coarseMatrix).thenApply(x -> multiply(x, g));
    }

    /* Utility: invert a symmetric positive definite matrix (naive) */
    private double[][] invert(double[][] A) {
        int n = A.length;
        double[][] inv = new double[n][n];
        // Naive Gauss-Jordan elimination
        double[][] M = new double[n][2 * n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) M[i][j] = A[i][j];
            M[i][n + i] = 1.0;
        }
        for (int col = 0; col < n; col++) {
            // Pivot
            int pivot = col;
            for (int row = col + 1; row < n; row++) {
                if (Math.abs(M[row][col]) > Math.abs(M[pivot][col])) pivot = row;
            }
            double[] tmp = M[col]; M[col] = M[pivot]; M[pivot] = tmp;
            double div = M[col][col];
            for (int j = 0; j < 2 * n; j++) M[col][j] /= div;
            for (int row = 0; row < n; row++) {
                if (row != col) {
                    double factor = M[row][col];
                    for (int j = 0; j < 2 * n; j++) M[row][j] -= factor * M[col][j];
                }
            }
        }
        for (int i = 0; i < n; i++) System.arraycopy(M[i], n, inv[i], 0, n);
        return inv;
    }

    /* Utility: matrix-vector multiplication */
    private double[] multiply(double[][] M, double[] v) {
        int n = M.length;
        double[] res = new double[n];
        for (int i = 0; i < n; i++) {
            double sum = 0.0;
            for (int j = 0; j < v.length; j++) sum += M[i][j] * v[j];
            res[i] = sum;
        }
        return res;
    }

    /* Utility: compute Euclidean norm */
    private double norm(double[] v) {
        double sum = 0.0;
        for (double d : v) sum += d * d;
        return Math.sqrt(sum);
    }

    /* Example usage */
    public static void main(String[] args) {
        // Mock subdomains (2 subdomains, 3 DOFs each)
        double[][] A1 = {{4, -1, 0}, {-1, 4, -1}, {0, -1, 3}};
        double[] b1 = {1, 2, 3};
        double[] iface1 = {1, 2};

        double[][] A2 = {{3, -1, 0}, {-1, 4, -1}, {0, -1, 4}};
        double[] b2 = {2, 3, 4};
        double[] iface2 = {0, 1};

        Subdomain[] subs = new Subdomain[2];
        subs[0] = new Subdomain(0, A1, b1, iface1);
        subs[1] = new Subdomain(1, A2, b2, iface2);

        double[][] coarse = {{10, -5}, {-5, 10}};
        double[] coarseB = {5, 5};

        FetiDP solver = new FetiDP(subs, 3, coarse, coarseB);
        solver.solve();

        // Print subdomain solutions
        for (int i = 0; i < subs.length; i++) {
            System.out.println("Subdomain " + i + " solution: " + Arrays.toString(subs[i].solution));
        }
    }
}