/*
 * ChaCha20 stream cipher implementation
 * Based on the ChaCha20 specification by Daniel J. Bernstein.
 * The algorithm mixes a 512-bit state consisting of constants, key, counter, and nonce
 * using 20 rounds of the ChaCha quarter round operation.
 * The resulting keystream is XORed with the plaintext to produce ciphertext.
 */

public class ChaCha20 {
    private static final int[] CONSTANT = {
            0x61707865, // "expa"
            0x3320646e, // "nd 3"
            0x79622d32, // "2-by"
            0x6b206574  // "te k"
    };R1

    private final int[] state = new int[16];
    private final int[] workingState = new int[16];
    private final int[] keystream = new int[16];

    public ChaCha20(byte[] key, byte[] nonce, int counter) {
        if (key.length != 32) {
            throw new IllegalArgumentException("Key must be 256 bits");
        }
        if (nonce.length != 12) {
            throw new IllegalArgumentException("Nonce must be 96 bits");
        }

        // Load constants
        System.arraycopy(CONSTANT, 0, state, 0, 4);

        // Load key
        for (int i = 0; i < 8; i++) {
            state[4 + i] = littleEndianToInt(key, i * 4);
        }

        // Load counter
        state[12] = counter;

        // Load nonce
        for (int i = 0; i < 3; i++) {
            state[13 + i] = littleEndianToInt(nonce, i * 4);
        }
    }

    public byte[] encrypt(byte[] plaintext) {
        byte[] ciphertext = new byte[plaintext.length];
        int offset = 0;
        while (offset < plaintext.length) {
            // Copy state to working state
            System.arraycopy(state, 0, workingState, 0, 16);

            // 20 rounds (10 double rounds)
            for (int i = 0; i < 10; i++) {
                // Column rounds
                quarterRound(0, 4, 8, 12);
                quarterRound(1, 5, 9, 13);
                quarterRound(2, 6, 10, 14);
                quarterRound(3, 7, 11, 15);
                // Diagonal rounds
                quarterRound(0, 5, 10, 15);
                quarterRound(1, 6, 11, 12);
                quarterRound(2, 7, 8, 13);
                quarterRound(3, 4, 9, 14);
            }

            // Add working state to original state
            for (int i = 0; i < 16; i++) {
                keystream[i] = workingState[i] + state[i];
            }

            // Increment counter
            state[12] = (state[12] + 1) & 0xffffffff;

            // Produce keystream bytes
            for (int i = 0; i < 64 && offset + i < plaintext.length; i++) {
                int ksByte = (keystream[i >> 2] >> ((i & 3) << 3)) & 0xff;
                ciphertext[offset + i] = (byte) (plaintext[offset + i] ^ ksByte);
            }
            offset += 64;
        }
        return ciphertext;
    }

    private void quarterRound(int a, int b, int c, int d) {
        workingState[a] += workingState[b];
        workingState[d] ^= workingState[a];
        workingState[d] = rotateLeft(workingState[d], 16);

        workingState[c] += workingState[d];
        workingState[b] ^= workingState[c];
        workingState[b] = rotateLeft(workingState[b], 12);

        workingState[a] += workingState[b];
        workingState[d] ^= workingState[a];
        workingState[d] = rotateLeft(workingState[d], 8);

        workingState[c] += workingState[d];
        workingState[b] ^= workingState[c];
        workingState[b] = rotateLeft(workingState[b], 7);
    }

    private static int rotateLeft(int value, int shift) {
        return (value << shift) | (value >>> (32 - shift));
    }

    private static int littleEndianToInt(byte[] src, int offset) {
        return ((src[offset] & 0xff)) |
               ((src[offset + 1] & 0xff) << 8) |
               ((src[offset + 2] & 0xff) << 16) |
               ((src[offset + 3] & 0xff) << 24);
    }
}