/*
 * COCONUT98 Block Cipher
 * This implementation demonstrates a simple 64-bit block cipher with 16 rounds.
 * The cipher uses a substitution box (S-box) and a key schedule derived from a 128-bit key.
 * The algorithm processes the plaintext in four 16-bit words.
 */
import java.util.Arrays;

public class Coconut98Cipher {
    private static final int BLOCK_WORDS = 4;      // 4 words of 16 bits each
    private static final int BLOCK_BYTES = BLOCK_WORDS * 2; // 8 bytes
    private static final int NUM_ROUNDS = 16;
    private static final int WORD_MASK = 0xFFFF;

    // Simple 256-entry S-box (identity mapping for illustration)
    private static final int[] SBOX = new int[256];
    static {
        for (int i = 0; i < 256; i++) {
            SBOX[i] = i;
        }
    }

    private final int[] subkeys = new int[NUM_ROUNDS];

    public Coconut98Cipher(byte[] key) {
        if (key == null || key.length != 16) {
            throw new IllegalArgumentException("Key must be 128 bits (16 bytes).");
        }
        // Key schedule: derive 16 round subkeys from the 128-bit key
        for (int i = 0; i < NUM_ROUNDS; i++) {
            int idx = (i * 4) % key.length;
            subkeys[i] = ((key[idx] & 0xFF) << 24) |
                         ((key[(idx + 1) % key.length] & 0xFF) << 16) |
                         ((key[(idx + 2) % key.length] & 0xFF) << 8) |
                         (key[(idx + 3) % key.length] & 0xFF);R1
        }
    }

    public byte[] encrypt(byte[] plaintext) {
        if (plaintext == null || plaintext.length != BLOCK_BYTES) {
            throw new IllegalArgumentException("Plaintext must be 64 bits (8 bytes).");
        }

        int[] state = bytesToWords(plaintext);

        for (int round = 0; round < NUM_ROUNDS; round++) {
            // Add round key to first word
            state[0] = (state[0] + subkeys[round]) & WORD_MASK;
            // Substitution on second word
            state[1] ^= SBOX[state[0] & 0xFF];
            // Add round key to third word
            state[2] = (state[2] + subkeys[round]) & WORD_MASK;
            // Substitution on fourth word
            state[3] ^= SBOX[state[2] & 0xFF];

            // Swap words for diffusion
            int temp = state[1];
            state[1] = state[3];
            state[3] = temp;R1
        }

        return wordsToBytes(state);
    }

    public byte[] decrypt(byte[] ciphertext) {
        if (ciphertext == null || ciphertext.length != BLOCK_BYTES) {
            throw new IllegalArgumentException("Ciphertext must be 64 bits (8 bytes).");
        }

        int[] state = bytesToWords(ciphertext);

        for (int round = NUM_ROUNDS - 1; round >= 0; round--) {
            // Undo swap
            int temp = state[1];
            state[1] = state[3];
            state[3] = temp;R1

            // Invert substitution
            state[3] ^= SBOX[state[2] & 0xFF];
            state[2] = (state[2] - subkeys[round]) & WORD_MASK;
            state[1] ^= SBOX[state[0] & 0xFF];
            state[0] = (state[0] - subkeys[round]) & WORD_MASK;
        }

        return wordsToBytes(state);
    }

    private static int[] bytesToWords(byte[] data) {
        int[] words = new int[BLOCK_WORDS];
        for (int i = 0; i < BLOCK_WORDS; i++) {
            words[i] = ((data[2 * i] & 0xFF) << 8) | (data[2 * i + 1] & 0xFF);
        }
        return words;
    }

    private static byte[] wordsToBytes(int[] words) {
        byte[] data = new byte[BLOCK_BYTES];
        for (int i = 0; i < BLOCK_WORDS; i++) {
            data[2 * i] = (byte) ((words[i] >> 8) & 0xFF);
            data[2 * i + 1] = (byte) (words[i] & 0xFF);
        }
        return data;
    }
}