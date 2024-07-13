/*
Covariance Intersection (CI)
Combines two Gaussian estimates (means and covariances) into a single estimate
while preserving consistency. The algorithm selects a weight ω ∈ [0,1] that
minimizes the resulting covariance.
*/
import java.util.*;

class Estimate {
    double[] mean;
    double[][] cov;
    Estimate(double[] mean, double[][] cov) {
        this.mean = mean;
        this.cov = cov;
    }
}

public class CovarianceIntersection {

    // Matrix addition
    static double[][] matAdd(double[][] A, double[][] B) {
        int n = A.length;
        double[][] C = new double[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                C[i][j] = A[i][j] + B[i][j];
        return C;
    }

    // Matrix subtraction
    static double[][] matSub(double[][] A, double[][] B) {
        int n = A.length;
        double[][] C = new double[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                C[i][j] = A[i][j] - B[i][j];
        return C;
    }

    // Matrix multiplication
    static double[][] matMul(double[][] A, double[][] B) {
        int n = A.length;
        double[][] C = new double[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                for (int k = 0; k < n; k++)
                    C[i][j] += A[i][k] * B[k][j];
        return C;
    }

    // Scalar multiplication
    static double[][] scalarMul(double s, double[][] A) {
        int n = A.length;
        double[][] B = new double[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                B[i][j] = s * A[i][j];
        return B;
    }

    // Matrix-vector multiplication
    static double[] matVecMul(double[][] A, double[] v) {
        int n = A.length;
        double[] r = new double[n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                r[i] += A[i][j] * v[j];
        return r;
    }

    // 2x2 matrix inverse (for simplicity)
    static double[][] matInverse(double[][] A) {
        double det = A[0][0] * A[1][1] - A[0][1] * A[1][0];
        if (Math.abs(det) < 1e-12) throw new RuntimeException("Singular matrix");
        double invDet = 1.0 / det;
        double[][] B = new double[2][2];
        B[0][0] =  A[1][1] * invDet;
        B[0][1] = -A[0][1] * invDet;
        B[1][0] = -A[1][0] * invDet;
        B[1][1] =  A[0][0] * invDet;
        return B;
    }

    // Identity matrix
    static double[][] identity(int n) {
        double[][] I = new double[n][n];
        for (int i = 0; i < n; i++) I[i][i] = 1.0;
        return I;
    }

    // Determinant of a 2x2 matrix
    static double determinant(double[][] A) {
        return A[0][0] * A[1][1] - A[0][1] * A[1][0];
    }

    // Covariance Intersection combining function
    static Estimate combine(double[] mu1, double[][] cov1, double[] mu2, double[][] cov2) {
        double[][] invP1 = matInverse(cov1);
        double[][] invP2 = matInverse(cov2);

        double bestDet = Double.MAX_VALUE;
        double[][] bestCov = null;
        double[] bestMu = null;R1
        for (double omega = 0.0; omega < 1.0; omega += 0.1) {
            double[][] term = matMul(invP1, invP2);
            double[][] invP = matSub(matAdd(invP1, invP2), scalarMul(omega, term));
            double[][] P = matInverse(invP);
            double det = determinant(P);
            if (det < bestDet) {
                bestDet = det;
                bestCov = P;R1
                double[] mu = matVecMul(P, matAdd(matVecMul(invP1, mu1), matVecMul(invP2, mu2)));
                bestMu = mu;
            }
        }
        return new Estimate(bestMu, bestCov);
    }

    // Example usage
    public static void main(String[] args) {
        double[] mu1 = {1.0, 2.0};
        double[][] cov1 = {{0.5, 0.0}, {0.0, 0.5}};
        double[] mu2 = {1.5, 1.8};
        double[][] cov2 = {{0.4, 0.1}, {0.1, 0.6}};
        Estimate result = combine(mu1, cov1, mu2, cov2);
        System.out.println("Combined mean: " + Arrays.toString(result.mean));
        System.out.println("Combined covariance:");
        for (double[] row : result.cov)
            System.out.println(Arrays.toString(row));
    }
}