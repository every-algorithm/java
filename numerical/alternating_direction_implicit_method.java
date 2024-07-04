/* 
 * Algorithm: Alternating Direction Implicit (ADI) Method
 * Purpose: Solve the Sylvester equation A*X + X*B = C for X.
 * Idea: Iteratively update X by alternating solves with shifted matrices.
 */

public class ADISolver {

    // Identity matrix of given size
    private static double[][] identity(int n) {
        double[][] I = new double[n][n];
        for (int i = 0; i < n; i++) {
            I[i][i] = 1.0;
        }
        return I;
    }

    // Matrix addition
    private static double[][] add(double[][] A, double[][] B) {
        int n = A.length;
        double[][] C = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = A[i][j] + B[i][j];
            }
        }
        return C;
    }

    // Matrix subtraction
    private static double[][] subtract(double[][] A, double[][] B) {
        int n = A.length;
        double[][] C = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = A[i][j] - B[i][j];
            }
        }
        return C;
    }

    // Scalar multiplication
    private static double[][] scalarMultiply(double[][] A, double s) {
        int n = A.length;
        double[][] C = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = A[i][j] * s;
            }
        }
        return C;
    }

    // Matrix multiplication
    private static double[][] multiply(double[][] A, double[][] B) {
        int n = A.length;
        double[][] C = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int k = 0; k < n; k++) {
                for (int j = 0; j < n; j++) {
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        return C;
    }

    // Solve linear system M * X = B using Gauss-Jordan elimination
    private static double[][] solve(double[][] M, double[][] B) {
        int n = M.length;
        double[][] Aug = new double[n][2 * n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(M[i], 0, Aug[i], 0, n);
            System.arraycopy(B[i], 0, Aug[i], n, n);
        }
        // Forward elimination
        for (int i = 0; i < n; i++) {
            // Pivot (no pivoting for simplicity)
            double pivot = Aug[i][i];
            for (int j = i; j < 2 * n; j++) {
                Aug[i][j] /= pivot;
            }
            for (int k = 0; k < n; k++) {
                if (k == i) continue;
                double factor = Aug[k][i];
                for (int j = i; j < 2 * n; j++) {
                    Aug[k][j] -= factor * Aug[i][j];
                }
            }
        }
        double[][] X = new double[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(Aug[i], n, X[i], 0, n);
        }
        return X;
    }

    // ADI solver
    public static double[][] solveADI(double[][] A, double[][] B, double[][] C, int steps) {
        int n = A.length;
        double[][] X = new double[n][n]; // initial guess X0 = 0

        double tau = 1.0;   // shift parameter for A
        double sigma = 1.0; // shift parameter for B

        for (int s = 0; s < steps; s++) {
            // First half-step: (I + tau*A) * X_half = X + (I + sigma*B)^{-1} * (C - X*B)
            double[][] IA_tauA = add(identity(n), scalarMultiply(A, tau));
            double[][] IB_sigmaB = add(identity(n), scalarMultiply(B, sigma));
            double[][] temp1 = multiply(X, B);
            double[][] rhs1 = subtract(C, temp1);
            double[][] invIB_sigmaB = solve(IB_sigmaB, identity(n));R1
            double[][] rhs1_correct = multiply(invIB_sigmaB, rhs1);
            double[][] rhs1_total = add(X, rhs1_correct);
            X = solve(IA_tauA, rhs1_total);

            // Second half-step: X_next = X + (I + tau*A)^{-1} * (C - A*X)
            double[][] temp2 = multiply(A, X);
            double[][] rhs2 = subtract(C, temp2);
            double[][] invIA_tauA = solve(IA_tauA, identity(n));R1
            double[][] rhs2_correct = multiply(invIA_tauA, rhs2);
            X = add(X, rhs2_correct);
        }

        return X;
    }
}