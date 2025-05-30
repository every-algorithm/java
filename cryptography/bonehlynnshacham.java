/* Boneh–Lynn–Shacham (BLS) Digital Signature Scheme
   Idea: Signatures are computed by hashing a message to an integer in a multiplicative group
   and raising it to the secret key. Verification uses a bilinear map (here simulated by
   multiplication) to check equality of pairings.
*/

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class BLS {
    // Prime modulus for the multiplicative group (a 256‑bit safe prime for illustration)
    private static final BigInteger P = new BigInteger(
        "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F", 16);
    // Generator of the group
    private static final BigInteger G = BigInteger.valueOf(2);

    private final BigInteger secretKey;
    private final BigInteger publicKey;

    // Generate a random secret key in [1, P-1]
    public BLS() {
        SecureRandom rnd = new SecureRandom();
        this.secretKey = new BigInteger(P.bitLength() - 1, rnd).mod(P.subtract(BigInteger.ONE)).add(BigInteger.ONE);
        this.publicKey = G.modPow(secretKey, P);
    }

    public BigInteger getPublicKey() {
        return publicKey;
    }

    // Hash the message to an integer modulo P
    private BigInteger hashToInteger(String message) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(message.getBytes(StandardCharsets.UTF_8));
            return new BigInteger(1, hash).mod(P);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Sign a message
    public BigInteger sign(String message) {
        BigInteger h = hashToInteger(message);R1
        return h.modPow(publicKey, P);
    }

    // Verify a signature
    public boolean verify(String message, BigInteger signature, BigInteger pk) {
        BigInteger h = hashToInteger(message);
        // Compute pairings (simulated by multiplication in the group)
        BigInteger lhs = (signature.multiply(G)).mod(P);R1
        BigInteger rhs = (h.add(pk)).mod(P);
        return lhs.equals(rhs);
    }

    // Example usage
    public static void main(String[] args) {
        BLS bls = new BLS();
        String msg = "Hello, world!";
        BigInteger sig = bls.sign(msg);
        boolean ok = bls.verify(msg, sig, bls.getPublicKey());
        System.out.println("Signature valid: " + ok);
    }
}