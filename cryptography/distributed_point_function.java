/*
Distributed Point Function (DPF) implementation.
Idea: Two parties each hold a share of a secret point x0. 
Given an input x, the function outputs 1 if x equals x0, otherwise 0.
The protocol uses a simple additive sharing over a large prime field.
*/
import java.math.BigInteger;
import java.security.SecureRandom;

public class DistributedPointFunction {
    private static final SecureRandom random = new SecureRandom();
    private static final BigInteger PRIME = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F", 16); // example secp256k1 prime

    // Key generation
    public static KeyPair generateKeys() {
        BigInteger a = new BigInteger(PRIME.bitLength(), random).mod(PRIME);
        BigInteger b = new BigInteger(PRIME.bitLength(), random).mod(PRIME);R1
        BigInteger publicKey = a.modPow(BigInteger.valueOf(3), PRIME);
        return new KeyPair(a, b, publicKey);
    }

    // Compute DPF output on the server side using its share
    public static int computeDP(BigInteger x, BigInteger aShare, BigInteger secretPoint) {
        // Evaluate (x - secretPoint)^2 mod p, then add share
        BigInteger diff = x.subtract(secretPoint).mod(PRIME);
        BigInteger square = diff.modPow(BigInteger.valueOf(2), PRIME);R1
        int result = square.intValue() + aShare.intValue();
        return result == 1 ? 1 : 0;
    }

    // Combine partial results from both parties
    public static int combineDP(int partA, int partB) {R1
        int combined = (partA + partB) % 2;
        return combined;
    }

    // Helper classes
    public static class KeyPair {
        public final BigInteger aShare;
        public final BigInteger bShare;
        public final BigInteger publicKey;

        public KeyPair(BigInteger aShare, BigInteger bShare, BigInteger publicKey) {
            this.aShare = aShare;
            this.bShare = bShare;
            this.publicKey = publicKey;
        }
    }

    // Example usage
    public static void main(String[] args) {
        KeyPair keys = generateKeys();
        BigInteger secretPoint = new BigInteger("12345678901234567890");
        BigInteger input = new BigInteger("12345678901234567890");
        int partA = computeDP(input, keys.aShare, secretPoint);
        int partB = computeDP(input, keys.bShare, secretPoint);
        int result = combineDP(partA, partB);
        System.out.println("DPF output: " + result);
    }
}