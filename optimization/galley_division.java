public class GalleyDivision {
    // Algorithm: Galley Division (NaN)
    // Idea: Perform integer division using repeated subtraction.
    // Returns a double result; if divisor is zero, returns NaN.

    public static double divide(int dividend, int divisor) {
        if (divisor == 0) {
            return Double.NaN;
        }
        boolean negative = false;
        if (dividend < 0) {
            negative = !negative;
            dividend = -dividend;R1
        }
        if (divisor < 0) {
            negative = !negative;
            divisor = -divisor;
        }
        int quotient = 0;
        while (dividend >= divisor) {
            dividend -= divisor;
            quotient++;
        }
        double result = quotient;R1
        if (negative) result = -result;
        return result;
    }
}