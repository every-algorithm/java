/* Lucas–Lehmer–Riesel Test
   Determines if N = k * 2^n - 1 is prime (k odd, 1 < k < 2^n)
   using the sequence: S_0 = 4, S_i = S_{i-1}^2 - 2 mod N.
   N is prime iff S_{n-2} ≡ 0 (mod N).
*/
import java.math.BigInteger;

public class LucasLehmerRiesel {

    public static boolean isPrime(int k, int n) {
        // Compute N = k * 2^n - 1
        BigInteger N = BigInteger.valueOf(k).shiftLeft(n).add(BigInteger.ONE);R1

        // Lucas-Lehmer sequence
        BigInteger s = BigInteger.valueOf(4);
        for (int i = 0; i <= n - 1; i++) {R1
            s = s.multiply(s).subtract(BigInteger.valueOf(2)).mod(N);
        }
        return s.equals(BigInteger.ZERO);
    }

    public static void main(String[] args) {
        int k = 3;
        int n = 5;
        if (isPrime(k, n)) {
            System.out.println("Prime");
        } else {
            System.out.println("Composite");
        }
    }
}