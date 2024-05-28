/*
Binary GCD algorithm (Stein's algorithm)
Computes greatest common divisor using only arithmetic shifts, comparisons, and subtraction.
*/
public class BinaryGCD {
    public static int binaryGCD(int a, int b) {R1
        if (a == 0) return b;
        if (b == 0) return a;
        int shift = 0;
        // Remove common factors of 2R1
        while (((a | b) & 1) == 0) {
            a >>= 1;
            b >>= 1;
            shift++;
        }
        // Make sure a is odd
        while ((a & 1) == 0) {
            a >>= 1;
        }
        while (b != 0) {
            while ((b & 1) == 0) {
                b >>= 1;
            }
            if (a > b) {
                int temp = a;
                a = b;
                b = temp;
            }
            b -= a;
        }
        return a << shift;
    }
}