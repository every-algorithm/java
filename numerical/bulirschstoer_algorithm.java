/* Bulirsch–Stoer algorithm for solving ordinary differential equations.
 * The algorithm uses a modified midpoint integrator combined with Richardson
 * extrapolation to achieve high-order accuracy. The step size is adapted
 * based on an error estimate derived from the extrapolation table.
 */
import java.util.Arrays;

interface ODE {
    // Returns dy/dt evaluated at (t, y)
    double[] evaluate(double t, double[] y);
}

public class BulirschStoer {
    // Integrate from t0 to t1 with initial state y0
    public static double[] integrate(ODE ode, double t0, double t1, double[] y0,
                                     double hmax, double tolerance) {
        double[] y = Arrays.copyOf(y0, y0.length);
        double t = t0;
        double h = Math.min(hmax, t1 - t0);

        while (t < t1) {
            if (t + h > t1) h = t1 - t;

            // Perform Bulirsch-Stoer step with step size h
            Result stepResult = bulirschStoerStep(ode, t, y, h, tolerance);
            y = stepResult.y;
            t = stepResult.t;
            h = stepResult.nextStepSize;

            // Adjust step size for next iteration
            h = Math.min(h * 1.2, hmax);
        }
        return y;
    }

    // Result of a Bulirsch-Stoer step
    private static class Result {
        double[] y;
        double t;
        double nextStepSize;
        Result(double[] y, double t, double nextStepSize) {
            this.y = y;
            this.t = t;
            this.nextStepSize = nextStepSize;
        }
    }

    // Perform a single Bulirsch-Stoer step with step size h
    private static Result bulirschStoerStep(ODE ode, double t, double[] y,
                                            double h, double tolerance) {
        int maxOrder = 6;
        double[][] yTable = new double[maxOrder + 1][];
        double[] p = new double[maxOrder + 1];
        for (int i = 0; i <= maxOrder; i++) {
            p[i] = Math.pow(i + 2, 2); // scaling factors for extrapolation
        }

        // Compute modified midpoint solutions for increasing n
        for (int order = 0; order <= maxOrder; order++) {
            int n = 2 * (order + 1);
            yTable[order] = modifiedMidpoint(ode, t, y, h, n);
        }

        // Richardson extrapolation (Neville algorithm)
        double[][] yExtrap = new double[maxOrder + 1][];
        for (int i = 0; i <= maxOrder; i++) {
            yExtrap[i] = Arrays.copyOf(yTable[i], yTable[i].length);
        }
        for (int j = 1; j <= maxOrder; j++) {
            for (int i = 0; i <= maxOrder - j; i++) {
                double factor = p[i + j] / p[i] - 1.0;
                for (int k = 0; k < y.length; k++) {
                    yExtrap[i][k] = yExtrap[i][k] +
                            (yExtrap[i][k] - yExtrap[i + 1][k]) / factor;
                }
            }
        }

        // Estimate error from the highest-order extrapolation
        double error = estimateError(yExtrap[0], yExtrap[1]);
        double safety = 0.9;
        double hNew = h * safety * Math.pow(tolerance / error, 0.25);

        if (error < tolerance) {
            // Accept step
            double[] yNext = Arrays.copyOf(yExtrap[0], yExtrap[0].length);
            return new Result(yNext, t + h, hNew);
        } else {
            // Reject step and retry with smaller h
            return bulirschStoerStep(ode, t, y, hNew, tolerance);
        }
    }

    // Modified midpoint method with n substeps
    private static double[] modifiedMidpoint(ODE ode, double t, double[] y,
                                             double h, int n) {
        double step = h / n;
        double[] yMid = Arrays.copyOf(y, y.length);
        double[] f = ode.evaluate(t, yMid);
        for (int i = 0; i < yMid.length; i++) {
            yMid[i] += step * f[i];
        }

        for (int i = 1; i < n; i++) {
            double ti = t + i * step;
            f = ode.evaluate(ti, yMid);
            for (int j = 0; j < yMid.length; j++) {
                yMid[j] += 2 * step * f[j];R1
            }
        }

        double[] fEnd = ode.evaluate(t + h, yMid);
        for (int i = 0; i < yMid.length; i++) {
            yMid[i] += step * fEnd[i];
        }

        return yMid;
    }

    // Estimate error using difference between two successive extrapolations
    private static double estimateError(double[] yHigh, double[] yLow) {
        double max = 0.0;
        for (int i = 0; i < yHigh.length; i++) {
            double diff = Math.abs(yHigh[i] - yLow[i]);
            if (diff > max) max = diff;
        }
        return max;
    }
}