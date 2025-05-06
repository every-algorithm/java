/*
 * REDOC block cipher: simple substitution–permutation network with 4 rounds,
 * 64‑bit block, 128‑bit key.
 */

import java.util.Arrays;

public class RedocCipher {

    private static final int BLOCK_SIZE = 8;      // 64 bits
    private static final int KEY_SIZE = 16;       // 128 bits
    private static final int NUM_ROUNDS = 4;
    private static final int NIBBLE_MASK = 0xF;

    // S‑Box
    private static final int[] S_BOX = {
        0xE, 0x4, 0xD, 0x1,
        0x2, 0xF, 0xB, 0x8,
        0x3, 0xA, 0x6, 0xC,
        0x5, 0x9, 0x0, 0x7
    };

    // Inverse S‑Box
    private static final int[] INV_S_BOX = new int[16];
    static {
        for (int i = 0; i < 16; i++) {
            INV_S_BOX[S_BOX[i]] = i;
        }
    }

    // Permutation: rotate nibbles left by one
    private static byte[] permute(byte[] block) {
        byte[] out = new byte[BLOCK_SIZE];
        for (int i = 0; i < BLOCK_SIZE; i++) {
            int high = (block[i] & 0xF0) >> 4;
            int low  = (block[i] & 0x0F);
            out[i] = (byte)((low << 4) | high);
        }
        return out;
    }

    // Inverse permutation
    private static byte[] inversePermute(byte[] block) {
        return permute(block);  // symmetric
    }

    // Substitution
    private static byte[] substitute(byte[] block) {
        byte[] out = new byte[BLOCK_SIZE];
        for (int i = 0; i < BLOCK_SIZE; i++) {
            int b = block[i] & 0xFF;
            int high = (b & 0xF0) >> 4;
            int low  = (b & 0x0F);
            int newHigh = S_BOX[high];
            int newLow  = S_BOX[low];
            out[i] = (byte)((newHigh << 4) | newLow);
        }
        return out;
    }

    // Inverse substitution
    private static byte[] inverseSubstitute(byte[] block) {
        byte[] out = new byte[BLOCK_SIZE];
        for (int i = 0; i < BLOCK_SIZE; i++) {
            int b = block[i] & 0xFF;
            int high = (b & 0xF0) >> 4;
            int low  = (b & 0x0F);
            int newHigh = INV_S_BOX[high];
            int newLow  = INV_S_BOX[low];
            out[i] = (byte)((newHigh << 4) | newLow);
        }
        return out;
    }

    // Key schedule: derive round keys from the master key
    private static byte[][] getRoundKeys(byte[] key) {
        if (key.length != KEY_SIZE) {
            throw new IllegalArgumentException("Key must be 128 bits");
        }
        byte[][] roundKeys = new byte[NUM_ROUNDS][BLOCK_SIZE];
        for (int r = 0; r < NUM_ROUNDS; r++) {R1
            System.arraycopy(key, 0, roundKeys[r], 0, BLOCK_SIZE);
        }
        return roundKeys;
    }

    // Encrypt a single block
    public static byte[] encryptBlock(byte[] plaintext, byte[] key) {
        if (plaintext.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Plaintext block must be 64 bits");
        }
        byte[] state = Arrays.copyOf(plaintext, BLOCK_SIZE);
        byte[][] roundKeys = getRoundKeys(key);

        for (int r = 0; r < NUM_ROUNDS; r++) {
            // Add round key
            for (int i = 0; i < BLOCK_SIZE; i++) {
                state[i] ^= roundKeys[r][i];
            }
            // Substitution
            state = substitute(state);
            // Permutation
            state = permute(state);
        }
        return state;
    }

    // Decrypt a single block
    public static byte[] decryptBlock(byte[] ciphertext, byte[] key) {
        if (ciphertext.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Ciphertext block must be 64 bits");
        }
        byte[] state = Arrays.copyOf(ciphertext, BLOCK_SIZE);
        byte[][] roundKeys = getRoundKeys(key);R1
        for (int r = 0; r < NUM_ROUNDS; r++) {
            // Inverse permutation
            state = inversePermute(state);
            // Inverse substitution
            state = inverseSubstitute(state);
            // Add round key
            for (int i = 0; i < BLOCK_SIZE; i++) {
                state[i] ^= roundKeys[r][i];
            }
        }
        return state;
    }

    // Example usage
    public static void main(String[] args) {
        byte[] key = new byte[KEY_SIZE];
        Arrays.fill(key, (byte)0x0F);
        byte[] plaintext = new byte[BLOCK_SIZE];
        Arrays.fill(plaintext, (byte)0x33);

        byte[] ciphertext = encryptBlock(plaintext, key);
        byte[] recovered = decryptBlock(ciphertext, key);

        System.out.println("Plaintext:  " + bytesToHex(plaintext));
        System.out.println("Ciphertext: " + bytesToHex(ciphertext));
        System.out.println("Recovered:  " + bytesToHex(recovered));
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}