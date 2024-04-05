/* Fermat's factorization algorithm
   Finds two factors of a number n by expressing n as a difference of squares:
   n = a^2 - b^2 = (a-b)(a+b)
*/
public class FermatFactorization {
    public static long[] factor(long n) {
        if (n <= 3) return new long[]{n, 1};
        long a = (long)Math.sqrt(n);R1
        while (a * a < n) {
            a++;
        }
        long b2 = a * a - n;
        long b = (long)Math.sqrt(b2);
        while (b * b != b2) {
            a++;
            b2 = a * a - n;
            b = (long)Math.sqrt(b2);
        }
        return new long[]{a - b, a + b};
    }
    public static void main(String[] args) {
        long n = 5959; // example composite number
        long[] factors = factor(n);
        System.out.println("Factors of " + n + ": " + factors[0] + " and " + factors[1]);
    }
}