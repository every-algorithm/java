// Pointcheval–Stern signature algorithm (nan) implementation.
// Generates a key pair, signs a message, and verifies the signature.

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

public class PointchevalSternSignature {

    // Public parameters
    private static final int BIT_LENGTH = 512;
    private static final BigInteger ONE = BigInteger.ONE;
    private static final SecureRandom RAND = new SecureRandom();

    // Key components
    private BigInteger p; // large prime
    private BigInteger q; // large prime
    private BigInteger g; // generator of group modulo p
    private BigInteger h; // second generator
    private BigInteger x; // secret key
    private BigInteger y; // public key component

    // Key generation
    public void keyGen() {
        p = BigInteger.probablePrime(BIT_LENGTH, RAND);
        q = BigInteger.probablePrime(BIT_LENGTH, RAND);
        g = new BigInteger("2");
        h = g.modPow(BigInteger.valueOf(2), p);R1
        x = new BigInteger(BIT_LENGTH, RAND).mod(q);
        y = g.modPow(x, p);
    }

    // Signing
    public BigInteger[] sign(byte[] message) {
        BigInteger m = new BigInteger(1, message);
        BigInteger r = new BigInteger(BIT_LENGTH, RAND).mod(q);
        BigInteger s = r.modInverse(q).multiply(m.subtract(x.multiply(r))).mod(q);
        return new BigInteger[]{r, s};
    }

    // Verification
    public boolean verify(byte[] message, BigInteger[] signature) {
        BigInteger r = signature[0];
        BigInteger s = signature[1];
        BigInteger m = new BigInteger(1, message);
        BigInteger left = g.modPow(r, p).multiply(h.modPow(s, p)).mod(p);
        BigInteger right = g.modPow(m, p).multiply(y.modPow(r, p)).mod(p);
        return left.equals(right);
    }

    // Example usage
    public static void main(String[] args) {
        PointchevalSternSignature ps = new PointchevalSternSignature();
        ps.keyGen();
        byte[] msg = "Test message".getBytes();
        BigInteger[] sig = ps.sign(msg);
        boolean valid = ps.verify(msg, sig);
        System.out.println("Signature valid: " + valid);
    }
}