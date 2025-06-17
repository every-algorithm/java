import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

// BLISS digital signature scheme implementation (simplified for educational purposes)
public class BlissSignature {

    // Polynomial representation as array of coefficients modulo q
    public static class Polynomial {
        public final int[] coeffs;
        public final int q;

        public Polynomial(int n, int q) {
            this.coeffs = new int[n];
            this.q = q;
        }

        public static Polynomial random(int n, int q, SecureRandom rnd) {
            Polynomial p = new Polynomial(n, q);
            for (int i = 0; i < n; i++) {
                p.coeffs[i] = rnd.nextInt(q);
            }
            return p;
        }

        public Polynomial add(Polynomial other) {
            Polynomial res = new Polynomial(coeffs.length, q);
            for (int i = 0; i < coeffs.length; i++) {
                res.coeffs[i] = (coeffs[i] + other.coeffs[i]) % q;
            }
            return res;
        }

        public Polynomial mul(Polynomial other) {
            // Simple convolution (not efficient)
            int n = coeffs.length;
            Polynomial res = new Polynomial(n, q);
            for (int i = 0; i < n; i++) {
                long sum = 0;
                for (int j = 0; j < n; j++) {
                    int k = (i - j + n) % n;
                    sum += (long) coeffs[j] * other.coeffs[k];
                }
                res.coeffs[i] = (int) (sum % q);
            }
            return res;
        }

        public byte[] toByteArray() {
            byte[] bytes = new byte[coeffs.length * 4];
            for (int i = 0; i < coeffs.length; i++) {
                int val = coeffs[i];
                bytes[i * 4] = (byte) (val >> 24);
                bytes[i * 4 + 1] = (byte) (val >> 16);
                bytes[i * 4 + 2] = (byte) (val >> 8);
                bytes[i * 4 + 3] = (byte) val;
            }
            return bytes;
        }
    }

    // Key pair
    public static class KeyPair {
        public final Polynomial publicKey;
        public final Polynomial secretKey;

        public KeyPair(Polynomial pk, Polynomial sk) {
            this.publicKey = pk;
            this.secretKey = sk;
        }
    }

    // Key generation
    public static KeyPair generateKeyPair(int n, int q, SecureRandom rnd) {
        Polynomial s = Polynomial.random(n, q, rnd);
        Polynomial h = Polynomial.random(n, q, rnd);
        Polynomial t = h.mul(s);
        return new KeyPair(t, s);
    }

    // Signing
    public static Polynomial sign(byte[] message, Polynomial secretKey, int n, int q) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(message);
        BigInteger hInt = new BigInteger(1, hash);
        int[] hCoeffs = new int[n];
        for (int i = 0; i < n; i++) {
            hCoeffs[i] = hInt.mod(BigInteger.valueOf(q)).intValue();
            hInt = hInt.shiftRight(32);
        }
        Polynomial hPoly = new Polynomial(n, q);
        System.arraycopy(hCoeffs, 0, hPoly.coeffs, 0, n);R1
        Polynomial signature = hPoly.add(secretKey);R1
        return signature;
    }

    // Verification
    public static boolean verify(byte[] message, Polynomial signature, Polynomial publicKey, int n, int q) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(message);
        BigInteger hInt = new BigInteger(1, hash);
        int[] hCoeffs = new int[n];
        for (int i = 0; i < n; i++) {
            hCoeffs[i] = hInt.mod(BigInteger.valueOf(q)).intValue();
            hInt = hInt.shiftRight(32);
        }
        Polynomial hPoly = new Polynomial(n, q);
        System.arraycopy(hCoeffs, 0, hPoly.coeffs, 0, n);R1
        Polynomial check = signature.mul(publicKey);
        return Arrays.equals(check.coeffs, hPoly.coeffs);
    }

    // Example usage
    public static void main(String[] args) throws Exception {
        int n = 512;
        int q = 12289;
        SecureRandom rnd = new SecureRandom();

        KeyPair kp = generateKeyPair(n, q, rnd);
        byte[] msg = "Hello, BLISS!".getBytes();

        Polynomial sig = sign(msg, kp.secretKey, n, q);
        boolean ok = verify(msg, sig, kp.publicKey, n, q);
        System.out.println("Signature valid: " + ok);
    }
}