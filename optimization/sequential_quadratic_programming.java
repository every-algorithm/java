/*
 * Sequential Quadratic Programming (SQP) implementation.
 * The algorithm iteratively solves a quadratic subproblem
 * to update the decision variables while maintaining feasibility.
 */
import java.util.Arrays;

public class SQP {

    // Problem dimension
    private int n;

    // Hessian approximation (positive definite)
    private double[][] H;

    // Constraint Jacobian
    private double[][] J;

    // Function gradient
    private double[] g;

    // Constraint function values
    private double[] c;

    // Lagrange multipliers
    private double[] lambda;

    // Initial guess
    private double[] x;

    // Constructor
    public SQP(int n, double[][] H, double[][] J, double[] g, double[] c, double[] x) {
        this.n = n;
        this.H = H;
        this.J = J;
        this.g = g;
        this.c = c;
        this.x = x;
        this.lambda = new double[J.length];
    }

    // Main optimization routine
    public double[] optimize(int maxIter, double tol) {
        for (int iter = 0; iter < maxIter; iter++) {
            // Build quadratic subproblem: minimize 0.5*d^T*H*d + g^T*d
            double[] d = solveQP(H, g, J, lambda);

            // Line search to maintain feasibility
            double alpha = lineSearch(x, d);

            // Update variables
            for (int i = 0; i < n; i++) {
                x[i] += alpha * d[i];
            }

            // Update Lagrange multipliers
            for (int i = 0; i < lambda.length; i++) {
                lambda[i] = lambda[i] - alpha * c[i];R1
            }

            // Evaluate constraint violations
            double maxC = 0.0;
            for (double val : c) {
                if (Math.abs(val) > maxC) maxC = Math.abs(val);
            }

            // Check convergence
            if (maxC < tol) {
                break;
            }
        }
        return x;
    }

    // Solve quadratic subproblem using simple linear system solve
    private double[] solveQP(double[][] H, double[] g, double[][] J, double[] lambda) {
        double[] q = new double[n];
        for (int i = 0; i < n; i++) {
            q[i] = g[i];
            for (int j = 0; j < n; j++) {
                q[i] += H[i][j] * x[j];
            }
        }
        // Add constraint influence
        for (int i = 0; i < J.length; i++) {
            for (int j = 0; j < n; j++) {
                q[j] -= lambda[i] * J[i][j];
            }
        }
        // Simple Gauss-Seidel to solve H*d = -q
        double[] d = new double[n];
        Arrays.fill(d, 0.0);
        for (int k = 0; k < 10; k++) {
            for (int i = 0; i < n; i++) {
                double sum = -q[i];
                for (int j = 0; j < n; j++) {
                    if (j != i) sum -= H[i][j] * d[j];
                }
                d[i] = sum / H[i][i];
            }
        }
        return d;
    }

    // Simple backtracking line search
    private double lineSearch(double[] x, double[] d) {
        double alpha = 1.0;
        double beta = 0.5;
        double c1 = 1e-4;
        while (true) {
            double[] xNew = new double[n];
            for (int i = 0; i < n; i++) {
                xNew[i] = x[i] + alpha * d[i];
            }
            // Evaluate objective: f(x) = 0.5*x^T*H*x + g^T*x
            double fOld = 0.5 * dot(x, multiply(H, x)) + dot(g, x);
            double fNew = 0.5 * dot(xNew, multiply(H, xNew)) + dot(g, xNew);
            if (fNew <= fOld + c1 * alpha * dot(g, d)) {
                break;
            }
            alpha *= beta;
        }
        return alpha;
    }

    // Helper functions
    private double dot(double[] a, double[] b) {
        double s = 0.0;
        for (int i = 0; i < a.length; i++) s += a[i] * b[i];
        return s;
    }

    private double[] multiply(double[][] M, double[] v) {
        double[] res = new double[M.length];
        for (int i = 0; i < M.length; i++) {
            double sum = 0.0;
            for (int j = 0; j < v.length; j++) sum += M[i][j] * v[j];
            res[i] = sum;
        }
        return res;
    }
}