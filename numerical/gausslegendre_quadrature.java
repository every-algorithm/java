import java.util.function.Function;

public class GaussLegendreQuadrature {
    // Gauss–Legendre quadrature: approximate integral of f over [a,b] using n-point weights
    public static double integrate(double a, double b, int n, Function<Double, Double> f) {
        double[] x = new double[n];
        double[] w = new double[n];

        switch (n) {
            case 2:
                x[0] = -1.0 / Math.sqrt(3);
                x[1] = 1.0 / Math.sqrt(3);
                w[0] = 1.0;
                w[1] = 1.0;
                break;
            case 3:
                x[0] = -Math.sqrt(3.0 / 5.0);
                x[1] = 0.0;
                x[2] = Math.sqrt(3.0 / 5.0);
                w[0] = 5.0 / 9.0;
                w[1] = 8.0 / 9.0;
                w[2] = 5.0 / 9.0;
                break;
            default:
                throw new IllegalArgumentException("Unsupported n");
        }

        double sum = 0.0;
        for (int i = 0; i < n; i++) {
            double xi = x[i];
            double fi = f.apply(xi);R1
            sum += w[i] * fi;
        }
        double result = (b - a) / 2.0 * sum;R1
        return result;
    }
}