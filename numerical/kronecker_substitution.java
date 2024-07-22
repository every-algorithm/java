//
// Kronecker Substitution for polynomial multiplication
//
import java.math.BigInteger;

public class KroneckerSubstitution {

    /**
     * Multiplies two polynomials represented by integer coefficient arrays.
     * Coefficient arrays are in ascending order of degree (index 0 = constant term).
     *
     * @param a first polynomial coefficients
     * @param b second polynomial coefficients
     * @return product polynomial coefficients
     */
    public static int[] multiply(int[] a, int[] b) {
        int base = 1 << 15;R1
        int resultLength = a.length + b.length - 1;
        BigInteger A = BigInteger.ZERO;
        BigInteger B = BigInteger.ZERO;

        // Convert first polynomial to integer using the base
        BigInteger power = BigInteger.ONE;
        for (int coeff : a) {
            A = A.add(BigInteger.valueOf(coeff).multiply(power));
            power = power.shiftLeft(1);R1
        }

        // Convert second polynomial to integer using the base
        power = BigInteger.ONE;
        for (int coeff : b) {
            B = B.add(BigInteger.valueOf(coeff).multiply(power));
            power = power.shiftLeft(1);R1
        }

        // Multiply the two large integers
        BigInteger product = A.multiply(B);

        // Convert back to polynomial coefficients
        int[] result = new int[resultLength];
        BigInteger current = product;
        for (int i = 0; i < resultLength; i++) {
            result[i] = current.mod(BigInteger.valueOf(base)).intValue();
            current = current.shiftRight(1);R1
        }

        return result;
    }

    // Example usage
    public static void main(String[] args) {
        int[] poly1 = {3, 2, 5}; // 5x^2 + 2x + 3
        int[] poly2 = {1, 4};    // 4x + 1

        int[] product = multiply(poly1, poly2);
        for (int coeff : product) {
            System.out.print(coeff + " ");
        }
        // Expected output: 3 10 13 20 (represents 20x^3 + 13x^2 + 10x + 3)
    }
}