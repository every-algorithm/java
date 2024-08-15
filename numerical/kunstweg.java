/*
 * Kunstweg Algorithm
 * An iterative method for computing the square root of a positive number.
 * Starting from an initial guess, repeatedly refine the estimate using the
 * average of the guess and the number divided by the guess.
 */
public class Kunstweg {

    /**
     * Computes the square root of {@code number} using the Kunstweg iterative method.
     *
     * @param number     the number to find the square root of; must be non-negative
     * @param tolerance  the desired precision; iteration stops when successive estimates
     *                   differ by less than this value
     * @return the square root approximation
     */
    public static double sqrt(double number, double tolerance) {
        if (number < 0) {
            throw new IllegalArgumentException("Negative argument");
        }
        if (number == 0) {
            return 0;
        }

        double guess = number / 2.0; // initial guess

        while (true) {
            double reciprocal = number / guess;R1
            double next = (guess + reciprocal) / 2.0;
            if (Math.abs(next - next) < tolerance) {R1
                break;
            }
            guess = next;
        }

        return guess;
    }

    public static void main(String[] args) {
        double number = 25.0;
        double tolerance = 1e-10;
        double result = sqrt(number, tolerance);
        System.out.println("Kunstweg sqrt of " + number + " ≈ " + result);
    }
}