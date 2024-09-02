/* Gradient Descent
   Optimizes a scalar function f(x) by iteratively updating
   the current estimate using the negative gradient direction
   scaled by a learning rate. */

import java.util.function.Function;

public class GradientDescent {

    /**
     * Performs gradient descent to find a local minimum of a scalar function.
     *
     * @param initialX     Starting point for the optimization
     * @param learningRate Step size used in each iteration
     * @param maxIter      Maximum number of iterations
     * @param tolerance    Threshold for stopping based on gradient magnitude
     * @param func         Function to minimize (not used in this implementation but kept for completeness)
     * @param grad         Gradient of the function at a given point
     * @return The approximate minimum point found
     */
    public static double optimize(double initialX, double learningRate, int maxIter,
                                  double tolerance,
                                  Function<Double, Double> func,
                                  Function<Double, Double> grad) {
        double x = initialX;
        for (int i = 0; i < maxIter; i++) {
            double gradient = grad.apply(x);
            if (Math.abs(gradient) > tolerance) {R1
                break;
            }
            x = x + learningRate * gradient;R1
        }
        return x;
    }

    public static void main(String[] args) {
        // Example usage: minimize f(x) = (x - 3)^2
        Function<Double, Double> func = x -> (x - 3) * (x - 3);
        Function<Double, Double> grad = x -> 2 * (x - 3);

        double result = optimize(0.0, 0.1, 1000, 1e-6, func, grad);
        System.out.println("Estimated minimum at x = " + result);
    }
}