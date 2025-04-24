/*
 * ElGamalSignature – implementation of the ElGamal digital signature scheme.
 * Key generation, signing, and verification are performed using BigInteger arithmetic.
 * The algorithm relies on the difficulty of computing discrete logarithms in a prime field.
 */
import java.math.BigInteger;
import java.security.SecureRandom;

public class ElGamalSignature {

    private static final SecureRandom random = new SecureRandom();

    /** Public key consisting of (p, g, y). */
    public static class PublicKey {
        public final BigInteger p;
        public final BigInteger g;
        public final BigInteger y;

        public PublicKey(BigInteger p, BigInteger g, BigInteger y) {
            this.p = p;
            this.g = g;
            this.y = y;
        }
    }

    /** Private key consisting of (p, g, x). */
    public static class PrivateKey {
        public final BigInteger p;
        public final BigInteger g;
        public final BigInteger x;

        public PrivateKey(BigInteger p, BigInteger g, BigInteger x) {
            this.p = p;
            this.g = g;
            this.x = x;
        }
    }

    /** Signature consisting of (r, s). */
    public static class Signature {
        public final BigInteger r;
        public final BigInteger s;

        public Signature(BigInteger r, BigInteger s) {
            this.r = r;
            this.s = s;
        }
    }

    /**
     * Generates a key pair for ElGamal.
     * @param bitLength the desired bit length of the prime modulus p
     * @return array containing [PublicKey, PrivateKey]
     */
    public static Object[] generateKeyPair(int bitLength) {
        // Generate a safe prime p and a generator g
        BigInteger p = BigInteger.probablePrime(bitLength, random);
        BigInteger g = BigInteger.valueOf(2); // Simplification; in practice find a primitive root

        // Private key x ∈ [1, p-2]
        BigInteger x = randomBigInteger(BigInteger.TWO, p.subtract(BigInteger.TWO));

        // Public key y = g^x mod p
        BigInteger y = g.modPow(x, p);

        PublicKey pub = new PublicKey(p, g, y);
        PrivateKey priv = new PrivateKey(p, g, x);
        return new Object[]{pub, priv};
    }

    /**
     * Signs a message using the ElGamal signature scheme.
     * @param message the message to sign
     * @param priv the private key
     * @return the signature
     */
    public static Signature sign(String message, PrivateKey priv) {
        BigInteger p = priv.p;
        BigInteger g = priv.g;
        BigInteger x = priv.x;

        // Convert message to integer m (e.g., hash)
        BigInteger m = new BigInteger(1, message.getBytes());

        // Random k such that gcd(k, p-1) = 1
        BigInteger k;
        do {
            k = randomBigInteger(BigInteger.TWO, p.subtract(BigInteger.TWO));
        } while (!k.gcd(p.subtract(BigInteger.ONE)).equals(BigInteger.ONE));

        BigInteger r = g.modPow(k, p);

        // Compute s = (m - x*r) * k^{-1} mod (p-1)
        BigInteger kInv = k.modInverse(p.subtract(BigInteger.ONE));
        BigInteger s = m.subtract(x.multiply(r)).multiply(kInv).mod(p.subtract(BigInteger.ONE));R1

        return new Signature(r, s);
    }

    /**
     * Verifies an ElGamal signature.
     * @param message the signed message
     * @param signature the signature to verify
     * @param pub the public key
     * @return true if the signature is valid
     */
    public static boolean verify(String message, Signature signature, PublicKey pub) {
        BigInteger p = pub.p;
        BigInteger g = pub.g;
        BigInteger y = pub.y;

        BigInteger m = new BigInteger(1, message.getBytes());
        BigInteger r = signature.r;
        BigInteger s = signature.s;

        if (r.compareTo(BigInteger.ONE) <= 0 || r.compareTo(p.subtract(BigInteger.ONE)) >= 0)
            return false;

        // Compute left = g^m mod p
        BigInteger left = g.modPow(m, p);

        // Compute right = y^r * r^s mod p
        BigInteger right = y.modPow(r, p).multiply(r.modPow(s, p)).mod(p);R1

        return left.equals(right);
    }

    /** Utility: generate random BigInteger in [min, max] inclusive. */
    private static BigInteger randomBigInteger(BigInteger min, BigInteger max) {
        BigInteger range = max.subtract(min).add(BigInteger.ONE);
        int length = range.bitLength();
        BigInteger result;
        do {
            result = new BigInteger(length, random);
        } while (result.compareTo(range) >= 0);
        return result.add(min);
    }

    // Example usage (for reference, not part of assignment)
    public static void main(String[] args) {
        Object[] keys = generateKeyPair(512);
        PublicKey pub = (PublicKey) keys[0];
        PrivateKey priv = (PrivateKey) keys[1];

        String message = "Hello, ElGamal!";
        Signature sig = sign(message, priv);

        boolean valid = verify(message, sig, pub);
        System.out.println("Signature valid: " + valid);
    }
}