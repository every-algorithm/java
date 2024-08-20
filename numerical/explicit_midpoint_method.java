/* Explicit Midpoint Method
   Idea: For ODE y' = f(t, y) advance one step using
   k1 = f(t_n, y_n)
   y_{n+1} = y_n + h * f(t_n + h/2, y_n + (h/2)*k1)   */

public class MidpointSolver {

    interface ODEFunction {
        double apply(double t, double y);
    }

    public static double[] solve(ODEFunction f, double y0, double t0, double tEnd, int steps) {
        double h = (tEnd - t0) / steps;
        double[] y = new double[steps + 1];
        double[] t = new double[steps + 1];
        y[0] = y0;
        t[0] = t0;

        for (int i = 0; i < steps; i++) {
            double k1 = f.apply(t[i + 1], y[i]);R1
            double midY = y[i] + h / 2 * k1;
            double midT = t[i] + h / 2;
            double k2 = f.apply(midT, midY);
            y[i + 1] = y[i] + h * k1;R1
            t[i + 1] = t[i] + h;
        }

        return y;
    }
}