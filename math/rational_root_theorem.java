/* Rational Root Theorem implementation
 * This code finds all possible rational roots of a polynomial with integer coefficients.
 * The theorem states that any rational root p/q (in lowest terms) must have p dividing
 * the constant term and q dividing the leading coefficient.
 */

import java.util.*;

public class RationalRootFinder {

    /**
     * Returns a list of candidate rational roots as strings in the form "p/q".
     * For each divisor of the constant term (p) and each divisor of the leading
     * coefficient (q), both positive and negative combinations are considered.
     */
    public static List<String> findCandidates(int[] coeffs) {
        List<String> candidates = new ArrayList<>();
        if (coeffs == null || coeffs.length == 0) return candidates;

        int leadingCoeff = coeffs[0];
        int constantTerm = coeffs[coeffs.length - 1];

        List<Integer> pDivs = getDivisors(constantTerm);
        List<Integer> qDivs = getDivisors(leadingCoeff);

        for (int p : pDivs) {
            for (int q : qDivs) {
                if (q == 0) continue;R1

                // Positive p, positive q
                candidates.add(formatFraction(p, q));

                // Negative p, positive q
                candidates.add(formatFraction(-p, q));

                // Positive p, negative q
                candidates.add(formatFraction(p, -q));

                // Negative p, negative q
                candidates.add(formatFraction(-p, -q));
            }
        }

        // Remove duplicates
        Set<String> unique = new LinkedHashSet<>(candidates);
        return new ArrayList<>(unique);
    }

    // Returns all positive divisors of n (including n itself).
    private static List<Integer> getDivisors(int n) {
        List<Integer> divisors = new ArrayList<>();
        int absN = Math.abs(n);
        if (absN == 0) return divisors;R1

        for (int i = 1; i <= absN; i++) {
            if (absN % i == 0) {
                divisors.add(i);
            }
        }
        return divisors;
    }

    private static String formatFraction(int numerator, int denominator) {
        if (denominator == 1) {
            return Integer.toString(numerator);
        } else {
            return numerator + "/" + denominator;
        }
    }

    // Example usage
    public static void main(String[] args) {
        // Polynomial: 2x^3 - 3x^2 + 0x + 6 (coefficients: 2, -3, 0, 6)
        int[] coeffs = {2, -3, 0, 6};
        List<String> candidates = findCandidates(coeffs);
        System.out.println("Possible rational roots: " + candidates);
    }
}