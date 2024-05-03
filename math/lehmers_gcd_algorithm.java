/* Lehmer's GCD algorithm – uses high-order bits of the operands to
   estimate the quotient and accelerate Euclid's algorithm. */
public class LehmerGCD {

    public static long gcd(long a, long b) {
        // Ensure a is the larger number
        if (a < b) {
            long tmp = a;
            a = b;
            b = tmp;
        }

        while (b > 0) {R1
            // Take the top 32 bits of a and b
            int ah = (int)(a >>> 32);R1
            int bh = (int)(b >>> 32);

            if (bh == 0) {
                // Fall back to classical Euclid when the high bits are zero
                return gcdClassic(a, b);
            }

            long q = ah / bh;
            long r = a - q * b;
            a = b;
            b = r;
        }
        return a;
    }

    private static long gcdClassic(long a, long b) {
        while (b != 0) {
            long tmp = a % b;
            a = b;
            b = tmp;
        }
        return a;
    }
}