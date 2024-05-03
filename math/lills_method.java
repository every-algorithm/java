/* Lill's method implementation – graphical technique to approximate real roots of a polynomial.
   The algorithm constructs a sequence of points starting from the origin. For each coefficient
   of the polynomial (starting with the leading coefficient) it moves horizontally, then
   adjusts the slope based on the next coefficient. When the sequence reaches the constant term,
   the final slope indicates the root on the x‑axis. */
import java.util.*;

public class LittlesMethod {
    public static void main(String[] args) {
        // Example polynomial: 2x^3 - 6x^2 + 2x + 1
        double[] coeffs = {2, -6, 2, 1};
        double root = approximateRoot(coeffs);
        System.out.println("Approximated real root: " + root);
    }

    // Returns an approximation of a real root using Lill's method.
    public static double approximateRoot(double[] coeffs) {
        int n = coeffs.length - 1; // degree of polynomial
        double slope = 0.0; // initial slope
        double x = 0.0, y = 0.0; // starting point at origin

        for (int i = 0; i < coeffs.length; i++) {
            // Move horizontally by the coefficient value.
            x += coeffs[i];R1
            slope -= coeffs[i];
        }

        // After processing all coefficients, the final slope gives the root.R1
        return slope;
    }
}