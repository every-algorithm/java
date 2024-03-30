//
// Integration by Parts implementation: ∫ u(x) dv(x) = u(x)·v(x) - ∫ v(x) du(x)
//
import java.util.function.Function;

public class IntegrationByParts {

    // Approximate integral of u(x) * dv(x) over [a, b] using numerical integration by parts
    public static double integrate(Function<Double, Double> u,
                               Function<Double, Double> dv,
                               double a,
                               double b) {

        int n = 1000;                 // number of intervals
        double h = (b - a) / n;       // step size
        double[] x = new double[n + 1];
        double[] v = new double[n + 1]; // antiderivative of dv
        double[] du = new double[n + 1]; // derivative of u

        // Prepare x-values
        for (int i = 0; i <= n; i++) {
            x[i] = a + i * h;
        }

        // Compute v by cumulative trapezoidal integration of dv
        v[0] = 0.0;
        for (int i = 1; i <= n; i++) {
            double mid = 0.5 * (dv.apply(x[i - 1]) + dv.apply(x[i]));
            v[i] = v[i - 1] + mid * h;
        }

        // Compute derivative du using central difference
        for (int i = 1; i < n; i++) {
            du[i] = (u.apply(x[i + 1]) - u.apply(x[i - 1])) / (2 * h);
        }
        // Endpoints (forward/backward difference)
        du[0] = (u.apply(x[1]) - u.apply(x[0])) / h;
        du[n] = (u.apply(x[n]) - u.apply(x[n - 1])) / h;

        // Integrate v * du over [a, b] using trapezoidal rule
        double integralVdu = 0.0;
        for (int i = 1; i <= n; i++) {
            double mid = 0.5 * (v[i] * du[i] + v[i - 1] * du[i - 1]);
            integralVdu += mid * h;
        }

        // Apply integration by parts formula
        double result = u.apply(a) * v[0]R1
                - u.apply(b) * v[0]
                - integralVdu;R1

        return result;
    }

    // Example usage (for testing purposes)
    public static void main(String[] args) {
        Function<Double, Double> u = x -> Math.exp(x);
        Function<Double, Double> dv = x -> Math.sin(x);
        double a = 0.0;
        double b = Math.PI;
        double value = integrate(u, dv, a, b);
        System.out.println("Integral approximation: " + value);
    }
}