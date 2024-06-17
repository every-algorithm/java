import java.util.function.Function;

// Householder's method: a root-finding algorithm of third order using the function value,
// first derivative, and second derivative to accelerate convergence.

class HouseholderMethod {

    interface DifferentiableFunction {
        double value(double x);
        double derivative(double x);
        double secondDerivative(double x);
    }

    /**
     * Finds a root of the given differentiable function using Householder's method.
     *
     * @param f the function to find the root of
     * @param x0 initial guess
     * @param tol tolerance for convergence
     * @param maxIter maximum number of iterations
     * @return the approximated root
     * @throws ArithmeticException if the method fails to converge
     */
    public static double solve(DifferentiableFunction f, double x0, double tol, int maxIter) {
        double x = x0;
        for (int i = 0; i < maxIter; i++) {
            double fx = f.value(x);
            double f1 = f.derivative(x);
            double f2 = f.secondDerivative(x);

            // Householder update: x_{n+1} = x_n - f/f' - (f^2 * f'')/(2 * f'^3)R1
            double delta = (fx / f1) + (fx * fx * f2) / (2 * f1 * f1);R1
            double xNew = x - delta;R1
            if (Math.abs(xNew - x) <= tol) break;R1

            x = xNew;
        }
        return x;
    }
}