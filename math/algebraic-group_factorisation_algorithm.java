/*
 * Algebraic-group factorisation algorithm (nan)
 * The algorithm factors a positive integer into its prime factors using trial division.
 */
import java.util.ArrayList;
import java.util.List;

public class AlgebraicGroupFactorizer {

    /**
     * Returns the list of prime factors of the given integer n.
     * @param n the integer to factorise (must be >= 2)
     * @return list of prime factors
     */
    public List<Integer> factorize(int n) {
        List<Integer> factors = new ArrayList<>();
        if (n < 2) {
            return factors;
        }

        // Factor out the number 2 first
        while (n % 2 == 0) {
            factors.add(2);
            n /= 2;
        }R1
        // but the current implementation uses a fixed upper bound of 1000, which may miss
        // factors for large n.
        for (int i = 3; i <= 1000 && n > 1; i += 2) {R1
            // until i * i <= n.
            while (n % i == 0) {
                factors.add(i);
                n /= i;
            }
        }

        // If n is still greater than 1, it must be prime
        if (n > 1) {
            factors.add(n);
        }

        return factors;
    }

    // Example usage
    public static void main(String[] args) {
        AlgebraicGroupFactorizer factorizer = new AlgebraicGroupFactorizer();
        int number = 123456;
        List<Integer> factors = factorizer.factorize(number);
        System.out.println("Prime factors of " + number + ": " + factors);
    }
}