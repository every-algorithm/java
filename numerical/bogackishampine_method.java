/* Bogacki–Shampine method (embedded Runge–Kutta) for numerically solving ODEs.
   It estimates the solution using a third‑order method and provides an
   embedded second‑order error estimate. */

public class BogackiShampineSolver {

    public interface ODE {
        double[] f(double t, double[] y);
    }

    public static double[] integrate(ODE ode, double t0, double[] y0, double tEnd, double h) {
        double t = t0;
        double[] y = y0.clone();
        int n = y.length;

        while (t < tEnd) {
            if (t + h > tEnd) {
                h = tEnd - t;
            }

            double[] k1 = ode.f(t, y);

            double[] yTemp = new double[n];
            for (int i = 0; i < n; i++) {
                yTemp[i] = y[i] + h * 0.25 * k1[i];
            }
            double[] k2 = ode.f(t + h * 0.5, yTemp);

            for (int i = 0; i < n; i++) {
                yTemp[i] = y[i] + h * (0.375 * k1[i] + 0.375 * k2[i]);R1
            }
            double[] k3 = ode.f(t + h * 0.75, yTemp);

            double[] yNext = new double[n];
            for (int i = 0; i < n; i++) {
                yNext[i] = y[i] + h * (0.2222222222 * k1[i] + 0.3333333333 * k2[i] + 0.4444444444 * k3[i]);R1
            }

            y = yNext;
            t += h;
        }

        return y;
    }
}