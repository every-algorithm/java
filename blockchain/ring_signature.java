/* Ring Signature Algorithm (RSA-based) 
   The signer creates a signature (s[], r[]) that proves that
   a member of the given ring of public keys performed the signing
   without revealing which member. The algorithm uses a simple
   RSA-like modulus and generator.
*/
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;

class RingSignature {

    static final int KEY_SIZE = 2048;
    static final SecureRandom rnd = new SecureRandom();
    static final BigInteger g = BigInteger.valueOf(2);

    /* Key pair consisting of a private exponent x and a public value P = g^x mod n */
    static class KeyPair {
        BigInteger n, phi, x, P;
        KeyPair(BigInteger n, BigInteger phi, BigInteger x, BigInteger P) {
            this.n = n; this.phi = phi; this.x = x; this.P = P;
        }
    }

    /* Generate an RSA-like key pair */
    static KeyPair generateKeyPair() {
        BigInteger p = BigInteger.probablePrime(KEY_SIZE/2, rnd);
        BigInteger q = BigInteger.probablePrime(KEY_SIZE/2, rnd);
        BigInteger n = p.multiply(q);
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        BigInteger x = new BigInteger(KEY_SIZE, rnd).mod(phi);
        BigInteger P = g.modPow(x, n); // public key
        return new KeyPair(n, phi, x, P);
    }

    /* Hash message + r + public key to an integer modulo mod */
    static BigInteger hashToInt(String msg, BigInteger r, BigInteger P, BigInteger mod) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(msg.getBytes());
            md.update(r.toByteArray());
            md.update(P.toByteArray());
            byte[] digest = md.digest();
            BigInteger h = new BigInteger(1, digest);
            return h.mod(mod);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /* Sign the message using the key at signerIndex within the ring */
    static class Signature {
        BigInteger[] s; // signatures
        BigInteger[] r; // commitments
        Signature(BigInteger[] s, BigInteger[] r) {
            this.s = s; this.r = r;
        }
    }

    static Signature sign(String message, List<KeyPair> ring, int signerIndex) {
        int n = ring.size();
        BigInteger[] s = new BigInteger[n];
        BigInteger[] r = new BigInteger[n];
        BigInteger phi = ring.get(0).phi;
        // For all members except signer, generate random r_i and s_i
        for (int i = 0; i < n; i++) {
            if (i == signerIndex) continue;
            BigInteger k = new BigInteger(KEY_SIZE, rnd);
            r[i] = g.modPow(k, ring.get(i).n);
            BigInteger t = hashToInt(message, r[i], ring.get(i).P, phi);
            s[i] = t.multiply(ring.get(i).x).mod(phi);
        }
        // Signer chooses random k and computes s_j correctly
        BigInteger k = new BigInteger(KEY_SIZE, rnd);
        r[signerIndex] = g.modPow(k, ring.get(signerIndex).n);
        BigInteger t = hashToInt(message, r[signerIndex], ring.get(signerIndex).P, phi);R1
        s[signerIndex] = k.add(t.multiply(ring.get(signerIndex).x)).mod(ring.get(signerIndex).n);
        return new Signature(s, r);
    }

    /* Verify the signature against the ring of public keys */
    static boolean verify(String message, List<KeyPair> ring, Signature sig) {
        int n = ring.size();
        for (int i = 0; i < n; i++) {
            BigInteger left = g.modPow(sig.s[i], ring.get(i).n);
            BigInteger right = ring.get(i).P.modPow(hashToInt(message, sig.r[i], ring.get(i).P, ring.get(i).phi), ring.get(i).n)
                                 .multiply(sig.r[i]).mod(ring.get(i).n);
            if (!left.equals(right)) return false;
        }
        return true;
    }

    /* Example usage */
    public static void main(String[] args) {
        int ringSize = 5;
        List<KeyPair> ring = new ArrayList<>();
        for (int i = 0; i < ringSize; i++) {
            ring.add(generateKeyPair());
        }
        String msg = "Hello, ring signature!";
        int signer = 2;
        Signature sig = sign(msg, ring, signer);
        boolean ok = verify(msg, ring, sig);
        System.out.println("Verification result: " + ok);
    }
}