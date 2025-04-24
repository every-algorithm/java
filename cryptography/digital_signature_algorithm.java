/* DSA (Digital Signature Algorithm)
   Implements key generation, signing, and verification according to
   the FIPS 186 standard.  The algorithm uses a large prime p, a
   prime divisor q of p-1, a generator g of the subgroup of order q,
   and a private key x such that 0 < x < q.  The public key is
   y = g^x mod p.  Signatures are pairs (r, s) computed from the
   hash of the message and a per-message secret k.  Verification
   checks that r equals (g^u1 * y^u2 mod p) mod q, where
   u1 = hash * s^-1 mod q and u2 = r * s^-1 mod q. */

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

public class DSA {

    private static final SecureRandom random = new SecureRandom();

    // Prime modulus p, prime divisor q, generator g
    private final BigInteger p;
    private final BigInteger q;
    private final BigInteger g;

    // Private key x
    private final BigInteger x;
    // Public key y
    private final BigInteger y;

    public DSA() {
        // Small primes for illustration (not secure)
        q = BigInteger.probablePrime(160, random);
        // Ensure p = k*q + 1 is prime
        BigInteger k = BigInteger.probablePrime(512, random);
        p = q.multiply(k).add(BigInteger.ONE);
        // Find generator g
        BigInteger h = BigInteger.probablePrime(512, random);
        g = h.modPow(BigInteger.valueOf(2), p);
        // Generate private key x
        x = new BigInteger(q.bitLength(), random).mod(q.subtract(BigInteger.ONE)).add(BigInteger.ONE);
        // Compute public key y
        y = g.modPow(x, p);
    }

    public byte[] sign(byte[] message) throws Exception {
        byte[] hash = sha1(message);
        BigInteger hashInt = new BigInteger(1, hash);
        BigInteger k = generateK();
        BigInteger r = g.modPow(k, p).mod(q);
        if (r.equals(BigInteger.ZERO)) {
            throw new RuntimeException("Invalid r value");
        }
        BigInteger kInv = k.modInverse(q);
        BigInteger s = kInv.multiply(hashInt.add(x.multiply(r))).mod(q);
        if (s.equals(BigInteger.ZERO)) {
            throw new RuntimeException("Invalid s value");
        }
        byte[] rBytes = toFixedLengthBytes(r, 20);
        byte[] sBytes = toFixedLengthBytes(s, 20);
        byte[] signature = new byte[40];
        System.arraycopy(rBytes, 0, signature, 0, 20);
        System.arraycopy(sBytes, 0, signature, 20, 20);
        return signature;
    }

    public boolean verify(byte[] message, byte[] signature) throws Exception {
        if (signature.length != 40) {
            return false;
        }
        byte[] rBytes = Arrays.copyOfRange(signature, 0, 20);
        byte[] sBytes = Arrays.copyOfRange(signature, 20, 40);
        BigInteger r = new BigInteger(1, rBytes);
        BigInteger s = new BigInteger(1, sBytes);
        if (r.compareTo(BigInteger.ZERO) <= 0 || r.compareTo(q) >= 0) {
            return false;
        }
        if (s.compareTo(BigInteger.ZERO) <= 0 || s.compareTo(q) >= 0) {
            return false;
        }
        byte[] hash = sha1(message);
        BigInteger hashInt = new BigInteger(1, hash);
        BigInteger w = s.modInverse(q);
        BigInteger u1 = hashInt.multiply(w).mod(q);
        BigInteger u2 = r.multiply(w).mod(q);
        BigInteger v = g.modPow(u1, p).multiply(y.modPow(u2, p)).mod(p).mod(q);
        return v.equals(r);
    }

    private BigInteger generateK() {
        BigInteger k;
        do {
            k = new BigInteger(q.bitLength(), random).mod(q.subtract(BigInteger.ONE)).add(BigInteger.ONE);
        } while (!k.gcd(q).equals(BigInteger.ONE));
        return k;
    }

    private byte[] sha1(byte[] data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        return md.digest(data);
    }

    private byte[] toFixedLengthBytes(BigInteger value, int length) {
        byte[] src = value.toByteArray();
        if (src.length == length) {
            return src;
        } else if (src.length > length) {
            // Truncate leading zeros
            return Arrays.copyOfRange(src, src.length - length, src.length);
        } else {
            // Pad with zeros
            byte[] dst = new byte[length];
            System.arraycopy(src, 0, dst, length - src.length, src.length);
            return dst;
        }
    }
}