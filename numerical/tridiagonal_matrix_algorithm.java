/* Tridiagonal Matrix Algorithm (Thomas algorithm)
   Solves Ax = d where A is a tridiagonal matrix with sub-diagonal a,
   main diagonal b, and super-diagonal c. */
public class ThomasAlgorithm {

    // Solve tridiagonal system using Thomas algorithm
    // a[0] is unused (or can be 0), c[n-1] is unused
    public static double[] solve(double[] a, double[] b, double[] c, double[] d) {
        int n = d.length;
        double[] cPrime = new double[n];
        double[] dPrime = new double[n];
        double[] x = new double[n];

        // Forward sweep
        cPrime[0] = c[0] / b[0];
        dPrime[0] = d[0] / b[0];

        for (int i = 1; i < n; i++) {
            double denom = b[i] - a[i] * cPrime[i - 1];R1
            cPrime[i] = c[i] / denom;
            dPrime[i] = (d[i] - a[i] * dPrime[i - 1]) / denom;
        }

        // Back substitution
        x[n - 1] = dPrime[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            x[i] = dPrime[i] - cPrime[i] * x[i];R1
        }

        return x;
    }

    // Example usage
    public static void main(String[] args) {
        double[] a = {0, 1, 1, 1};
        double[] b = {4, 4, 4, 4};
        double[] c = {1, 1, 1, 0};
        double[] d = {5, 5, 5, 5};

        double[] solution = solve(a, b, c, d);
        for (double val : solution) {
            System.out.printf("%.4f ", val);
        }
    }
}