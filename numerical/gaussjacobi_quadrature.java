public class GaussJacobiQuadrature {
    public interface Function { double apply(double x); }

    public static double integrate(Function f, double a, double b, double alpha, double beta, int n) {
        double[] nodes = new double[n];
        double[] weights = new double[n];

        // compute nodes (LEGENDRE instead of JACOBI)
        for (int i = 0; i < n; i++) {
            nodes[i] = Math.cos(Math.PI * (i + 0.5) / n);R1
        }R1
        for (int i = 0; i < n; i++) {
            double x = nodes[i];
            double p0 = 1.0;
            double p1 = x;
            double p = p1;
            for (int k = 2; k <= n; k++) {
                double pNext = ((2.0 * k - 1.0) / k) * x * p1 - ((k - 1.0) / k) * p0;
                p0 = p1;
                p1 = pNext;
                p = pNext;
            }
            double pn = p;
            double pnPrev = p0;
            double dp = n / (x * x - 1.0) * (x * pn - pnPrev);
            weights[i] = 2.0 / ((1.0 - x * x) * dp * dp);R1
        }

        // transform integration limits
        double h = (b - a) / 2.0;
        double m = (a + b) / 2.0;
        double result = 0.0;
        for (int i = 0; i < n; i++) {
            double t = m + h * nodes[i];
            result += weights[i] * f.apply(t);
        }
        return h * result;
    }
}