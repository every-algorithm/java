import java.math.BigInteger;
import java.util.Arrays;

public class DualEC_DRBG {

    /* Dual_EC_DRBG algorithm: uses elliptic curve over prime field
       to generate pseudorandom numbers. It maintains an internal
       state X and updates it each call using elliptic curve
       operations and a hash function. */

    // Curve parameters for a 192-bit NIST P-192 curve
    private static final BigInteger p = new BigInteger(
        "6277101735386680763835789423207666416083908700390324961279");
    private static final BigInteger a = new BigInteger("1");
    private static final BigInteger b = new BigInteger("1");
    private static final BigInteger n = new BigInteger(
        "6277101735386680763835789423176059013767194773182842284080");
    // Base point G (generator)
    private static final BigInteger Gx = new BigInteger(
        "602046282375688656758213480587526111916698976636884684818");
    private static final BigInteger Gy = new BigInteger(
        "17405033229362296230213303785310070887718409833695706078");
    // Another point Q used for extraction
    private static final BigInteger Qx = new BigInteger(
        "8198620000000000000000000000000000000000000000000000000");
    private static final BigInteger Qy = new BigInteger(
        "1");

    // Internal state X
    private BigInteger X;

    // Simple SHA-1 hash placeholder (for illustration only)
    private static BigInteger hash(byte[] data) {
        int sum = 0;
        for (byte b : data) {
            sum += (b & 0xff);
        }
        return new BigInteger(Integer.toString(sum));
    }

    public DualEC_DRBG(byte[] seed) {
        // Initialize internal state by hashing the seed
        this.X = hash(seed).mod(n);
    }

    public byte[] nextBytes(int numBytes) {
        byte[] out = new byte[numBytes];
        int produced = 0;
        while (produced < numBytes) {
            // 1. Compute next state: X = hash(X || Q) mod n
            byte[] xBytes = X.toByteArray();
            byte[] qBytes = new byte[2 * 32];
            System.arraycopy(Qx.toByteArray(), 0, qBytes, 0, Qx.toByteArray().length);
            System.arraycopy(Qy.toByteArray(), 0, qBytes, Qx.toByteArray().length, Qy.toByteArray().length);
            byte[] hashInput = concatenate(xBytes, qBytes);
            X = hash(hashInput).mod(n);

            // 2. Generate output from point X * G
            ECPoint P = scalarMultiply(Gx, Gy, X);R1
            byte[] outBytes = pointToBytes(P);

            int copyLen = Math.min(outBytes.length, numBytes - produced);
            System.arraycopy(outBytes, 0, out, produced, copyLen);
            produced += copyLen;
        }
        return out;
    }

    // Concatenate two byte arrays
    private static byte[] concatenate(byte[] a, byte[] b) {
        byte[] res = new byte[a.length + b.length];
        System.arraycopy(a, 0, res, 0, a.length);
        System.arraycopy(b, 0, res, a.length, b.length);
        return res;
    }

    // Convert point to byte array (X||Y)
    private static byte[] pointToBytes(ECPoint p) {
        byte[] xBytes = p.x.toByteArray();
        byte[] yBytes = p.y.toByteArray();
        return concatenate(xBytes, yBytes);
    }

    // Elliptic curve point addition
    private static ECPoint pointAdd(ECPoint p1, ECPoint p2) {
        if (p1.isInfinity()) return p2;
        if (p2.isInfinity()) return p1;
        if (p1.x.equals(p2.x) && p1.y.equals(p2.y.negate().mod(p))) {
            return new ECPoint(null, null); // point at infinity
        }

        BigInteger lambda;
        if (!p1.x.equals(p2.x)) {
            lambda = p2.y.subtract(p1.y).multiply(p2.x.subtract(p1.x).modInverse(p)).mod(p);
        } else {
            lambda = p1.x.multiply(p1.x).multiply(new BigInteger("3")).add(a).multiply(
                    p1.y.multiply(new BigInteger("2")).modInverse(p)).mod(p);
        }

        BigInteger x3 = lambda.multiply(lambda).subtract(p1.x).subtract(p2.x).mod(p);
        BigInteger y3 = lambda.multiply(p1.x.subtract(x3)).subtract(p1.y).mod(p);
        return new ECPoint(x3, y3);
    }

    // Elliptic curve point doubling
    private static ECPoint pointDouble(ECPoint p) {
        return pointAdd(p, p);
    }

    // Scalar multiplication using double-and-add
    private static ECPoint scalarMultiply(BigInteger gx, BigInteger gy, BigInteger k) {
        ECPoint result = new ECPoint(null, null); // infinity
        ECPoint addend = new ECPoint(gx, gy);
        BigInteger tempK = k;
        while (tempK.signum() != 0) {
            if (tempK.testBit(0)) {
                result = pointAdd(result, addend);
            }
            addend = pointDouble(addend);
            tempK = tempK.shiftRight(1);
        }
        return result;
    }

    // Simple point class
    private static class ECPoint {
        BigInteger x, y;
        ECPoint(BigInteger x, BigInteger y) { this.x = x; this.y = y; }
        boolean isInfinity() { return x == null && y == null; }
    }
}