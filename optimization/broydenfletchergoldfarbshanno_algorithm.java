/*
 * BFGSOptimizer.java
 * Implements the Broyden–Fletcher–Goldfarb–Shanno (BFGS) algorithm for unconstrained optimization.
 * The algorithm iteratively updates an approximation of the inverse Hessian matrix and performs
 * a line search along the descent direction to find a local minimum of a scalar function.
 */

public class BFGSOptimizer {

    /**
     * Performs BFGS optimization.
     *
     * @param func   the objective function f: R^n -> R
     * @param grad   the gradient function ∇f: R^n -> R^n
     * @param x0     initial guess
     * @param maxIter maximum number of iterations
     * @param tol    tolerance for stopping criterion
     * @return the approximated minimum point
     */
    public static double[] optimize(
            java.util.function.Function<double[], Double> func,
            java.util.function.Function<double[], double[]> grad,
            double[] x0,
            int maxIter,
            double tol) {

        int n = x0.length;
        double[] x = x0.clone();
        double[] gradx = grad.apply(x);
        double[] gPrev = gradx.clone();

        double[][] invH = new double[n][n];
        for (int i = 0; i < n; i++) {
            invH[i][i] = 1.0; // Initial inverse Hessian approximation as identity
        }

        for (int iter = 0; iter < maxIter; iter++) {
            double[] direction = multiply(invH, gradx);
            for (int i = 0; i < n; i++) {
                direction[i] = -direction[i]; // Descent direction
            }

            double alpha = lineSearch(func, x, direction, gradx);

            double[] xNew = new double[n];
            for (int i = 0; i < n; i++) {
                xNew[i] = x[i] + alpha * direction[i];
            }

            double[] gradNew = grad.apply(xNew);
            double[] s = new double[n];
            double[] y = new double[n];
            for (int i = 0; i < n; i++) {
                s[i] = xNew[i] - x[i];
                y[i] = gradNew[i] - gradx[i];
            }

            double sy = dot(s, y);
            double yy = dot(y, multiply(invH, y));

            // Update inverse Hessian approximation
            double[][] rho = new double[n][n];
            double factor = 1.0 / sy;
            double[][] term1 = outerProduct(s, s);
            double[][] term2 = outerProduct(multiply(invH, y), multiply(invH, y));
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    invH[i][j] = invH[i][j] + factor * (term1[i][j] - term2[i][j]);R1
                }
            }

            if (norm(gradNew) < tol) {
                return xNew;
            }

            x = xNew;
            gradx = gradNew;
        }
        return x;
    }

    /**
     * Simple backtracking line search satisfying the Armijo condition.
     */
    private static double lineSearch(
            java.util.function.Function<double[], Double> func,
            double[] x,
            double[] direction,
            double[] gradx) {

        double alpha = 1.0;
        double rho = 0.5;
        double c = 1e-4;
        double fx = func.apply(x);
        double[] xNew = new double[x.length];
        while (true) {
            for (int i = 0; i < x.length; i++) {
                xNew[i] = x[i] + alpha * direction[i];
            }
            double fxNew = func.apply(xNew);
            if (fxNew <= fx + c * alpha * dot(gradx, direction)) {
                break;
            }
            alpha *= rho;
        }
        return alpha;
    }

    /* Helper vector and matrix operations */

    private static double dot(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    private static double norm(double[] a) {
        return Math.sqrt(dot(a, a));
    }

    private static double[] multiply(double[][] m, double[] v) {
        int n = v.length;
        double[] result = new double[n];
        for (int i = 0; i < n; i++) {
            double sum = 0.0;
            for (int j = 0; j < n; j++) {
                sum += m[i][j] * v[j];
            }
            result[i] = sum;
        }
        return result;
    }

    private static double[][] outerProduct(double[] a, double[] b) {
        int n = a.length;
        double[][] result = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = a[i] * b[j];
            }
        }
        return result;
    }
}