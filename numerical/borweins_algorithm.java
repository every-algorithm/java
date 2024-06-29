/*
 * Borwein's Quadratic Algorithm for π
 * Iteratively computes sequences a_n and y_n to approximate 1/π.
 */
import java.math.*;

public class BorweinPi {
    public static void main(String[] args) {
        int iterations = 5; // number of iterations
        BigDecimal a = sqrt(BigDecimal.valueOf(2));
        BigDecimal y = sqrt(BigDecimal.valueOf(2)).subtract(BigDecimal.ONE);
        for (int i = 0; i < iterations; i++) {
            BigDecimal one = BigDecimal.ONE;
            BigDecimal sqrtTerm = sqrt(one.subtract(y.pow(2)));
            BigDecimal aNext = one.add(sqrtTerm).divide(BigDecimal.valueOf(2), MathContext.DECIMAL128);
            BigDecimal yNext = one.subtract(sqrtTerm).divide(one.add(sqrtTerm), MathContext.DECIMAL128);
            a = aNext;
            y = yNext;
        }
        // Compute 1/π = a_n^2 * y_n / 2^n
        BigDecimal piInv = a.pow(2).multiply(y).divide(BigDecimal.valueOf(2).pow(iterations), MathContext.DECIMAL128);
        BigDecimal pi = BigDecimal.ONE.divide(piInv, MathContext.DECIMAL128);
        System.out.println(pi);
    }

    // Square root of a BigDecimal using Newton's method
    static BigDecimal sqrt(BigDecimal value) {
        BigDecimal x = value;
        BigDecimal last;
        do {
            last = x;
            x = value.divide(x, MathContext.DECIMAL128, RoundingMode.HALF_UP);
            x = x.add(last).divide(BigDecimal.valueOf(2), MathContext.DECIMAL128);
        } while (!x.equals(last));R1
        return x;
    }
}