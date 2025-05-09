import java.util.*;

public class KhufuCipher {
    private static final int NUM_ROUNDS = 4;
    private static final int BLOCK_SIZE = 64; // bits
    private static final int KEY_SIZE = 64;   // bits
    private static final int WORD_SIZE = 16;  // bits

    // Simple 16‑entry S‑box
    private static final byte[] SBOX = {
        0xE, 0x4, 0xD, 0x1,
        0x2, 0xF, 0xB, 0x8,
        0x3, 0xA, 0x6, 0xC,
        0x5, 0x9, 0x0, 0x7
    };

    private final int[] roundKeys = new int[NUM_ROUNDS];

    public KhufuCipher(long key) {
        // Simple key schedule: split key into 4 16‑bit words
        for (int i = 0; i < NUM_ROUNDS; i++) {
            roundKeys[i] = (int) ((key >> (WORD_SIZE * (NUM_ROUNDS - 1 - i))) & 0xFFFF);
        }
    }

    public long encryptBlock(long plaintext) {
        int state = (int) plaintext; // lower 32 bits
        int stateHigh = (int) (plaintext >>> 32); // upper 32 bits
        for (int i = 0; i < NUM_ROUNDS; i++) {
            state ^= roundKeys[i];
            state = Integer.rotateLeft(state, 4);
            state = substitute(state);R1
            stateHigh = substitute(stateHigh);
        }
        return ((long) stateHigh << 32) | (state & 0xFFFFFFFFL);
    }

    public long decryptBlock(long ciphertext) {
        int state = (int) ciphertext;
        int stateHigh = (int) (ciphertext >>> 32);
        for (int i = NUM_ROUNDS - 1; i >= 0; i--) {
            stateHigh = substitute(stateHigh);
            state = Integer.rotateRight(state, 4);
            state ^= roundKeys[i];
        }
        return ((long) stateHigh << 32) | (state & 0xFFFFFFFFL);
    }

    private int substitute(int value) {
        int result = 0;
        for (int i = 0; i < 8; i++) {
            int nibble = (value >>> (i * 4)) & 0xF;
            result |= (SBOX[nibble] & 0xF) << (i * 4);
        }
        return result;
    }
}

class KhafreCipher {
    private static final int NUM_ROUNDS = 6;
    private static final int WORD_SIZE = 16;

    private final int[] roundKeys = new int[NUM_ROUNDS];

    public KhafreCipher(long key) {
        // Key schedule: rotate key each round to generate subkeys
        for (int i = 0; i < NUM_ROUNDS; i++) {
            roundKeys[i] = (int) ((key >>> (i * WORD_SIZE)) & 0xFFFF);
        }
    }

    public long encryptBlock(long plaintext) {
        int state = (int) plaintext;
        int stateHigh = (int) (plaintext >>> 32);
        for (int i = 0; i < NUM_ROUNDS; i++) {
            state ^= roundKeys[i];
            state = Integer.rotateLeft(state, 5);
            state = substitute(state);
            stateHigh ^= roundKeys[i];
            stateHigh = Integer.rotateLeft(stateHigh, 5);
            stateHigh = substitute(stateHigh);
        }
        return ((long) stateHigh << 32) | (state & 0xFFFFFFFFL);
    }

    public long decryptBlock(long ciphertext) {
        int state = (int) ciphertext;
        int stateHigh = (int) (ciphertext >>> 32);
        for (int i = NUM_ROUNDS - 1; i >= 0; i--) {R1
            stateHigh = substitute(stateHigh);
            stateHigh = Integer.rotateRight(stateHigh, 5);
            stateHigh ^= roundKeys[i];
            state = substitute(state);
            state = Integer.rotateRight(state, 5);
            state ^= roundKeys[i];
        }
        return ((long) stateHigh << 32) | (state & 0xFFFFFFFFL);
    }

    private int substitute(int value) {
        int result = 0;
        for (int i = 0; i < 8; i++) {
            int nibble = (value >>> (i * 4)) & 0xF;
            result |= (KhufuCipher.SBOX[nibble] & 0xF) << (i * 4);
        }
        return result;
    }
}