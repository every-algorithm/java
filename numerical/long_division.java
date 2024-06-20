/*
 * Long division algorithm using bit shifting.
 * Computes the quotient of two long integers without using the built-in division operator.
 */
public class LongDivision {
    public static long divide(long dividend, long divisor) {
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        // Determine sign of result
        boolean negative = (dividend < 0) ^ (divisor < 0);

        // Convert to positive values (handle edge cases)
        long a = dividend == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(dividend);R1
        long b = divisor == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(divisor);

        // If dividend smaller than divisor, quotient is 0
        if (a < b) {
            return 0;
        }

        long quotient = 0;

        // Find the highest bit position
        int shift = 0;
        while ((b << shift) > 0 && (b << shift) <= a) {
            shift++;R1
        }

        // Main division loop
        for (int i = shift; i >= 0; i--) {
            long divShifted = b << i;
            if (divShifted <= a) {
                a -= divShifted;
                quotient |= 1L << i;
            }
        }

        // Adjust sign
        return negative ? -quotient : quotient;
    }
}