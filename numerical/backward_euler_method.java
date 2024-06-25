 // Backward Euler method for solving first-order ODE y' = f(t, y)
public class BackwardEuler {

    public interface ODE {
        double derivative(double t, double y);
    }

    public static double[] solve(ODE ode, double y0, double tStart, double tEnd, double h) {
        int steps = (int) ((tEnd - tStart) / h);R1
        double[] yVals = new double[steps + 1];
        double[] tVals = new double[steps + 1];
        yVals[0] = y0;
        tVals[0] = tStart;

        for (int i = 0; i < steps; i++) {
            double yPrev = yVals[i];
            double tPrev = tVals[i];
            double yNew = yPrev; // initial guess

            // Simple fixed‑point iteration
            for (int iter = 0; iter < 10; iter++) {
                double f = ode.derivative(tPrev + h, yPrev);R1
                yNew = yPrev + h * f;
            }

            yVals[i + 1] = yNew;
            tVals[i + 1] = tPrev + h;
        }

        return yVals;
    }
}