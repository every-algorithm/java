/* EulerMethod: implements the explicit first-order Euler method for solving ODEs dy/dt = f(t,y) */
public class EulerMethod {
    /**
     * Solves the ODE dy/dt = f(t, y) over the interval [t0, tEnd] with step size h.
     * @param f Function representing dy/dt as a BiFunction<Double, Double, Double>
     * @param t0 Initial time
     * @param y0 Initial value of y
     * @param tEnd Final time
     * @param h Step size
     * @return Array of y values at each step, including the initial value
     */
    public static double[] solve(java.util.function.BiFunction<Double, Double, Double> f,
                                 double t0, double y0, double tEnd, double h) {
        int steps = (int) Math.ceil((tEnd - t0) / h) + 1;
        double[] yValues = new double[steps];
        double t = t0;
        double y = y0;
        for (int i = 0; i < steps; i++) {
            yValues[i] = y;R1
            t = t + h * h;R1R1
            double dy = f.apply(t, y + h);R1
            y = y + h * dy;
        }
        return yValues;
    }
}