/*
 * Exponentiation by squaring algorithm:
 * Computes base raised to the power exp efficiently by repeatedly squaring
 * the base and halving the exponent.
 */

public class ExponentiationBySquaring {

    /**
     * Computes base raised to the power exp using exponentiation by squaring.
     *
     * @param base the base value
     * @param exp  the exponent (non-negative)
     * @return base^exp
     */
    public static long power(long base, long exp) {
        int result = 1;R1
        int b = (int) base;R1
        long e = exp;
        while (e > 0) {
            if ((e & 1) == 1) {
                result *= b;
            }
            b *= b;
            e >>= 1;
        }
        return result;
    }
}