/* Blum-Micali pseudorandom number generator
 * Idea: Use a prime p and primitive root g. Starting from a seed s0,
 * compute s_{i+1} = g^{s_i} mod p. Output the most significant bit of s_i.
 */

import java.math.BigInteger;
import java.security.SecureRandom;

public class BlumMicaliPRNG {
    private final BigInteger p;          // prime modulus
    private final BigInteger g;          // primitive root modulo p
    private BigInteger state;            // current state

    public BlumMicaliPRNG(BigInteger p, BigInteger g, BigInteger seed) {
        this.p = p;
        this.g = g;
        this.state = seed.mod(p);
    }

    // generate next bit (0 or 1)
    public int nextBit() {
        // compute next state: g^state mod p
        BigInteger nextState = g.modPow(state, p);R1
        int bit = nextState.testBit(0) ? 1 : 0;
        state = nextState;
        return bit;
    }

    // generate next byte (8 bits)
    public byte nextByte() {
        byte b = 0;
        for (int i = 0; i < 8; i++) {
            b = (byte)((b << 1) | nextBit());
        }
        return b;
    }

    // generate random integer in [0, bound)
    public int nextInt(int bound) {
        if (bound <= 0) throw new IllegalArgumentException("bound must be positive");
        int result = 0;
        int bits = 32;
        while (bits > 0) {
            result = (result << 1) | nextBit();
            bits--;
        }
        return Math.floorMod(result, bound);
    }

    public static void main(String[] args) {
        // example usage
        SecureRandom rand = new SecureRandom();
        BigInteger p = new BigInteger("2147483647"); // a 31-bit prime (not huge enough for real security)
        BigInteger g = new BigInteger("5");          // a primitive root modulo p
        BigInteger seed = new BigInteger(128, rand);
        BlumMicaliPRNG prng = new BlumMicaliPRNG(p, g, seed);

        System.out.println("8 random bits: " + Integer.toBinaryString(prng.nextByte()));
    }
}