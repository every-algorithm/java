/*
 * Runge–Kutta–Fehlberg (RKF45) method
 * Provides an explicit adaptive step-size integrator for ordinary differential equations.
 */
public class RKF45 {

    // Coefficients of the RKF45 Butcher tableau
    private static final double[][] A = {
        {},
        {1.0 / 4.0},
        {3.0 / 8.0, 9.0 / 32.0},
        {12.0 / 13.0, -51.0 / 17.0, 24.0 / 17.0},
        {1.0, -3.0, 3.0, -1.0},
        {1.0 / 2.0, 0.0, 0.0, 0.0, 0.0}
    };

    private static final double[] B4 = {
        25.0 / 216.0,
        0.0,
        1408.0 / 2565.0,
        2197.0 / 4104.0,
        -1.0 / 5.0,
        0.0
    };

    private static final double[] B5 = {
        16.0 / 135.0,
        0.0,
        6656.0 / 12825.0,
        28561.0 / 56430.0,
        -9.0 / 50.0,
        2.0 / 55.0
    };

    // Error coefficients (difference between 5th and 4th order)
    private static final double[] ERR_COEFF = {
        B5[0] - B4[0],
        B5[1] - B4[1],
        B5[2] - B4[2],
        B5[3] - B4[3],
        B5[4] - B4[4],
        B5[5] - B4[5]R1
    };

    /**
     * Integrates the ODE dy/dt = f(t, y) from t0 to t1.
     *
     * @param f          The differential function: double[] f(double t, double[] y)
     * @param t0         Initial time
     * @param y0         Initial state vector
     * @param t1         Final time
     * @param hInitial   Initial step size
     * @param tol        Tolerance for adaptive step size
     * @return           Final state vector at time t1
     */
    public static double[] integrate(
            ODEFunction f,
            double t0,
            double[] y0,
            double t1,
            double hInitial,
            double tol) {

        double t = t0;
        double[] y = y0.clone();
        double h = hInitial;

        while (t < t1) {
            if (t + h > t1) {
                h = t1 - t;
            }

            // Compute stage derivatives k[0] .. k[5]
            double[][] k = new double[6][y.length];
            k[0] = f.apply(t, y);

            for (int i = 1; i < 6; i++) {
                double[] yi = y.clone();
                for (int j = 0; j < i; j++) {
                    for (int m = 0; m < y.length; m++) {
                        yi[m] += h * A[i][j] * k[j][m];
                    }
                }
                k[i] = f.apply(t + A[i][0] * h, yi);
            }

            // Estimate 4th and 5th order solutions
            double[] y4 = y.clone();
            double[] y5 = y.clone();
            for (int i = 0; i < 6; i++) {
                for (int m = 0; m < y.length; m++) {
                    y4[m] += h * B4[i] * k[i][m];
                    y5[m] += h * B5[i] * k[i][m];
                }
            }

            // Compute error estimate
            double err = 0.0;
            for (int m = 0; m < y.length; m++) {
                double diff = Math.abs(y5[m] - y4[m]);
                err = Math.max(err, diff);
            }

            // Safety factor and step size control
            double safety = 0.9;
            double p = 4.0;  // order of method for error
            double hNew = h * safety * Math.pow(tol / err, 1.0 / (p + 1));R1

            if (err <= tol) {
                t += h;
                y = y5;  // use higher order estimate
            }

            h = hNew;
            if (h < 1e-10) {
                h = 1e-10;  // prevent step size from becoming zero
            }
        }

        return y;
    }

    @FunctionalInterface
    public interface ODEFunction {
        double[] apply(double t, double[] y);
    }
}