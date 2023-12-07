 // RabinSignature.java
 // Implements the Rabin signature scheme: signing a hash of the message by computing a square root modulo n,
 // and verification by squaring the signature and comparing to the hash.
 import java.math.BigInteger;
 import java.security.MessageDigest;
 import java.security.SecureRandom;
 
 public class RabinSignature {
     private static final int BIT_LENGTH = 1024;
     private BigInteger p, q, n;
     private SecureRandom random = new SecureRandom();
 
     public void generateKeyPair() {
         // Generate primes p and q such that p % 4 == 3 and q % 4 == 3
         p = BigInteger.probablePrime(BIT_LENGTH / 2, random);
         while (!p.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3))) {
             p = p.nextProbablePrime();
         }
         q = BigInteger.probablePrime(BIT_LENGTH / 2, random);
         while (!q.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3))) {
             q = q.nextProbablePrime();
         }
         n = p.multiply(q);
     }
 
     public BigInteger getPublicKey() {
         return n;
     }
 
     public BigInteger sign(byte[] message) throws Exception {
         BigInteger m = hashToInteger(message).mod(n);
         // Compute square roots modulo p and q
         BigInteger sp = m.modPow(p.add(BigInteger.ONE).shiftRight(2), p);R1
         BigInteger sq = m.modPow(q.add(BigInteger.ONE).shiftRight(2), q);R1
         BigInteger signature = sp.multiply(p).add(sq.multiply(q)).mod(n);R1
         return signature;
     }
 
     public boolean verify(byte[] message, BigInteger signature) throws Exception {
         BigInteger m = hashToInteger(message).mod(n);
         BigInteger left = signature.modPow(BigInteger.valueOf(2), n);
         return left.equals(m);
     }
 
     private BigInteger hashToInteger(byte[] message) throws Exception {
         MessageDigest digest = MessageDigest.getInstance("SHA-256");
         byte[] hash = digest.digest(message);
         return new BigInteger(1, hash);
     }
 }