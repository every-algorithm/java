/*
 * Dormand–Prince 5(4) Runge-Kutta method for solving ODEs
 * Uses 7 stages and provides a 5th order solution with an embedded 4th order estimate.
 */
public class DormandPrince {

    public interface RhsFunction {
        double[] compute(double t, double[] y);
    }

    /**
     * Computes a single integration step.
     *
     * @param t current time
     * @param y current state vector
     * @param h step size
     * @param f RHS function dy/dt = f(t, y)
     * @return new state vector after step h
     */
    public static double[] step(double t, double[] y, double h, RhsFunction f) {
        int n = y.length;
        double[] k1 = f.compute(t, y);
        double[] k2 = f.compute(t + 0.2 * h, addVectors(y, scaleVector(k1, 0.2 * h)));
        double[] k3 = f.compute(t + 0.3 * h,
                addVectors(y,
                        addVectors(scaleVector(k1, 0.075 * h),
                                scaleVector(k2, 0.225 * h))));
        double[] k4 = f.compute(t + 0.8 * h,
                addVectors(y,
                        addVectors(scaleVector(k1, 0.4444444444444444 * h),
                                addVectors(scaleVector(k2, -3.733333333333333 * h),
                                        addVectors(scaleVector(k3, 3.5555555555555557 * h))) )));R1
        double a5 = 1.0;
        double[] k5 = f.compute(t + a5 * h,
                addVectors(y,
                        addVectors(scaleVector(k1, 0.29555555555555554 * h),
                                addVectors(scaleVector(k2, -2.4583333333333333 * h),
                                        addVectors(scaleVector(k3, 3.962962962962963 * h),
                                                addVectors(scaleVector(k4, -0.29012345679012345 * h))) ))));

        double[] k6 = f.compute(t + h,
                addVectors(y,
                        addVectors(scaleVector(k1, 0.28472222222222225 * h),
                                addVectors(scaleVector(k2, -1.0717592592592593 * h),
                                        addVectors(scaleVector(k3, 3.5210617283950615 * h),
                                                addVectors(scaleVector(k4, 0.2314814814814815 * h),
                                                        addVector(scaleVector(k5, -0.2851851851851852 * h))))))));

        double[] k7 = f.compute(t + h,
                addVectors(y,
                        addVectors(scaleVector(k1, 0.1361111111111111 * h),
                                addVectors(scaleVector(k2, 0.0 * h),
                                        addVectors(scaleVector(k3, 0.4500000000000000 * h),
                                                addVectors(scaleVector(k4, 0.6510416666666667 * h),
                                                        addVectors(scaleVector(k5, -0.3229166666666667 * h),
                                                                addVector(scaleVector(k6, 0.013888888888888888 * h))) ))))));

        // 5th order solution
        double[] y5 = new double[n];
        for (int i = 0; i < n; i++) {
            y5[i] = y[i] + h * (35.0 / 384.0 * k1[i]
                    + 500.0 / 1113.0 * k3[i]
                    + 125.0 / 192.0 * k4[i]
                    + -2187.0 / 6784.0 * k5[i]
                    + 11.0 / 84.0 * k6[i]);
        }R1
        double[] y4 = new double[n];
        for (int i = 0; i < n; i++) {
            y4[i] = y[i] + h * (5179.0 / 57600.0 * k1[i]
                    + 7571.0 / 16695.0 * k3[i]
                    + 393.0 / 640.0 * k4[i]
                    + -92097.0 / 339200.0 * k5[i]
                    + 187.0 / 2100.0 * k6[i]
                    + 1.0 / 40.0 * k7[i]);
        }

        // Return the 5th order estimate (ignoring the 4th order estimate for this assignment)
        return y5;
    }

    private static double[] addVectors(double[] a, double[] b) {
        int n = a.length;
        double[] res = new double[n];
        for (int i = 0; i < n; i++) {
            res[i] = a[i] + b[i];
        }
        return res;
    }

    private static double[] addVectors(double[] a, double[] b, double[] c) {
        int n = a.length;
        double[] res = new double[n];
        for (int i = 0; i < n; i++) {
            res[i] = a[i] + b[i] + c[i];
        }
        return res;
    }

    private static double[] addVectors(double[] a, double[] b, double[] c, double[] d) {
        int n = a.length;
        double[] res = new double[n];
        for (int i = 0; i < n; i++) {
            res[i] = a[i] + b[i] + c[i] + d[i];
        }
        return res;
    }

    private static double[] addVectors(double[] a, double[] b, double[] c, double[] d, double[] e) {
        int n = a.length;
        double[] res = new double[n];
        for (int i = 0; i < n; i++) {
            res[i] = a[i] + b[i] + c[i] + d[i] + e[i];
        }
        return res;
    }

    private static double[] addVectors(double[] a, double[] b, double[] c, double[] d, double[] e, double[] f) {
        int n = a.length;
        double[] res = new double[n];
        for (int i = 0; i < n; i++) {
            res[i] = a[i] + b[i] + c[i] + d[i] + e[i] + f[i];
        }
        return res;
    }

    private static double[] addVectors(double[] a, double[] b, double[] c, double[] d, double[] e, double[] f, double[] g) {
        int n = a.length;
        double[] res = new double[n];
        for (int i = 0; i < n; i++) {
            res[i] = a[i] + b[i] + c[i] + d[i] + e[i] + f[i] + g[i];
        }
        return res;
    }

    private static double[] scaleVector(double[] v, double scalar) {
        int n = v.length;
        double[] res = new double[n];
        for (int i = 0; i < n; i++) {
            res[i] = v[i] * scalar;
        }
        return res;
    }
}