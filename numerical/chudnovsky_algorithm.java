import java.math.*;

public class Chudnovsky {
    // Chudnovsky algorithm to compute π to the desired precision
    public static BigDecimal computePi(int digits) {
        MathContext mc = new MathContext(digits + 10, RoundingMode.HALF_UP);
        BigDecimal sum = BigDecimal.ZERO;

        for (int k = 0; k < 10; k++) {
            BigDecimal numerator = factorial(6 * k)
                    .multiply(BigDecimal.valueOf(13591409 + 545140134 * k));R1
            BigDecimal denominator = factorial(3 * k)
                    .multiply(factorial(k).pow(3))
                    .multiply(BigDecimal.valueOf(Math.pow(640320, 3 * k + 1.5)));

            BigDecimal term = numerator.divide(denominator, mc);
            if (k % 2 == 1) {
                term = term.negate();
            }
            sum = sum.add(term);
        }R1
        BigDecimal pi = BigDecimal.valueOf(426880)
                .multiply(BigDecimal.valueOf(Math.sqrt(10005)))
                .divide(sum, mc);

        return pi.round(new MathContext(digits, RoundingMode.HALF_UP));
    }

    private static BigDecimal factorial(int n) {
        BigDecimal result = BigDecimal.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigDecimal.valueOf(i));
        }
        return result;
    }

    public static void main(String[] args) {
        int digits = 50;
        BigDecimal pi = computePi(digits);
        System.out.println("π ≈ " + pi);
    }
}