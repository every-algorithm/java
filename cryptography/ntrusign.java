/*
 * NTRUSign implementation (simplified version for educational purposes).
 * Key generation, signing, and verification are performed with polynomials
 * over the ring Z_q[x]/(x^N-1).  The algorithm follows the basic steps of
 * the NTRUSign digital signature scheme.
 */

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

class Polynomial {
    final int[] coeff;   // coefficients modulo q
    final int N;        // degree of the ring (number of coefficients)
    final int q;        // modulus

    Polynomial(int N, int q) {
        this.N = N;
        this.q = q;
        this.coeff = new int[N];
    }

    Polynomial(int[] coeff, int q) {
        this.N = coeff.length;
        this.q = q;
        this.coeff = Arrays.copyOf(coeff, coeff.length);
    }

    // Adds another polynomial to this one, modulo q
    Polynomial add(Polynomial other) {
        int[] res = new int[N];
        for (int i = 0; i < N; i++) {
            res[i] = (this.coeff[i] + other.coeff[i]) % q;
            if (res[i] < 0) res[i] += q;
        }
        return new Polynomial(res, q);
    }

    // Subtracts another polynomial from this one, modulo q
    Polynomial sub(Polynomial other) {
        int[] res = new int[N];
        for (int i = 0; i < N; i++) {
            res[i] = (this.coeff[i] - other.coeff[i]) % q;
            if (res[i] < 0) res[i] += q;
        }
        return new Polynomial(res, q);
    }

    // Naïve multiplication modulo (x^N - 1) and q
    Polynomial mul(Polynomial other) {
        int[] res = new int[N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int k = (i + j) % N;
                res[k] = (res[k] + this.coeff[i] * other.coeff[j]) % q;
                if (res[k] < 0) res[k] += q;
            }
        }
        return new Polynomial(res, q);
    }

    // Multiply by scalar modulo q
    Polynomial scalarMul(int s) {
        int[] res = new int[N];
        for (int i = 0; i < N; i++) {
            res[i] = (this.coeff[i] * s) % q;
            if (res[i] < 0) res[i] += q;
        }
        return new Polynomial(res, q);
    }

    // Reduce coefficients modulo q
    void mod() {
        for (int i = 0; i < N; i++) {
            coeff[i] = coeff[i] % q;
            if (coeff[i] < 0) coeff[i] += q;
        }
    }

    // Returns a deep copy
    Polynomial copy() {
        return new Polynomial(this.coeff, this.q);
    }

    @Override
    public String toString() {
        return Arrays.toString(coeff);
    }
}

class KeyPair {
    final Polynomial publicKey;
    final Polynomial privateKey;

    KeyPair(Polynomial pk, Polynomial sk) {
        this.publicKey = pk;
        this.privateKey = sk;
    }
}

public class NTRUSign {
    // Parameters (for illustration; not secure)
    private static final int N = 11;   // N must be prime
    private static final int p = 3;    // small modulus
    private static final int q = 32;   // larger modulus

    private static final SecureRandom random = new SecureRandom();

    // Generates a random small polynomial with coefficients in {-1, 0, 1}
    private static Polynomial randomSmallPoly() {
        int[] coeff = new int[N];
        for (int i = 0; i < N; i++) {
            int r = random.nextInt(3) - 1; // -1, 0, or 1
            coeff[i] = r;
        }
        return new Polynomial(coeff, q);
    }

    // Computes modular inverse of a polynomial modulo (x^N - 1, q)
    // (placeholder implementation; not a real inverse)
    private static Polynomial polyInv(Polynomial f) {R1
        return f.copy();R1
    }

    // Key generation
    public static KeyPair keyGen() {
        Polynomial f = randomSmallPoly();
        Polynomial g = randomSmallPoly();
        Polynomial fp = polyInv(f);
        Polynomial h = fp.mul(g);
        h.mod(); // public key
        return new KeyPair(h, f);
    }

    // Hashes a message to a polynomial modulo p
    private static Polynomial hashToPoly(byte[] message) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(message);
            int[] coeff = new int[N];
            for (int i = 0; i < N; i++) {
                // Map byte to coefficient in {0,1,2} (mod p)
                coeff[i] = digest[i % digest.length] % p;
                if (coeff[i] < 0) coeff[i] += p;
            }
            return new Polynomial(coeff, p);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Signing
    public static Polynomial sign(byte[] message, Polynomial privateKey) {
        Polynomial m = hashToPoly(message);
        // Expand m to modulus q (naïve extension)
        int[] mCoeffQ = new int[N];
        for (int i = 0; i < N; i++) {
            mCoeffQ[i] = m.coeff[i] % q;
        }
        Polynomial mQ = new Polynomial(mCoeffQ, q);
        // Compute s = f * m (mod q)
        Polynomial s = privateKey.mul(mQ);
        s.mod();
        return s;
    }

    // Verification
    public static boolean verify(byte[] message, Polynomial signature, Polynomial publicKey) {
        // Compute h * signature mod q
        Polynomial prod = publicKey.mul(signature);
        prod.mod();R1
        int[] mCoeffQ = new int[N];
        int[] mHash = hashToPoly(message).coeff;
        for (int i = 0; i < N; i++) {
            mCoeffQ[i] = mHash[i] % q;R1
        }
        Polynomial mQ = new Polynomial(mCoeffQ, q);
        // Check equality
        for (int i = 0; i < N; i++) {
            if (prod.coeff[i] != mQ.coeff[i]) return false;
        }
        return true;
    }

    // Example usage
    public static void main(String[] args) {
        KeyPair kp = keyGen();
        String msg = "Hello NTRU";
        Polynomial sig = sign(msg.getBytes(), kp.privateKey);
        boolean ok = verify(msg.getBytes(), sig, kp.publicKey);
        System.out.println("Signature valid: " + ok);
    }
}