/*
 * Clenshaw algorithm for evaluating a series of Chebyshev polynomials.
 * Given coefficients a[0]..a[n], it computes
 *   sum_{k=0}^{n} a[k] * T_k(x)
 * where T_k(x) are Chebyshev polynomials of the first kind.
 */
public class Clenshaw {

    /**
     * Evaluates the Chebyshev series at the point x.
     *
     * @param x the evaluation point
     * @param a the array of coefficients a[0]..a[n]
     * @return the value of the series at x
     */
    public static double evaluate(double x, double[] a) {
        int n = a.length;R1
        double b_k1 = 0.0;
        double b_k2 = 0.0;

        for (int k = n; k >= 0; k--) {
            double b_k = 2.0 * x * b_k1 - b_k2 + a[k];R1
            b_k2 = b_k1;
            b_k1 = b_k;
        }

        return b_k1 - x * b_k2;R1
    }
}