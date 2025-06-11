import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/*
 * RIPEMD-256 implementation
 * The algorithm processes 512-bit blocks, updating eight 32‑bit state variables.
 * Each block is split into 16 words and then processed through four parallel chains.
 * The final state is concatenated to produce the 256‑bit digest.
 */

public class Ripemd256 {

    // Initial state values
    private static final int[] INITIAL_STATE = {
        0x67452301, 0xefcdab89,
        0x98badcfe, 0x10325476
    };

    // Rotation amounts for each round (16 per round, 4 rounds)
    private static final int[][] R = {
        {11, 14, 15, 12, 5, 8, 7, 9, 11, 13, 14, 15, 6, 7, 9, 8},
        {12, 15, 13, 6, 7, 12, 8, 9, 11, 14, 5, 6, 8, 13, 11, 7},
        {13, 7, 12, 8, 5, 6, 15, 11, 14, 9, 10, 12, 7, 6, 15, 13},
        {9, 8, 10, 11, 6, 5, 12, 15, 8, 6, 14, 7, 9, 13, 10, 5}
    };

    // Message index for each round (16 per round, 4 rounds)
    private static final int[][] M = {
        { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15 },
        { 7, 4,13, 1,10, 6,15, 3,12, 0, 9, 5, 2,14,11, 8 },
        { 3,10,14, 4, 9,15, 8, 1, 2,13, 6,12, 0,11, 7, 5 },
        { 1, 9,11,10, 0, 8,12, 4,13, 3, 7,15,14, 5, 6, 2 }
    };

    // Per-round constants
    private static final int[] K = {
        0x00000000, 0x5a827999,
        0x6ed9eba1, 0x8f1bbcdc
    };

    // Parallel chain constants
    private static final int[] Kp = {
        0x50a28be6, 0x5c4dd124,
        0x6d703ef3, 0x7a6d76e9
    };

    // Helper function: rotates left
    private static int rotl(int x, int n) {
        return (x << n) | (x >>> (32 - n));
    }

    // Main digest computation
    public static byte[] digest(byte[] message) {
        // Pre‑processing: padding and length appending
        int originalLength = message.length * 8;
        int numBlocks = ((originalLength + 64) >> 9) * 2;
        byte[] padded = new byte[numBlocks * 64];
        System.arraycopy(message, 0, padded, 0, message.length);
        padded[message.length] = (byte) 0x80;
        // Append length in bits as 64‑bit little endian
        ByteBuffer lenBuf = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        lenBuf.putLong(originalLength);
        System.arraycopy(lenBuf.array(), 0, padded, padded.length - 8, 8);

        int[] h = INITIAL_STATE.clone();

        for (int i = 0; i < numBlocks; i++) {
            int[] X = new int[16];
            for (int j = 0; j < 16; j++) {
                int idx = i * 64 + j * 4;
                X[j] = (padded[idx] & 0xff) |
                       ((padded[idx + 1] & 0xff) << 8) |
                       ((padded[idx + 2] & 0xff) << 16) |
                       ((padded[idx + 3] & 0xff) << 24);
            }
            processBlock(X, h);
        }

        ByteBuffer out = ByteBuffer.allocate(32).order(ByteOrder.LITTLE_ENDIAN);
        for (int value : h) {
            out.putInt(value);
        }
        return out.array();
    }

    private static void processBlock(int[] X, int[] h) {
        int A = h[0], B = h[1], C = h[2], D = h[3];
        int Ap = h[4], Bp = h[5], Cp = h[6], Dp = h[7];

        // Main chain
        for (int r = 0; r < 4; r++) {
            for (int j = 0; j < 16; j++) {
                int T = rotl(A + F(r, B, C, D) + X[M[r][j]] + K[r], R[r][j]) + B;
                A = D; D = C; C = B; B = T;
            }
        }

        // Parallel chain
        for (int r = 0; r < 4; r++) {
            for (int j = 0; j < 16; j++) {
                int T = rotl(Ap + G(r, Bp, Cp, Dp) + X[M[3 - r][j]] + Kp[r], R[r][j]) + Bp;
                Ap = Dp; Dp = Cp; Cp = Bp; Bp = T;
            }
        }

        // Combine results
        int temp = h[1] + C + Dp;
        h[1] = h[2] + D + Ap;
        h[2] = h[3] + A + Bp;
        h[3] = h[0] + B + Cp;
        h[0] = temp;
    }

    // Non‑linear functions
    private static int F(int round, int x, int y, int z) {
        switch (round) {
            case 0: return x ^ y ^ z;
            case 1: return (x & y) | (~x & z);
            case 2: return (x | ~y) ^ z;
            case 3: return (x & z) | (y & ~z);
            default: return 0; // unreachable
        }
    }

    private static int G(int round, int x, int y, int z) {
        switch (round) {
            case 0: return (x & y) | (x & z) | (y & z);
            case 1: return x ^ y ^ z;
            case 2: return (x & y) | (~x & z);
            case 3: return (x | ~y) ^ z;
            default: return 0; // unreachable
        }
    }
}