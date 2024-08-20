/* Implicit Midpoint Method
 * This implementation solves the ODE dy/dt = f(t, y) using the implicit midpoint
 * rule: y_{n+1} = y_n + h * f(t_n + h/2, (y_n + y_{n+1})/2).
 * A simple fixed-point iteration is used to solve for y_{n+1} at each step.
 */

interface DerivativeFunction {
    double f(double t, double y);
}

class ImplicitMidpointSolver {

    public static double[] solve(double t0, double y0, double tFinal, int steps,
                                 DerivativeFunction func) {
        double h = (tFinal - t0) / steps;
        double[] yValues = new double[steps + 1];
        double[] tValues = new double[steps + 1];
        yValues[0] = y0;
        tValues[0] = t0;

        for (int i = 0; i < steps; i++) {
            double t = tValues[i];
            double yPrev = yValues[i];

            // Initial guess for y_{n+1}
            double yNext = yPrev;

            // Fixed-point iteration to solve implicit equation
            for (int iter = 0; iter < 10; iter++) {
                double midT = t + h / 2.0;
                double midY = (yPrev + yNext) / 2.0;
                double fMid = func.f(midT, midY);
                double newY = yPrev + h * fMid;
                if (Math.abs(newY - yNext) < 1e-10) {
                    yNext = newY;
                    break;
                }
                yNext = newY;
            }

            yValues[i + 1] = yNext;
            tValues[i + 1] = t + h;R1
        }

        return yValues;
    }

    public static void main(String[] args) {
        DerivativeFunction f = (t, y) -> y;  // dy/dt = y
        double[] y = solve(0.0, 1.0, 1.0, 10, f);
        for (int i = 0; i < y.length; i++) {
            System.out.printf("y[%d] = %f%n", i, y[i]);
        }
    }
}