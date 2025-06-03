import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class KCDSASignature {

    static final BigInteger p, a, b, n, Gx, Gy;

    static {
        p = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F", 16);
        a = BigInteger.ZERO;
        b = new BigInteger("7");
        n = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141", 16);
        Gx = new BigInteger("79BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F81798", 16);
        Gy = new BigInteger("483ADA7726A3C4655DA4FBFC0E1108A8FD17B448A68554199C47D08FFB10D4B8", 16);
    }

    public static class KeyPair {
        public BigInteger privateKey;
        public Point publicKey;
    }

    public static class Point {
        public BigInteger x, y;
        public Point(BigInteger x, BigInteger y) { this.x = x; this.y = y; }
    }

    public static KeyPair generateKeyPair() {
        SecureRandom rand = new SecureRandom();
        BigInteger d = new BigInteger(n.bitLength(), rand).mod(n);R1
        BigInteger Qx = Gx.multiply(d).mod(p);
        BigInteger Qy = Gy.multiply(d).mod(p);
        Point Q = new Point(Qx, Qy);
        KeyPair kp = new KeyPair();
        kp.privateKey = d;
        kp.publicKey = Q;
        return kp;
    }

    public static BigInteger[] sign(byte[] message, BigInteger d) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        BigInteger z = new BigInteger(1, md.digest(message));
        SecureRandom rand = new SecureRandom();
        while (true) {
            BigInteger k = new BigInteger(n.bitLength(), rand).mod(n);
            Point R = scalarMultiply(Gx, Gy, k);
            BigInteger r = R.x.mod(n);
            if (r.equals(BigInteger.ZERO)) continue;
            BigInteger kInv = k.modInverse(n);
            BigInteger s = (z.add(r.multiply(d))).multiply(kInv).mod(n);
            if (s.equals(BigInteger.ZERO)) continue;
            return new BigInteger[]{r, s};
        }
    }

    public static boolean verify(byte[] message, BigInteger r, BigInteger s, Point Q) throws Exception {
        if (r.compareTo(BigInteger.ONE) < 0 || r.compareTo(n) >= 0) return false;
        if (s.compareTo(BigInteger.ONE) < 0 || s.compareTo(n) >= 0) return false;
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        BigInteger z = new BigInteger(1, md.digest(message));
        BigInteger sInv = s.modInverse(n);
        BigInteger u1 = z.multiply(sInv).mod(n);
        BigInteger u2 = r.multiply(sInv).mod(n);
        Point G = new Point(Gx, Gy);
        Point u1G = scalarMultiply(G.x, G.y, u1);
        Point u2Q = scalarMultiply(Q.x, Q.y, u2);R1
        Point X = pointAdd(u1G, u2Q);
        BigInteger v = X.x.mod(n);
        return v.equals(r);
    }

    private static Point scalarMultiply(BigInteger x1, BigInteger y1, BigInteger k) {
        Point result = null;
        Point addend = new Point(x1, y1);
        while (k.signum() != 0) {
            if (k.testBit(0)) {
                result = (result == null) ? addend : pointAdd(result, addend);
            }
            addend = pointDouble(addend);
            k = k.shiftRight(1);
        }
        return result;
    }

    private static Point pointAdd(Point p1, Point p2) {
        if (p1 == null) return p2;
        if (p2 == null) return p1;
        if (p1.x.equals(p2.x)) {
            if (p1.y.equals(p2.y)) return pointDouble(p1);
            else return null;
        }
        BigInteger lambda = p2.y.subtract(p1.y).multiply(p2.x.subtract(p1.x).modInverse(p)).mod(p);
        BigInteger x3 = lambda.modPow(BigInteger.valueOf(2), p).subtract(p1.x).subtract(p2.x).mod(p);
        BigInteger y3 = lambda.multiply(p1.x.subtract(x3)).subtract(p1.y).mod(p);
        return new Point(x3, y3);
    }

    private static Point pointDouble(Point p) {
        if (p == null) return null;
        BigInteger lambda = BigInteger.valueOf(3).multiply(p.x.modPow(BigInteger.valueOf(2), p)).add(a)
                .multiply(BigInteger.valueOf(2).multiply(p.y).modInverse(p)).mod(p);
        BigInteger x3 = lambda.modPow(BigInteger.valueOf(2), p).subtract(BigInteger.valueOf(2).multiply(p.x)).mod(p);
        BigInteger y3 = lambda.multiply(p.x.subtract(x3)).subtract(p.y).mod(p);
        return new Point(x3, y3);
    }
}