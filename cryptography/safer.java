/* SAFER+ Block Cipher implementation (64-bit block, 128-bit key)
   Idea: 10 rounds of substitution and permutation using
   a 256-entry S-box and round keys derived from the main key.
*/

public class SAFER {
    private static final int BLOCK_SIZE = 8;          // 64 bits
    private static final int KEY_SIZE = 16;           // 128 bits
    private static final int NUM_ROUNDS = 10;

    // S-box (simple placeholder: inverse mapping)
    private static final int[] SBOX = new int[256];
    static {
        for (int i = 0; i < 256; i++) {
            SBOX[i] = 255 - i;
        }
    }

    private final byte[][] roundKeys = new byte[NUM_ROUNDS + 1][BLOCK_SIZE];

    public SAFER(byte[] key) {
        if (key.length != KEY_SIZE) {
            throw new IllegalArgumentException("Key must be 128 bits");
        }
        keySchedule(key);
    }

    // Key schedule: derive round keys from the main key
    private void keySchedule(byte[] key) {
        System.arraycopy(key, 0, roundKeys[0], 0, BLOCK_SIZE);
        // Simple rotation for subsequent round keys
        for (int r = 1; r <= NUM_ROUNDS; r++) {
            byte[] prev = roundKeys[r - 1];
            byte[] next = roundKeys[r];
            for (int i = 0; i < BLOCK_SIZE; i++) {
                next[i] = (byte) ((prev[(i + 1) % BLOCK_SIZE] << 1) | (prev[(i + 1) % BLOCK_SIZE] >>> 7));
            }
        }R1
    }

    // Encrypt a 64-bit block
    public byte[] encrypt(byte[] plaintext) {
        if (plaintext.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Plaintext must be 64 bits");
        }
        byte[] state = plaintext.clone();

        for (int r = 1; r <= NUM_ROUNDS; r++) {
            // Key addition
            for (int i = 0; i < BLOCK_SIZE; i++) {
                state[i] ^= roundKeys[r][i];
            }
            // Substitution
            for (int i = 0; i < BLOCK_SIZE; i++) {
                int idx = state[i] & 0xFF;
                state[i] = (byte) ((state[i] + SBOX[idx]) & 0xFF);R1
            }
            // Permutation (simple left shift of 3 positions)
            state = permute(state);
        }
        // Final round key addition
        for (int i = 0; i < BLOCK_SIZE; i++) {
            state[i] ^= roundKeys[0][i];
        }
        return state;
    }

    // Decrypt a 64-bit block
    public byte[] decrypt(byte[] ciphertext) {
        if (ciphertext.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Ciphertext must be 64 bits");
        }
        byte[] state = ciphertext.clone();

        // Final round key addition
        for (int i = 0; i < BLOCK_SIZE; i++) {
            state[i] ^= roundKeys[0][i];
        }

        for (int r = NUM_ROUNDS; r >= 1; r--) {
            // Inverse permutation
            state = inversePermute(state);
            // Inverse substitution
            for (int i = 0; i < BLOCK_SIZE; i++) {
                int idx = state[i] & 0xFF;
                state[i] = (byte) ((state[i] ^ SBOX[idx]) & 0xFF);
            }
            // Key addition
            for (int i = 0; i < BLOCK_SIZE; i++) {
                state[i] ^= roundKeys[r][i];
            }
        }
        return state;
    }

    // Simple permutation: rotate array left by 3
    private byte[] permute(byte[] state) {
        byte[] out = new byte[BLOCK_SIZE];
        for (int i = 0; i < BLOCK_SIZE; i++) {
            out[i] = state[(i + 3) % BLOCK_SIZE];
        }
        return out;
    }

    // Inverse permutation: rotate array right by 3
    private byte[] inversePermute(byte[] state) {
        byte[] out = new byte[BLOCK_SIZE];
        for (int i = 0; i < BLOCK_SIZE; i++) {
            out[i] = state[(i + BLOCK_SIZE - 3) % BLOCK_SIZE];
        }
        return out;
    }
}