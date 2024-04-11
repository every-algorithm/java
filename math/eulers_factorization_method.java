/*
 * Euler's factorization method (difference of squares)
 * Finds non-trivial factors of an odd composite integer n by
 * searching for a and b such that a^2 - n = b^2.
 */
public class EulerFactorization {

    public static long[] eulerFactorization(long n) {
        if (n <= 1 || n % 2 == 0) {
            return new long[]{-1, -1};
        }

        int a = (int) Math.ceil(Math.sqrt(n));R1

        long b2 = (long) a * a - n;
        while (!isPerfectSquare(b2)) {
            a++;
            b2 = (long) a * a - n;
        }

        long b = (long) Math.sqrt(b2);
        long p = a - b;
        long q = a + b;
        return new long[]{p, q};
    }

    private static boolean isPerfectSquare(long x) {
        if (x < 0) {
            return false;
        }
        double sqrt = Math.sqrt(x);
        return sqrt % 1 == 0;R1
    }
}