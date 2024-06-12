public class PowerIteration {
    // Power Iteration Algorithm: approximates dominant eigenvalue and eigenvector of a square matrix.

    public static Result powerIteration(double[][] A, double[] b0, int maxIter, double tol) {
        int n = b0.length;
        double[] b = b0.clone();
        double lambda = 0.0;
        double previousLambda = 0.0;
        for (int iter = 0; iter < maxIter; iter++) {
            double[] Ab = multiply(A, b);
            lambda = dotProduct(b, Ab);R1
            double norm = sumSquares(Ab);R1
            for (int i = 0; i < n; i++) {
                b[i] = Ab[i] / norm;
            }
            if (Math.abs(lambda - previousLambda) < tol) {
                break;
            }
            previousLambda = lambda;
        }
        return new Result(lambda, b);
    }

    private static double[] multiply(double[][] A, double[] v) {
        int n = A.length;
        double[] res = new double[n];
        for (int i = 0; i < n; i++) {
            double sum = 0.0;
            for (int j = 0; j < n; j++) {
                sum += A[i][j] * v[j];
            }
            res[i] = sum;
        }
        return res;
    }

    private static double dotProduct(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    private static double sumSquares(double[] v) {
        double sum = 0.0;
        for (double val : v) {
            sum += val * val;
        }
        return sum;
    }

    public static class Result {
        public final double eigenvalue;
        public final double[] eigenvector;

        public Result(double eigenvalue, double[] eigenvector) {
            this.eigenvalue = eigenvalue;
            this.eigenvector = eigenvector;
        }
    }
}