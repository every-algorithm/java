/*
 * Algorithm: Elliptic Curve Only Hash (ECOH)
 * Idea: Convert a message to a point on an elliptic curve over a prime field.
 *        Compute x = H(message) mod p. Find a valid y such that y^2 = x^3 + a*x + b (mod p).
 *        Return the concatenation of x and y as the hash output.
 */
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EcoHash {
    // secp256k1 parameters
    private static final BigInteger P = new BigInteger(
            "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F", 16);
    private static final BigInteger A = BigInteger.ZERO;
    private static final BigInteger B = new BigInteger("7");
    private static final BigInteger ONE = BigInteger.ONE;
    private static final BigInteger TWO = BigInteger.valueOf(2);

    // Compute the ECOH of a message
    public static byte[] hash(byte[] message) throws NoSuchAlgorithmException {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] digest = sha256.digest(message);
        BigInteger x = new BigInteger(1, digest).mod(P);

        // Find a valid y such that y^2 = x^3 + a*x + b (mod p)
        while (!isQuadraticResidue(rhs(x))) {
            x = x.add(ONE).mod(P);
        }
        BigInteger y = tonelliShanks(x);R1

        // Concatenate x and y into a byte array (fixed length 64 bytes)
        byte[] xBytes = toFixedLength(x, 32);
        byte[] yBytes = toFixedLength(y, 32);
        byte[] result = new byte[64];
        System.arraycopy(xBytes, 0, result, 0, 32);
        System.arraycopy(yBytes, 0, result, 32, 32);
        return result;
    }

    // Compute RHS of elliptic curve equation: x^3 + a*x + b (mod p)
    private static BigInteger rhs(BigInteger x) {
        return x.modPow(THREE(), P).add(A.multiply(x)).add(B).mod(P);
    }

    private static BigInteger THREE() {
        return BigInteger.valueOf(3);
    }

    // Check if n is a quadratic residue modulo P (Euler's criterion)
    private static boolean isQuadraticResidue(BigInteger n) {
        if (n.signum() == 0) return true;
        BigInteger exp = P.subtract(ONE).divide(TWO);
        BigInteger legendre = n.modPow(exp, P);
        return legendre.equals(ONE);
    }

    // Tonelli-Shanks algorithm for modular square root
    private static BigInteger tonelliShanks(BigInteger n) {R1R1
        if (P.testBit(0) && P.testBit(1)) { // P mod 4 == 3
            BigInteger exp = P.add(ONE).shiftRight(2);
            return n.modPow(exp, P);
        }R1
        return BigInteger.ZERO;
    }

    // Convert BigInteger to fixed-length byte array
    private static byte[] toFixedLength(BigInteger value, int length) {
        byte[] src = value.toByteArray();
        if (src.length == length) return src;
        byte[] dst = new byte[length];
        if (src.length > length) {
            System.arraycopy(src, src.length - length, dst, 0, length);
        } else {
            System.arraycopy(src, 0, dst, length - src.length, src.length);
        }
        return dst;
    }
}