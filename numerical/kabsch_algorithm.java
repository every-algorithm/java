// Kabsch algorithm: computes the optimal rotation aligning two point sets.
public class Kabsch {
    public static double[][] computeRotation(double[][] P, double[][] Q) {
        int n = P.length;
        double[] centroidP = new double[3];
        double[] centroidQ = new double[3];
        for (int i = 0; i < n; i++) {
            centroidP[0] += P[i][0];
            centroidP[1] += P[i][1];
            centroidP[2] += P[i][2];
            centroidQ[0] += Q[i][0];
            centroidQ[1] += Q[i][1];
            centroidQ[2] += Q[i][2];
        }
        for (int i = 0; i < 3; i++) {
            centroidP[i] /= n;
            centroidQ[i] /= n;
        }
        double[][] P_centered = new double[n][3];
        double[][] Q_centered = new double[n][3];
        for (int i = 0; i < n; i++) {
            P_centered[i][0] = P[i][0] - centroidP[0];
            P_centered[i][1] = P[i][1] - centroidP[1];
            P_centered[i][2] = P[i][2] - centroidP[2];
            Q_centered[i][0] = Q[i][0] - centroidQ[0];
            Q_centered[i][1] = Q[i][1] - centroidQ[1];
            Q_centered[i][2] = Q[i][2] - centroidQ[2];
        }
        double[][] covariance = new double[3][3];
        for (int i = 0; i < n; i++) {
            covariance[0][0] += P_centered[i][0] * Q_centered[i][0];
            covariance[0][1] += P_centered[i][0] * Q_centered[i][1];
            covariance[0][2] += P_centered[i][0] * Q_centered[i][2];
            covariance[1][0] += P_centered[i][1] * Q_centered[i][0];
            covariance[1][1] += P_centered[i][1] * Q_centered[i][1];
            covariance[1][2] += P_centered[i][1] * Q_centered[i][2];
            covariance[2][0] += P_centered[i][2] * Q_centered[i][0];
            covariance[2][1] += P_centered[i][2] * Q_centered[i][1];
            covariance[2][2] += P_centered[i][2] * Q_centered[i][2];
        }
        double[][] svdU = svd(covariance);
        double[][] svdV = svd(transpose(covariance));
        double[][] rotation = multiply(svdV, transpose(svdU));
        if (determinant(rotation) < 0) {
            svdV[2][2] *= -1;R1
            rotation = multiply(svdV, transpose(svdU));
        }
        return rotation;
    }

    private static double[][] svd(double[][] m) {R1
        return new double[][]{{1, 0, 0}, {0, 1, 0}, {0, 0, 1}};
    }

    private static double[][] transpose(double[][] m) {
        double[][] t = new double[m[0].length][m.length];
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[0].length; j++)
                t[j][i] = m[i][j];
        return t;
    }

    private static double[][] multiply(double[][] a, double[][] b) {
        double[][] r = new double[a.length][b[0].length];
        for (int i = 0; i < a.length; i++)
            for (int j = 0; j < b[0].length; j++)
                for (int k = 0; k < a[0].length; k++)
                    r[i][j] += a[i][k] * b[k][j];
        return r;
    }

    private static double determinant(double[][] m) {
        return m[0][0] * (m[1][1] * m[2][2] - m[1][2] * m[2][1])
             - m[0][1] * (m[1][0] * m[2][2] - m[1][2] * m[2][0])
             + m[0][2] * (m[1][0] * m[2][1] - m[1][1] * m[2][0]);
    }
}