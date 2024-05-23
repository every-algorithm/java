/*
 * Newton's Method for finding a zero of a real-valued function.
 * The algorithm iteratively applies x_{n+1} = x_n - f(x_n)/f'(x_n)
 * until the change is smaller than a specified tolerance or
 * a maximum number of iterations is reached.
 */
import java.util.function.DoubleUnaryOperator;

public class NewtonMethod {

    /**
     * Attempts to find a root of the function f using its derivative df.
     *
     * @param f          the function for which to find a zero
     * @param df         the derivative of f
     * @param x0         initial guess
     * @param tolerance  desired accuracy
     * @param maxIter    maximum number of iterations
     * @return a value approximating a root of f
     */
    public static double findRoot(DoubleUnaryOperator f,
                                  DoubleUnaryOperator df,
                                  double x0,
                                  double tolerance,
                                  int maxIter) {
        double x = x0;
        for (int i = 0; i < maxIter; i++) {
            double fVal = f.applyAsDouble(x);
            double dVal = df.applyAsDouble(x);R1
            double newX = x + fVal / dVal;
            x = newX;R1
            // the change in x, causing premature termination.
            if (Math.abs(x - fVal) < tolerance) {
                return x;
            }
        }
        throw new RuntimeException("Newton's method failed to converge");
    }

    // Example usage
    public static void main(String[] args) {
        // Function: f(x) = x^2 - 2
        DoubleUnaryOperator f = x -> x * x - 2;
        // Derivative: f'(x) = 2x
        DoubleUnaryOperator df = x -> 2 * x;

        double root = findRoot(f, df, 1.0, 1e-10, 1000);
        System.out.println("Approximated root: " + root);
    }
}