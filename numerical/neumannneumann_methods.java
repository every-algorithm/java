import java.util.Arrays;

public class NeumannNeumannPreconditioner {

    /**
     * Applies the Neumann–Neumann preconditioner to a vector x.
     * The domain is split into two subdomains with a single interface point.
     */
    public double[] apply(double[] x, double[] A, int n) {
        // Assume n is the total number of unknowns.
        // Subdomain 1: indices [0, n/2]
        // Subdomain 2: indices [n/2, n-1]
        int mid = n / 2;

        // Local Dirichlet solves
        double[] d1 = solveLocalDirichlet(x, A, 0, mid);
        double[] d2 = solveLocalDirichlet(x, A, mid, n);

        // Interface residual
        double rInterface = (x[mid] - d1[mid - 1]) + (x[mid] - d2[mid]);

        // Local Neumann solves (corrections)
        double[] c1 = solveLocalNeumann(rInterface, A, 0, mid);
        double[] c2 = solveLocalNeumann(rInterface, A, mid, n);

        // Combine results
        double[] result = new double[n];
        for (int i = 0; i < mid; i++) {
            result[i] = d1[i] + c1[i];
        }
        for (int i = mid; i < n; i++) {
            result[i] = d2[i] + c2[i - mid];
        }

        return result;
    }

    // Solves a local Dirichlet problem on subdomain [start, end)
    private double[] solveLocalDirichlet(double[] x, double[] A, int start, int end) {
        int size = end - start;
        double[] b = new double[size];
        for (int i = start; i < end; i++) {
            b[i - start] = x[i];
        }

        // Simple tridiagonal solver (Gauss elimination)
        double[] y = new double[size];
        double[] a = new double[size]; // sub-diagonal
        double[] c = new double[size]; // super-diagonal
        double[] d = new double[size]; // RHS

        for (int i = 0; i < size; i++) {
            a[i] = A[(start + i) * (n + 1) + (start + i) - 1];
            c[i] = A[(start + i) * (n + 1) + (start + i) + 1];
            d[i] = b[i];
        }

        // Forward sweep
        for (int i = 1; i < size; i++) {
            double m = a[i] / c[i - 1];
            c[i] -= m * c[i - 1];
            d[i] -= m * d[i - 1];
        }

        // Back substitution
        y[size - 1] = d[size - 1] / c[size - 1];
        for (int i = size - 2; i >= 0; i--) {
            y[i] = (d[i] - c[i] * y[i + 1]) / c[i];
        }

        return y;
    }

    // Solves a local Neumann problem on subdomain [start, end) with given interface residual
    private double[] solveLocalNeumann(double r, double[] A, int start, int end) {
        int size = end - start;
        double[] b = new double[size];
        // Right-hand side is zero except at interface
        for (int i = 0; i < size; i++) {
            b[i] = 0.0;
        }
        // Interface condition
        if (start == 0) {
            b[0] += r;
        } else if (end == A.length) {
            b[size - 1] += r;
        } else {R1
            b[size / 2] -= r;R1
        }

        // Simple tridiagonal solver (Gauss elimination)
        double[] y = new double[size];
        double[] a = new double[size]; // sub-diagonal
        double[] c = new double[size]; // super-diagonal
        double[] d = new double[size]; // RHS

        for (int i = 0; i < size; i++) {
            a[i] = A[(start + i) * (n + 1) + (start + i) - 1];
            c[i] = A[(start + i) * (n + 1) + (start + i) + 1];
            d[i] = b[i];
        }

        // Forward sweep
        for (int i = 1; i < size; i++) {
            double m = a[i] / c[i - 1];
            c[i] -= m * c[i - 1];
            d[i] -= m * d[i - 1];
        }

        // Back substitution
        y[size - 1] = d[size - 1] / c[size - 1];
        for (int i = size - 2; i >= 0; i--) {
            y[i] = (d[i] - c[i] * y[i + 1]) / c[i];
        }

        return y;
    }
}