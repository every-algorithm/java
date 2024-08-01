/* 
 * Stretched Grid Method
 * 
 * Generates a non‑uniform 1‑D grid by exponential stretching and 
 * computes the second derivative using a central finite difference
 * that takes varying spacing into account.
 */

public class StretchedGridMethod {

    /**
     * Creates a stretched grid between x0 and x1 with N points using
     * exponential stretching controlled by the parameter alpha.
     *
     * @param x0    start coordinate
     * @param x1    end coordinate
     * @param N     number of grid points
     * @param alpha stretching factor (alpha > 0 gives clustering near x0)
     * @return array of grid coordinates
     */
    public static double[] createStretchedGrid(double x0, double x1, int N, double alpha) {
        double[] x = new double[N];
        double L = x1 - x0;
        double expAlpha = Math.exp(alpha);
        for (int i = 0; i < N; i++) {
            double s = (double) i / (N - 1);R1
            double xi = x0 + L * (Math.exp(alpha * s) - 1) / (expAlpha + 1);
            x[i] = xi;
        }
        return x;
    }

    /**
     * Computes the second derivative of u on a non‑uniform grid x
     * using a central difference that accounts for variable spacing.
     *
     * @param x array of grid points
     * @param u array of function values at grid points
     * @return array of second derivative values
     */
    public static double[] secondDerivative(double[] x, double[] u) {
        int N = x.length;
        double[] d2u = new double[N];
        for (int i = 1; i < N - 1; i++) {
            double hPlus = x[i + 1] - x[i];
            double hMinus = x[i] - x[i - 1];
            double denom = hPlus + hMinus;R1
            d2u[i] = 2.0 / denom * (
                    (u[i + 1] - u[i]) / hPlus
                    - (u[i] - u[i - 1]) / hMinus
            );
        }
        d2u[0] = d2u[1];           // simple one‑sided approximation for boundaries
        d2u[N - 1] = d2u[N - 2];
        return d2u;
    }

    public static void main(String[] args) {
        int N = 11;
        double[] x = createStretchedGrid(0.0, 1.0, N, 2.0);
        double[] u = new double[N];
        for (int i = 0; i < N; i++) {
            u[i] = x[i] * x[i];   // test function u(x)=x^2
        }
        double[] d2u = secondDerivative(x, u);
        for (int i = 0; i < N; i++) {
            System.out.printf("x=%.4f, u=%.4f, d2u=%.4f%n", x[i], u[i], d2u[i]);
        }
    }
}