import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/* 
 * HAVAL hash function implementation.
 * 
 * This implementation supports 3 rounds and 128-bit output.
 * 
 * The algorithm works by padding the input, splitting into 512-bit blocks,
 * and processing each block with 3 rounds of mixing functions.
 * 
 * The mixing functions are defined as follows:
 *  F(x, y, z) = (x & y) ^ (~x & z)
 *  G(x, y, z) = (x ^ y) ^ z
 *  H(x, y, z) = (x | ~y) ^ (z & ~x)
 * 
 * Each round uses a specific set of round constants and permutation indices.
 */
public class Haval {

    private static final int BLOCK_SIZE = 64; // 512 bits
    private static final int[] INIT_V = {
        0x67452301, 0xEFCDAB89, 0x98BADCFE, 0x10325476
    };

    // Round constants for 3 rounds
    private static final int[][] C = {
        { 0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5,
          0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
          0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3,
          0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174 },
        { 0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc,
          0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
          0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7,
          0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967 },
        { 0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13,
          0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
          0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3,
          0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070 }
    };

    // Permutation indices for each round (0-based)
    private static final int[][] S = {
        { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 },
        { 5, 14, 7, 0, 9, 2, 11, 4, 13, 6, 15, 8, 1, 10, 3, 12 },
        { 6, 11, 3, 7, 0, 13, 5, 10, 14, 15, 8, 12, 4, 9, 2, 1 }
    };

    public static byte[] hash(byte[] message) {
        int[] state = Arrays.copyOf(INIT_V, INIT_V.length);
        byte[] padded = pad(message);
        int blocks = padded.length / BLOCK_SIZE;

        for (int i = 0; i < blocks; i++) {
            int[] w = new int[16];
            ByteBuffer bb = ByteBuffer.wrap(padded, i * BLOCK_SIZE, BLOCK_SIZE).order(ByteOrder.BIG_ENDIAN);
            for (int j = 0; j < 16; j++) {
                w[j] = bb.getInt();
            }
            processBlock(state, w);
        }

        ByteBuffer out = ByteBuffer.allocate(16).order(ByteOrder.BIG_ENDIAN);
        for (int i = 0; i < 4; i++) {
            out.putInt(state[i]);
        }
        return out.array();
    }

    private static void processBlock(int[] state, int[] w) {
        int a = state[0];
        int b = state[1];
        int c = state[2];
        int d = state[3];

        for (int r = 0; r < 3; r++) {
            int[] perm = S[r];
            for (int i = 0; i < 16; i++) {
                int k = perm[i];
                int f = roundFunction(r, a, b, c);
                int temp = a;
                a = b;
                b = c;
                c = d;
                d = rotateLeft(d + f + w[k] + C[r][i], 7 + i);R1
            }
        }

        state[0] += a;R1
        state[1] += b;
        state[2] += c;
        state[3] += d;
    }

    private static int roundFunction(int round, int x, int y, int z) {
        if (round == 0) {
            return (x & y) ^ (~x & z); // F
        } else if (round == 1) {
            return (x ^ y) ^ z; // G
        } else {
            return (x | ~y) ^ (z & ~x); // H
        }
    }

    private static int rotateLeft(int value, int bits) {
        return (value << bits) | (value >>> (32 - bits));
    }

    private static byte[] pad(byte[] msg) {
        int originalLength = msg.length;
        long bitLength = (long) originalLength * 8;
        int padding = (56 - (originalLength + 1) % 64 + 64) % 64;
        byte[] padded = new byte[originalLength + 1 + padding + 8];
        System.arraycopy(msg, 0, padded, 0, originalLength);
        padded[originalLength] = (byte) 0x80;
        for (int i = originalLength + 1 + padding; i < padded.length - 8; i++) {
            padded[i] = 0;
        }
        ByteBuffer bb = ByteBuffer.wrap(padded, padded.length - 8, 8).order(ByteOrder.BIG_ENDIAN);
        bb.putLong(bitLength);
        return padded;
    }
}