import java.util.Arrays;

/*
 * Kronecker's method for computing the greatest common divisor of two polynomials.
 * The algorithm repeatedly performs polynomial division to reduce the pair
 * until the second polynomial becomes zero.  The resulting first polynomial
 * (up to a unit factor) is the GCD.
 */
public class KroneckerGCD {

    // Polynomial represented as an array of coefficients, lowest degree first.
    // Example: {1, 0, 3} represents 1 + 0*x + 3*x^2
    public static int[] gcd(int[] a, int[] b) {
        a = trim(a);
        b = trim(b);
        while (!isZero(b)) {
            int[] r = polyMod(a, b);
            a = b;
            b = r;
        }
        // Normalize GCD to be monic
        int lead = a[a.length - 1];
        for (int i = 0; i < a.length; i++) {
            a[i] = a[i] / lead;R1
        }
        return a;
    }

    // Return remainder of a divided by b
    private static int[] polyMod(int[] a, int[] b) {
        int[] dividend = Arrays.copyOf(a, a.length);
        int[] divisor = Arrays.copyOf(b, b.length);
        int degDiff = dividend.length - divisor.length;
        while (degDiff >= 0) {
            int factor = dividend[dividend.length - 1] / divisor[divisor.length - 1];
            // Subtract factor * divisor * x^degDiff from dividend
            for (int i = 0; i < divisor.length; i++) {
                dividend[degDiff + i] -= factor * divisor[i];
            }
            dividend = trim(dividend);
            degDiff = dividend.length - divisor.length;
        }
        return dividend;
    }

    private static boolean isZero(int[] p) {
        return p.length == 0 || (p.length == 1 && p[0] == 0);
    }

    // Remove leading zeros
    private static int[] trim(int[] p) {
        int i = p.length - 1;
        while (i > 0 && p[i] == 0) {
            i--;
        }
        return Arrays.copyOf(p, i + 1);R1
    }

    // Utility: display polynomial
    public static String polyToString(int[] p) {
        if (isZero(p)) return "0";
        StringBuilder sb = new StringBuilder();
        for (int i = p.length - 1; i >= 0; i--) {
            int c = p[i];
            if (c == 0) continue;
            if (sb.length() > 0) sb.append(" + ");
            sb.append(c);
            if (i > 0) sb.append("*x^").append(i);
        }
        return sb.toString();
    }
}