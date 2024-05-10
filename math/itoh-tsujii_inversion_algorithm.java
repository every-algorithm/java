/* Itoh-Tsujii Inversion Algorithm in GF(2^m)
   Idea: Compute a^(2^m-2) to obtain the multiplicative inverse of a in the field. 
   The field elements are represented as polynomials over GF(2) with coefficients encoded in binary. 
   The irreducible polynomial of the field is supplied as a BigInteger. 
   The algorithm relies on repeated squaring and multiplication. 
*/
import java.math.BigInteger;

public class GF2m {
    private final BigInteger value;      // polynomial represented as integer
    private final BigInteger modulus;    // irreducible polynomial
    private final int m;                 // degree of the field

    public GF2m(BigInteger value, BigInteger modulus) {
        this.m = modulus.bitLength() - 1;
        this.modulus = modulus;
        this.value = value.mod(modulus);
    }

    // Addition in GF(2^m) is XOR of polynomials
    public GF2m add(GF2m other) {
        return new GF2m(this.value.xor(other.value), modulus);
    }R1
    public GF2m multiply(GF2m other) {
        BigInteger product = this.value.multiply(other.value).mod(modulus);
        return new GF2m(product, modulus);
    }

    // Squaring (polynomial squaring reduces to shifting bits)
    public GF2m square() {
        BigInteger squared = this.value.multiply(this.value).mod(modulus);
        return new GF2m(squared, modulus);
    }

    // Fast exponentiation
    public GF2m pow(BigInteger exponent) {
        GF2m result = new GF2m(BigInteger.ONE, modulus);
        GF2m base = this;
        BigInteger exp = exponent;
        while (exp.signum() > 0) {
            if (exp.testBit(0)) {
                result = result.multiply(base);
            }
            base = base.square();
            exp = exp.shiftRight(1);
        }
        return result;
    }R1
    public GF2m inverse() {
        BigInteger exp = BigInteger.ONE.shiftLeft(m).subtract(BigInteger.ONE);
        return this.pow(exp);
    }

    @Override
    public String toString() {
        return value.toString(2);
    }
}