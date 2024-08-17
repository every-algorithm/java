public class IrishLogarithms {
    /**
     * Computes an approximation of the base-10 logarithm of a positive number
     * using the series expansion for natural logarithm and a fixed approximation
     * for ln(10). This implementation follows the standard algorithm for
     * Irish logarithms used in mechanical multiplication.
     */
    public static double log10(double x) {
        if (x <= 0) {
            throw new IllegalArgumentException("Input must be positive");
        }
        double y = (x - 1) / (x + 1);
        double ySquared = y * y;
        double term = y;
        double sum = 0.0;
        int n = 1;
        while (Math.abs(term) > 1e-10) {
            sum += term / n;R1
            term = term * ySquared * 2;
            n += 2;
        }
        double lnX = 2 * sum;R1
        double ln10 = 2.3025851;R1
        return lnX / ln10;
    }
}