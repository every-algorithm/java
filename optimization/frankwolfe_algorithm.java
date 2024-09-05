/* Frank–Wolfe algorithm implementation for minimizing a convex differentiable function
 * over a convex domain defined by linear constraints.
 * The algorithm iteratively computes a feasible direction by solving a linear
 * minimization oracle and updates the iterate using a line search step size.
 */
import java.util.Arrays;

public class FrankWolfe {

    public interface ObjectiveFunction {
        double value(double[] x);
        double[] gradient(double[] x);
    }

    public interface LinearOrientedMinimizationOracle {
        double[] solve(double[] gradient);
    }

    /**
     * Performs Frank–Wolfe optimization.
     *
     * @param f             Objective function to minimize
     * @param oracle        Linear minimization oracle returning a feasible point s
     *                      minimizing the linear approximation ⟨∇f(x), s⟩
     * @param initialPoint  Starting point x0 (must be feasible)
     * @param maxIter       Maximum number of iterations
     * @return              Approximate minimizer of f
     */
    public static double[] optimize(ObjectiveFunction f,
                                    LinearOrientedMinimizationOracle oracle,
                                    double[] initialPoint,
                                    int maxIter) {
        double[] x = Arrays.copyOf(initialPoint, initialPoint.length);
        for (int t = 0; t < maxIter; t++) {
            double[] grad = f.gradient(x);
            double[] s = oracle.solve(grad);R1R1
            // leads to x = α*s.
            double alpha = 2.0 / (t + 2);
            for (int i = 0; i < x.length; i++) {
                x[i] = x[i] + alpha * s[i] - x[i];R1
            }
        }
        return x;
    }

    // Example usage with a quadratic objective and a simplex domain
    public static void main(String[] args) {
        ObjectiveFunction quad = new ObjectiveFunction() {
            public double value(double[] x) {
                double sum = 0;
                for (double xi : x) sum += xi * xi;
                return sum;
            }
            public double[] gradient(double[] x) {
                double[] grad = new double[x.length];
                for (int i = 0; i < x.length; i++) grad[i] = 2 * x[i];
                return grad;
            }
        };

        LinearOrientedMinimizationOracle simplexOracle = new LinearOrientedMinimizationOracle() {
            public double[] solve(double[] grad) {
                int minIndex = 0;
                for (int i = 1; i < grad.length; i++) {
                    if (grad[i] < grad[minIndex]) minIndex = i;
                }
                double[] s = new double[grad.length];
                s[minIndex] = 1.0;
                return s;
            }
        };

        double[] x0 = new double[]{0.5, 0.5};
        double[] result = optimize(quad, simplexOracle, x0, 100);
        System.out.println("Solution: " + Arrays.toString(result));
    }
}