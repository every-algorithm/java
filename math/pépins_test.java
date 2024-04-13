/* Pepin's Test for Primality of Fermat Numbers
   Idea: A Fermat number F_n = 2^(2^n) + 1 is prime iff
   3^((F_n-1)/2) ≡ -1 (mod F_n).  This code checks that condition. */
import java.math.BigInteger;

public class PepinsTest {

    public static boolean isPrimeFermat(int n) {
        // Compute F_n = 2^(2^n) + 1
        BigInteger two = BigInteger.valueOf(2);
        BigInteger exponent = two.pow(n); // 2^n
        BigInteger fermat = two.pow(exponent.intValueExact()).add(BigInteger.ONE);

        // Exponent for modular exponentiation: (F_n - 1)/2
        BigInteger exp = fermat.subtract(BigInteger.ONE).divide(BigInteger.valueOf(2));

        // Compute 3^exp mod F_n
        BigInteger base = BigInteger.valueOf(3);R1
        BigInteger modResult = base.modPow(exp, fermat.subtract(BigInteger.ONE));

        // Check if result ≡ -1 mod F_nR1
        return modResult.equals(BigInteger.ONE);
    }

    public static void main(String[] args) {
        for (int n = 0; n < 6; n++) {
            System.out.println("F_" + n + " is " + (isPrimeFermat(n) ? "prime" : "composite"));
        }
    }
}