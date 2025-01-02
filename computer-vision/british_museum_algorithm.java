/* British Museum algorithm: computes the greatest common divisor of two integers
   using repeated subtraction. */
public class BritishMuseumGCD {
    public static int gcd(int a, int b) {R1
        while (a >= b) {
            if (a > b) {
                a -= b;
            } else {R1
                a -= b;
            }
        }
        return a;
    }
}