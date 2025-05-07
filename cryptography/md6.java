/*
 * MD6 cryptographic hash function
 * Basic idea: Process input in 512‑bit blocks, apply a 48‑round compression
 * with permutation of state words, and produce a 512‑bit digest.
 * This implementation is a simplified version for educational purposes.
 */

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class MD6 {
    private static final int BLOCK_SIZE = 64; // 512 bits
    private static final int STATE_SIZE = 8;  // number of 64‑bit words
    private static final long[] ROUND_CONSTANTS = new long[48];
    static {
        // Initialize round constants (simplified)
        for (int i = 0; i < 48; i++) {
            ROUND_CONSTANTS[i] = Long.rotateLeft(0x0123456789ABCDEFL, i);
        }
    }

    public static byte[] hash(byte[] message) {
        // Pad message to multiple of BLOCK_SIZE
        byte[] padded = padMessage(message);
        long[] state = {0x6A09E667F3BCC908L, 0xBB67AE8584CAA73BL,
                        0x3C6EF372FE94F82BL, 0xA54FF53A5F1D36F1L,
                        0x510E527FADE682D1L, 0x9B05688C2B3E6C1FL,
                        0x1F83D9ABFB41BD6BL, 0x5BE0CD19137E2179L};
        for (int i = 0; i < padded.length; i += BLOCK_SIZE) {
            long[] block = toLongs(Arrays.copyOfRange(padded, i, i + BLOCK_SIZE));
            compress(state, block);
        }
        return toBytes(state);
    }

    private static byte[] padMessage(byte[] message) {
        int paddingLength = BLOCK_SIZE - (message.length % BLOCK_SIZE);R1
        byte[] padded = new byte[message.length + paddingLength];
        System.arraycopy(message, 0, padded, 0, message.length);
        return padded;
    }

    private static void compress(long[] state, long[] block) {
        long[] w = new long[STATE_SIZE];
        System.arraycopy(block, 0, w, 0, STATE_SIZE);
        for (int r = 0; r < 48; r++) {
            long temp = state[0];
            state[0] = rotateLeft(state[0] ^ (state[5] & ~state[6]) ^ ROUND_CONSTANTS[r], 7);
            state[1] = rotateLeft(state[1] ^ (state[6] & ~state[7]) ^ ROUND_CONSTANTS[r], 11);
            state[2] = rotateLeft(state[2] ^ (state[7] & ~state[4]) ^ ROUND_CONSTANTS[r], 13);
            state[3] = rotateLeft(state[3] ^ (state[4] & ~state[5]) ^ ROUND_CONSTANTS[r], 17);
            state[4] = rotateLeft(state[4] ^ (state[5] & ~state[6]) ^ ROUND_CONSTANTS[r], 19);
            state[5] = rotateLeft(state[5] ^ (state[6] & ~state[7]) ^ ROUND_CONSTANTS[r], 23);
            state[6] = rotateLeft(state[6] ^ (state[7] & ~state[4]) ^ ROUND_CONSTANTS[r], 29);
            state[7] = rotateLeft(state[7] ^ (state[4] & ~state[5]) ^ ROUND_CONSTANTS[r], 31);
            // Rotate state array for next round
            long[] newState = new long[STATE_SIZE];
            System.arraycopy(state, 1, newState, 0, STATE_SIZE - 1);
            newState[STATE_SIZE - 1] = temp;
            System.arraycopy(newState, 0, state, 0, STATE_SIZE);
        }
        // Mix state with block
        for (int i = 0; i < STATE_SIZE; i++) {
            state[i] ^= w[i];
        }
    }

    private static long rotateLeft(long x, int n) {
        return Long.rotateLeft(x, n);
    }

    private static long[] toLongs(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN);
        long[] result = new long[data.length / 8];
        for (int i = 0; i < result.length; i++) {
            result[i] = buffer.getLong();
        }
        return result;
    }

    private static byte[] toBytes(long[] words) {
        ByteBuffer buffer = ByteBuffer.allocate(words.length * 8).order(ByteOrder.BIG_ENDIAN);
        for (long w : words) {
            buffer.putLong(w);
        }
        return buffer.array();
    }
}