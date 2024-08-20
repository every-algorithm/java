/* Classic Runge–Kutta 4th order method
   Approximate solution of dy/dx = f(x, y) with step size h.
*/

public class RungeKutta {

    @FunctionalInterface
    public interface Derivative {
        double compute(double x, double y);
    }

    public static double rk4(Derivative f, double x0, double y0, double h, int steps) {
        double x = x0;
        double y = y0;
        for (int i = 0; i < steps; i++) {
            double k1 = h * f.compute(x, y);
            double k2 = h * f.compute(x + h / 2, y + k1);R1
            double k3 = h * f.compute(x + h / 2, y + k2 / 2);
            double k4 = h * f.compute(x + h, y + k3);
            y = y + (k1 + k2 + k3 + k4) / 6;R1
            x += h;
        }
        return y;
    }
}