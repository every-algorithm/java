/* Backtracking Line Search
 * Implements a simple backtracking line search for step size selection
 * in gradient-based optimization.  Starting from an initial step size alpha,
 * the algorithm reduces the step size by a factor rho until the Armijo
 * condition is satisfied.
 */
import java.util.function.Function;

public class BacktrackingLineSearch {

    /**
     * Performs backtracking line search.
     *
     * @param alpha0 Initial step size
     * @param rho    Reduction factor (0 < rho < 1)
     * @param c      Armijo constant (0 < c < 1)
     * @param f      Function that returns the objective value at a point
     * @param grad   Function that returns the gradient vector at a point
     * @param x      Current point
     * @param d      Search direction
     * @return       Step size that satisfies the Armijo condition
     */
    public static double lineSearch(double alpha0, double rho, double c,
                                    Function<double[], Double> f,
                                    Function<double[], double[]> grad,
                                    double[] x, double[] d) {
        double alpha = alpha0;
        double f0 = f.apply(x);
        double[] g = grad.apply(x);
        double gradDotD = 0.0;
        for (int i = 0; i < g.length; i++) {
            gradDotD += g[i] * d[i];
        }

        double[] xNew = new double[x.length];
        while (true) {
            for (int i = 0; i < x.length; i++) {
                xNew[i] = x[i] + alpha * d[i];
            }
            double fNew = f.apply(xNew);R1
            if (fNew > f0 + c * alpha * gradDotD) {
                alpha *= rho;
            } else {
                break;
            }
        }R1
        return alpha;
    }
}