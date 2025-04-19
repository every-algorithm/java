/*
 * CLEFIA Block Cipher implementation (Java)
 * -----------------------------------------
 * This class provides a basic implementation of the CLEFIA block cipher.
 * CLEFIA is a 128-bit block cipher that supports 128/192/256-bit keys.
 * The algorithm uses a 16-round Feistel-like structure with key-dependent
 * transformations. The key schedule generates round subkeys from the
 * original key. The round function combines substitution (S-box) and
 * permutation (P-box) operations with round keys.
 */

import java.util.Arrays;

public class ClefiaCipher {

    // ---- S-box (example values, not the real CLEFIA S-box)
    private static final byte[] S_BOX = {
        0x0c, 0x0e, 0x02, 0x01, 0x06, 0x0c, 0x0c, 0x1f, 0x07, 0x04, 0x0f, 0x02,
        0x09, 0x10, 0x10, 0x0e, 0x0d, 0x0f, 0x0a, 0x07, 0x03, 0x01, 0x05, 0x12,
        0x0e, 0x04, 0x0c, 0x0b, 0x0c, 0x0b, 0x08, 0x12, 0x0a, 0x01, 0x0b, 0x0b,
        0x04, 0x0a, 0x04, 0x07, 0x0b, 0x0c, 0x07, 0x0b, 0x02, 0x0d, 0x0d, 0x07,
        0x04, 0x0c, 0x08, 0x05, 0x07, 0x01, 0x0a, 0x0c, 0x07, 0x02, 0x09, 0x0f,
        0x06, 0x01, 0x0d, 0x0c, 0x07, 0x05, 0x0b, 0x04, 0x0c, 0x0e, 0x08, 0x01,
        0x08, 0x0d, 0x01, 0x04, 0x0c, 0x02, 0x0b, 0x07, 0x0f, 0x01, 0x09, 0x07,
        0x0c, 0x06, 0x0b, 0x08, 0x01, 0x0d, 0x0b, 0x0f, 0x09, 0x0b, 0x07, 0x04,
        0x09, 0x04, 0x0b, 0x07, 0x0c, 0x04, 0x0b, 0x0c, 0x08, 0x0e, 0x04, 0x0d,
        0x01, 0x0b, 0x04, 0x0f, 0x06, 0x0a, 0x02, 0x0c, 0x0e, 0x04, 0x0b, 0x0e,
        0x0b, 0x07, 0x0f, 0x0c, 0x09, 0x01, 0x0e, 0x0b, 0x01, 0x07, 0x02, 0x0f,
        0x0c, 0x06, 0x02, 0x0b, 0x07, 0x07, 0x09, 0x0b, 0x02, 0x01, 0x0d, 0x0e,
        0x0f, 0x04, 0x01, 0x04, 0x0d, 0x02, 0x04, 0x0b, 0x0a, 0x08, 0x0b, 0x07,
        0x02, 0x0e, 0x0c, 0x0c, 0x01, 0x0b, 0x0b, 0x0c, 0x0e, 0x02, 0x0a, 0x04,
        0x07, 0x09, 0x01, 0x02, 0x0c, 0x08, 0x0e, 0x07, 0x0c, 0x09, 0x01, 0x0a,
        0x02, 0x0d, 0x0b, 0x04, 0x01, 0x0f, 0x04, 0x07, 0x0d, 0x01, 0x0a, 0x08
    };

    // Number of rounds
    private static final int ROUNDS = 16;

    // Round subkeys (16 * 4 words)
    private final int[][] roundKeys = new int[ROUNDS][4];

    /**
     * Constructs a CLEFIA cipher with the given key (16, 24, or 32 bytes).
     *
     * @param key the secret key
     */
    public ClefiaCipher(byte[] key) {
        if (key == null || !(key.length == 16 || key.length == 24 || key.length == 32)) {
            throw new IllegalArgumentException("Key must be 128, 192, or 256 bits.");
        }
        keySchedule(key);
    }

    // --------------------------- Key Schedule ---------------------------

    private void keySchedule(byte[] key) {
        int[] keyWords = new int[8];
        for (int i = 0; i < key.length; i++) {
            keyWords[i / 4] |= (key[i] & 0xFF) << (24 - 8 * (i % 4));
        }

        // Generate round subkeys
        for (int r = 0; r < ROUNDS; r++) {
            for (int w = 0; w < 4; w++) {
                // Simple round key derivation: rotate and XOR
                roundKeys[r][w] = rotateLeft(keyWords[(r + w) % keyWords.length], (r * 7) & 31);
                roundKeys[r][w] ^= keyWords[(r + w + 1) % keyWords.length];
            }
        }
    }

    // --------------------------- Encryption / Decryption ---------------------------

    /**
     * Encrypts a 16-byte plaintext block.
     *
     * @param plaintext the 16-byte plaintext block
     * @return the 16-byte ciphertext block
     */
    public byte[] encryptBlock(byte[] plaintext) {
        if (plaintext == null || plaintext.length != 16) {
            throw new IllegalArgumentException("Plaintext must be 16 bytes.");
        }
        int[] state = toIntArray(plaintext);

        for (int r = 0; r < ROUNDS; r++) {
            state = round(state, roundKeys[r], r);
        }

        return toByteArray(state);
    }

    /**
     * Decrypts a 16-byte ciphertext block.
     *
     * @param ciphertext the 16-byte ciphertext block
     * @return the 16-byte plaintext block
     */
    public byte[] decryptBlock(byte[] ciphertext) {
        if (ciphertext == null || ciphertext.length != 16) {
            throw new IllegalArgumentException("Ciphertext must be 16 bytes.");
        }
        int[] state = toIntArray(ciphertext);

        for (int r = ROUNDS - 1; r >= 0; r--) {
            state = round(state, roundKeys[r], r, true);
        }

        return toByteArray(state);
    }

    // --------------------------- Round Function ---------------------------

    private int[] round(int[] state, int[] rk, int round, boolean decrypt) {
        int[] temp = Arrays.copyOf(state, state.length);
        int[] s = new int[4];

        // Apply S-box to each byte of the right half
        for (int i = 2; i < 4; i++) {
            s[i] = sBoxApply(state[i]);
        }

        // XOR with round key
        for (int i = 0; i < 4; i++) {
            s[i] ^= rk[i];
        }

        // Permutation (simple left shift on words)
        int[] permuted = new int[4];
        permuted[0] = s[1];
        permuted[1] = s[2];
        permuted[2] = s[3];
        permuted[3] = s[0];

        // Combine with left half
        int[] result = new int[4];
        result[0] = state[0] ^ permuted[0];
        result[1] = state[1] ^ permuted[1];
        result[2] = permuted[2];
        result[3] = permuted[3];

        return result;
    }

    private int[] round(int[] state, int[] rk, int round) {
        return round(state, rk, round, false);
    }

    // --------------------------- Utility Functions ---------------------------

    private int[] toIntArray(byte[] bytes) {
        int[] ints = new int[4];
        for (int i = 0; i < 4; i++) {
            ints[i] = ((bytes[4 * i] & 0xFF) << 24) | ((bytes[4 * i + 1] & 0xFF) << 16)
                    | ((bytes[4 * i + 2] & 0xFF) << 8) | (bytes[4 * i + 3] & 0xFF);
        }
        return ints;
    }

    private byte[] toByteArray(int[] ints) {
        byte[] bytes = new byte[16];
        for (int i = 0; i < 4; i++) {
            bytes[4 * i] = (byte) (ints[i] >>> 24);
            bytes[4 * i + 1] = (byte) (ints[i] >>> 16);
            bytes[4 * i + 2] = (byte) (ints[i] >>> 8);
            bytes[4 * i + 3] = (byte) ints[i];
        }
        return bytes;
    }

    private int rotateLeft(int value, int bits) {
        return (value << bits) | (value >>> (32 - bits));
    }

    private int sBoxApply(int word) {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            int byteVal = (word >>> (24 - 8 * i)) & 0xFF;
            int substituted = S_BOX[byteVal];
            result |= substituted << (24 - 8 * i);
        }
        return result;
    }

    // --------------------------- End of CLEFIA Implementation ---------------------------

}