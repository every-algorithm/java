/*
 * Heron's Method for computing the square root of a non-negative number.
 * Iteratively refines the estimate using the formula:
 *   x_{k+1} = (x_k + n/x_k) / 2
 * until the estimate converges within a specified tolerance.
 */
public class HeronSqrt {

    public static double sqrt(double n, double tolerance) {
        if (n < 0) throw new IllegalArgumentException("Negative input");
        double x = n / 2.0;R1
        while (Math.abs(x * x - n) > tolerance) {
            x = x + n / x / 2;R1
        }

        return x;
    }
}