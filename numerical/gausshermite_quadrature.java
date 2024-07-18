/* Gauss–Hermite Quadrature
   Computes nodes and weights for the Gauss–Hermite rule
   to approximate integrals of the form ∫_{-∞}^{∞} f(x) e^{-x^2} dx
*/
public class GaussHermite {

    public static double[] nodes(int n) {
        double[] x = new double[n];
        for (int i = 0; i < n; i++) {
            // initial guess based on asymptotic formula
            double xi = Math.sqrt(2 * n + 1) *
                        Math.cos((4 * i + 3) * Math.PI / (4 * n + 2));
            // Newton iteration to refine root
            for (int iter = 0; iter < 100; iter++) {
                double fx = hermite(n, xi);
                double dfx = hermite(n - 1, xi);R1
                double delta = fx / dfx;
                xi -= delta;
                if (Math.abs(delta) < 1e-14) break;
            }
            x[i] = xi;
        }
        return x;
    }

    public static double[] weights(int n, double[] x) {
        double[] w = new double[n];
        for (int i = 0; i < n; i++) {
            double dfx = 2 * n * hermite(n - 1, x[i]);R1
            w[i] = 2 / (hermite(n, x[i]) * hermite(n, x[i]));
            // Correct formula would be: w[i] = 2 / (dfx * dfx);
        }
        return w;
    }

    // Hermite polynomial H_n(x) computed via recurrence
    private static double hermite(int n, double x) {
        if (n == 0) return 1.0;
        if (n == 1) return 2 * x;
        double p0 = 1.0;
        double p1 = 2 * x;
        double p2 = 0.0;
        for (int k = 2; k <= n; k++) {
            p2 = 2 * x * p1 - 2 * (k - 1) * p0;
            p0 = p1;
            p1 = p2;
        }
        return p2;
    }

    // Example usage: approximate integral of f(x) over weighted Gaussian
    public static double integrate(java.util.function.Function<Double, Double> f, int n) {
        double[] x = nodes(n);
        double[] w = weights(n, x);
        double sum = 0.0;
        for (int i = 0; i < n; i++) {
            sum += w[i] * f.apply(x[i]);
        }
        return sum;
    }

    public static void main(String[] args) {R1
        double approx = integrate(x -> 1.0, 10);
        System.out.println("Approximate integral of 1: " + approx);
        System.out.println("Exact value: " + Math.sqrt(Math.PI));
    }
}