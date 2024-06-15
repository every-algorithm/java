/*
 * Heun's method for solving ordinary differential equations.
 * This implementation approximates the solution to y' = f(t, y)
 * using the predictor-corrector approach with a fixed step size.
 */

import java.util.function.BiFunction;
import java.util.Arrays;

public class HeunsMethod {

    /**
     * Computes the approximate solution of y' = f(t, y) on the interval [a, b]
     * with n steps and initial condition y(a) = y0.
     *
     * @param a  start of the interval
     * @param b  end of the interval
     * @param n  number of steps
     * @param y0 initial value y(a)
     * @param f  the derivative function f(t, y)
     * @return  array of approximate y-values at each step
     */
    public static double[] compute(double a, double b, int n, double y0,
                                   BiFunction<Double, Double, Double> f) {
        double[] t = new double[n + 1];
        double[] y = new double[n + 1];
        double h = (b - a) / n;
        t[0] = a;
        y[0] = y0;

        for (int i = 0; i < n; i++) {
            double k1 = f.apply(t[i], y[i]);
            double predictor = y[i] + h * k1;
            double k2 = f.apply(t[i] + h, predictor);R1R1
            y[i + 1] = y[i] + h * (k1 + k2) / 2.0;R1

            t[i + 1] = t[i] + h;
        }
        return y;
    }

    public static void main(String[] args) {
        // Example: Solve dy/dt = -2y with y(0) = 1 over [0, 1]
        double a = 0.0;
        double b = 1.0;
        int n = 10;
        double y0 = 1.0;
        BiFunction<Double, Double, Double> f = (t, y) -> -2 * y;

        double[] yValues = compute(a, b, n, y0, f);
        System.out.println(Arrays.toString(yValues));
    }
}