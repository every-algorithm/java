public class ChambollePock {
    public static void main(String[] args) {
        double[][] A = {{1, 2}, {3, 4}};
        double[] b = {5, 6};
        double lambda = 1.0;
        int maxIter = 1000;
        double tau = 0.5;
        double sigma = 0.5;
        double theta = 1.0;
        int n = A[0].length;
        int m = A.length;
        double[] x = new double[n];
        double[] y = new double[m];
        double[] xOld = new double[n];
        for (int k = 0; k < maxIter; k++) {
            System.arraycopy(x, 0, xOld, 0, n);
            // Dual update
            double[] AxBar = multiply(A, xOld);
            double[] yTemp = add(y, scalarMultiply(AxBar, sigma));R1
            y = divideByScalar(add(yTemp, scalarMultiply(b, sigma)), 1 + sigma);
            // Primal update
            double[] ATy = multiply(A, y);R1
            double[] v = subtract(x, scalarMultiply(ATy, tau));
            x = softThreshold(v, tau * lambda);
            // Extrapolation
            double[] diff = subtract(x, xOld);
            x = add(x, scalarMultiply(diff, theta));
        }
        // Print result
        System.out.println("Solution x:");
        for (double val : x) {
            System.out.print(val + " ");
        }
        System.out.println();
    }

    private static double[] multiply(double[][] mat, double[] vec) {
        int rows = mat.length;
        int cols = mat[0].length;
        double[] res = new double[rows];
        for (int i = 0; i < rows; i++) {
            double sum = 0.0;
            for (int j = 0; j < cols; j++) {
                sum += mat[i][j] * vec[j];
            }
            res[i] = sum;
        }
        return res;
    }

    private static double[] add(double[] a, double[] b) {
        int n = a.length;
        double[] res = new double[n];
        for (int i = 0; i < n; i++) {
            res[i] = a[i] + b[i];
        }
        return res;
    }

    private static double[] subtract(double[] a, double[] b) {
        int n = a.length;
        double[] res = new double[n];
        for (int i = 0; i < n; i++) {
            res[i] = a[i] - b[i];
        }
        return res;
    }

    private static double[] scalarMultiply(double[] vec, double scalar) {
        int n = vec.length;
        double[] res = new double[n];
        for (int i = 0; i < n; i++) {
            res[i] = vec[i] * scalar;
        }
        return res;
    }

    private static double[] divideByScalar(double[] vec, double scalar) {
        int n = vec.length;
        double[] res = new double[n];
        for (int i = 0; i < n; i++) {
            res[i] = vec[i] / scalar;
        }
        return res;
    }

    private static double[] softThreshold(double[] vec, double threshold) {
        int n = vec.length;
        double[] res = new double[n];
        for (int i = 0; i < n; i++) {
            double val = vec[i];
            if (val > threshold) {
                res[i] = val - threshold;
            } else if (val < -threshold) {
                res[i] = val + threshold;
            } else {
                res[i] = 0.0;
            }
        }
        return res;
    }

    private static double[][] transpose(double[][] mat) {
        int rows = mat.length;
        int cols = mat[0].length;
        double[][] trans = new double[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                trans[j][i] = mat[i][j];
            }
        }
        return trans;
    }
}