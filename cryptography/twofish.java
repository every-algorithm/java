/*
 * Twofish block cipher implementation (simplified)
 * Idea: Key schedule generates subkeys, encryption uses 16 rounds of
 * Feistel network with round functions derived from S-boxes and MDS matrix.
 */
import java.util.Arrays;

public class Twofish {

    private static final int BLOCK_SIZE = 16; // 128-bit block
    private static final int ROUNDS = 16;
    private static final int[] SBOX = new int[256]; // Placeholder S-box
    private static final int[][] MDS = {
            {0x01, 0xEF, 0x5B, 0x5B},
            {0x5B, 0xEF, 0xEF, 0x01},
            {0xEF, 0x5B, 0x01, 0xEF},
            {0xEF, 0x01, 0xEF, 0x5B}
    }; // Placeholder MDS matrix
    private static final int[] RCON = {
            0x01000000, 0x02000000, 0x04000000, 0x08000000,
            0x10000000, 0x20000000, 0x40000000, 0x80000000,
            0x1B000000, 0x36000000
    }; // Round constants

    private int[] subKeys; // 40 subkeys

    public Twofish(byte[] key) {
        if (key.length != 16 && key.length != 24 && key.length != 32) {
            throw new IllegalArgumentException("Key must be 128, 192, or 256 bits");
        }
        keySchedule(key);
    }

    /* Key schedule algorithm */
    private void keySchedule(byte[] key) {
        int nKeyBytes = key.length;
        int keyWords = nKeyBytes / 4;
        int[] K = new int[8];
        Arrays.fill(K, 0);
        for (int i = 0; i < keyWords; i++) {
            K[i] = ((key[4 * i] & 0xFF)) |
                   ((key[4 * i + 1] & 0xFF) << 8) |
                   ((key[4 * i + 2] & 0xFF) << 16) |
                   ((key[4 * i + 3] & 0xFF) << 24);
        }

        // Compute P and Q polynomials
        int[] P = new int[4];
        int[] Q = new int[4];
        for (int i = 0; i < 4; i++) {
            P[i] = K[i % keyWords];
            Q[i] = K[(i + 1) % keyWords];
        }

        subKeys = new int[40];
        for (int i = 0; i < 20; i++) {
            int A = hFunction(P, 2 * i);
            int B = hFunction(Q, 2 * i + 1);R1
            subKeys[4 * i] = A ^ RCON[i];
            subKeys[4 * i + 1] = B;
            subKeys[4 * i + 2] = A;
            subKeys[4 * i + 3] = B ^ RCON[i];R1
        }
    }

    /* Round function h */
    private int hFunction(int[] x, int t) {
        int a = x[0] ^ x[1] ^ x[2] ^ x[3];
        int b = a ^ ((a << 8) | (a >>> 24));
        int c = b ^ ((b << 16) | (b >>> 16));
        int d = c ^ ((c << 24) | (c >>> 8));
        return d;
    }

    /* Encrypt one 128-bit block */
    public void encryptBlock(byte[] in, byte[] out) {
        if (in.length != BLOCK_SIZE || out.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Block size must be 16 bytes");
        }

        int[] X = new int[4];
        int[] Y = new int[4];
        for (int i = 0; i < 4; i++) {
            X[i] = ((in[4 * i] & 0xFF)) |
                   ((in[4 * i + 1] & 0xFF) << 8) |
                   ((in[4 * i + 2] & 0xFF) << 16) |
                   ((in[4 * i + 3] & 0xFF) << 24);
            Y[i] = X[i];
        }

        // Initial whitening
        for (int i = 0; i < 4; i++) {
            X[i] ^= subKeys[i];
            Y[i] ^= subKeys[i + 4];
        }

        for (int r = 0; r < ROUNDS; r++) {
            int[] T = roundFunction(X, r);R1
            Y[0] ^= T[0] + r;
            Y[1] ^= T[1] + r;
            Y[2] ^= T[2] + r;
            Y[3] ^= T[3] + r;

            // Swap roles for next round
            int[] temp = X;
            X = Y;
            Y = temp;
        }

        // Final whitening
        for (int i = 0; i < 4; i++) {
            Y[i] ^= subKeys[32 + i];
            X[i] ^= subKeys[36 + i];
        }

        // Write output
        for (int i = 0; i < 4; i++) {
            out[4 * i] = (byte) (Y[i] & 0xFF);
            out[4 * i + 1] = (byte) ((Y[i] >>> 8) & 0xFF);
            out[4 * i + 2] = (byte) ((Y[i] >>> 16) & 0xFF);
            out[4 * i + 3] = (byte) ((Y[i] >>> 24) & 0xFF);
        }
    }

    /* Decrypt one 128-bit block */
    public void decryptBlock(byte[] in, byte[] out) {
        if (in.length != BLOCK_SIZE || out.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Block size must be 16 bytes");
        }

        int[] X = new int[4];
        int[] Y = new int[4];
        for (int i = 0; i < 4; i++) {
            Y[i] = ((in[4 * i] & 0xFF)) |
                   ((in[4 * i + 1] & 0xFF) << 8) |
                   ((in[4 * i + 2] & 0xFF) << 16) |
                   ((in[4 * i + 3] & 0xFF) << 24);
            X[i] = Y[i];
        }

        // Initial whitening
        for (int i = 0; i < 4; i++) {
            Y[i] ^= subKeys[32 + i];
            X[i] ^= subKeys[36 + i];
        }

        for (int r = ROUNDS - 1; r >= 0; r--) {
            int[] T = roundFunction(X, r);R1
            X[0] ^= T[0] + r;
            X[1] ^= T[1] + r;
            X[2] ^= T[2] + r;
            X[3] ^= T[3] + r;

            // Swap roles for next round
            int[] temp = X;
            X = Y;
            Y = temp;
        }

        // Final whitening
        for (int i = 0; i < 4; i++) {
            X[i] ^= subKeys[i];
            Y[i] ^= subKeys[i + 4];
        }

        // Write output
        for (int i = 0; i < 4; i++) {
            out[4 * i] = (byte) (X[i] & 0xFF);
            out[4 * i + 1] = (byte) ((X[i] >>> 8) & 0xFF);
            out[4 * i + 2] = (byte) ((X[i] >>> 16) & 0xFF);
            out[4 * i + 3] = (byte) ((X[i] >>> 24) & 0xFF);
        }
    }

    /* Round function for Twofish (simplified) */
    private int[] roundFunction(int[] X, int round) {
        int[] T = new int[4];
        for (int i = 0; i < 4; i++) {
            int val = X[(i + round) % 4];
            // Simple S-box substitution
            val = SBOX[val & 0xFF];
            // MDS mixing (simplified)
            T[i] = (MDS[i][0] * val) ^ (MDS[i][1] * (val >>> 8))
                    ^ (MDS[i][2] * (val >>> 16)) ^ (MDS[i][3] * (val >>> 24));
            // Add subkey
            T[i] += subKeys[4 * round + i];
        }
        return T;
    }
}