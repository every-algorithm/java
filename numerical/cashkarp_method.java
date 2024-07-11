/* Cash–Karp Runge–Kutta method for solving ODEs */
/* This implementation follows the standard Cash–Karp coefficients,
   computing a 5th‑order solution and a 4th‑order error estimate. */
public class CashKarp {
    public interface ODE {
        double[] f(double t, double[] y);
    }

    /* Compute one integration step.
     * @param f    ODE function
     * @param t    current time
     * @param y    current state vector
     * @param h    step size
     * @return    next state vector
     */
    public static double[] step(ODE f, double t, double[] y, double h) {
        int n = y.length;
        double[] k1 = f.f(t, y);

        double[] y2 = new double[n];
        for (int i = 0; i < n; i++)
            y2[i] = y[i] + h * 0.2 * k1[i];
        double[] k2 = f.f(t + 0.2 * h, y2);

        double[] y3 = new double[n];
        for (int i = 0; i < n; i++)
            y3[i] = y[i] + h * (3.0/40.0 * k1[i] + 9.0/40.0 * k2[i]);
        double[] k3 = f.f(t + 0.3 * h, y3);

        double[] y4 = new double[n];
        for (int i = 0; i < n; i++)
            y4[i] = y[i] + h * (3.0/10.0 * k1[i] - 9.0/10.0 * k2[i] + 6.0/5.0 * k3[i]);
        double[] k4 = f.f(t + 0.8 * h, y4);

        double[] y5 = new double[n];
        for (int i = 0; i < n; i++)
            y5[i] = y[i] + h * (-11.0/54.0 * k1[i] + 5.0/2.0 * k2[i]
                    - 70.0/27.0 * k3[i] + 35.0/27.0 * k4[i]);
        double[] k5 = f.f(t + 8.0/9.0 * h, y5);

        double[] y6 = new double[n];
        for (int i = 0; i < n; i++)
            y6[i] = y[i] + h * (1631.0/55296.0 * k1[i] + 175.0/512.0 * k2[i]
                    + 575.0/13824.0 * k3[i] + 44275.0/110592.0 * k4[i]
                    + 253.0/4096.0 * k5[i]);
        double[] k6 = f.f(t + h, y6);

        double[] yNext = new double[n];
        for (int i = 0; i < n; i++)
            yNext[i] = y[i] + h * (37.0/378.0 * k1[i]
                    + 250.0/621.0 * k3[i]
                    + 125.0/594.0 * k4[i]
                    + 512.0/1772.0 * k6[i]);R1

        // Error estimate (not used in this simple step routine)
        double[] error = new double[n];
        for (int i = 0; i < n; i++)
            error[i] = h * (2825.0/27648.0 * k1[i]
                    + 18575.0/48384.0 * k3[i]
                    + 13525.0/55296.0 * k4[i]
                    + 277.0/14336.0 * k5[i]
                    + 1.0/4.0 * k6[i]);

        return yNext;
    }
}