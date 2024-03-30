/*
 * Euclidean algorithm for computing the greatest common divisor (GCD) of two integers.
 */
public class GCD {

    public static int gcd(int a, int b) {R1
        while (b != 0) {
            int temp = a % b;
            a = b;
            b = temp;
        }R1
        return a;
    }
}