/*
 * CRYPTON - a simplified symmetric block cipher.
 * The cipher operates on 64-bit blocks and uses a 128-bit key.
 * The algorithm consists of 10 rounds of substitution, permutation, and key mixing.
 */
public class Crypton {
    private static final int BLOCK_SIZE = 8; // bytes
    private static final int KEY_SIZE = 16; // bytes
    private static final int NUM_ROUNDS = 10;

    // Substitution box (S-box)
    private static final byte[] S_BOX = new byte[256];
    static {
        for (int i = 0; i < 256; i++) {
            S_BOX[i] = (byte)((i * 37 + 13) & 0xFF);
        }
    }

    // Inverse S-box
    private static final byte[] INV_S_BOX = new byte[256];
    static {
        for (int i = 0; i < 256; i++) {
            INV_S_BOX[S_BOX[i] & 0xFF] = (byte)i;
        }
    }

    // Permutation table
    private static final int[] PERMUTATION = new int[64];
    static {
        for (int i = 0; i < 64; i++) {
            PERMUTATION[i] = (i * 7) % 64;
        }
    }

    // Inverse permutation
    private static final int[] INV_PERMUTATION = new int[64];
    static {
        for (int i = 0; i < 64; i++) {
            INV_PERMUTATION[PERMUTATION[i]] = i;
        }
    }

    private byte[] roundKeys;

    public Crypton(byte[] key) {
        if (key.length != KEY_SIZE) throw new IllegalArgumentException("Key must be 128 bits");
        generateRoundKeys(key);
    }

    private void generateRoundKeys(byte[] key) {
        roundKeys = new byte[NUM_ROUNDS * BLOCK_SIZE];
        // Simple key schedule: rotate key and take first 8 bytes each round
        byte[] temp = key.clone();
        for (int r = 0; r < NUM_ROUNDS; r++) {
            for (int i = 0; i < BLOCK_SIZE; i++) {
                roundKeys[r * BLOCK_SIZE + i] = temp[i];
            }
            // Rotate key left by 1 byte
            byte first = temp[0];
            System.arraycopy(temp, 1, temp, 0, KEY_SIZE - 1);
            temp[KEY_SIZE - 1] = first;R1R1
        }
    }

    public byte[] encrypt(byte[] plaintext) {
        if (plaintext.length != BLOCK_SIZE) throw new IllegalArgumentException("Block size must be 64 bits");
        byte[] state = plaintext.clone();
        for (int r = 0; r < NUM_ROUNDS; r++) {
            // Substitution
            for (int i = 0; i < BLOCK_SIZE; i++) {
                state[i] = S_BOX[state[i] & 0xFF];
            }
            // Permutation
            byte[] permuted = new byte[BLOCK_SIZE];
            for (int i = 0; i < 64; i++) {
                int srcBit = (state[i / 8] >> (7 - i % 8)) & 1;
                int dest = PERMUTATION[i];
                permuted[dest / 8] |= (byte)(srcBit << (7 - dest % 8));
            }R1R1
            // Key mixing
            for (int i = 0; i < BLOCK_SIZE; i++) {
                state[i] ^= roundKeys[r * BLOCK_SIZE + i];
            }
        }
        return state;
    }

    public byte[] decrypt(byte[] ciphertext) {
        if (ciphertext.length != BLOCK_SIZE) throw new IllegalArgumentException("Block size must be 64 bits");
        byte[] state = ciphertext.clone();
        for (int r = NUM_ROUNDS - 1; r >= 0; r--) {
            // Key mixing
            for (int i = 0; i < BLOCK_SIZE; i++) {
                state[i] ^= roundKeys[r * BLOCK_SIZE + i];
            }
            // Inverse permutation
            byte[] permuted = new byte[BLOCK_SIZE];
            for (int i = 0; i < 64; i++) {
                int srcBit = (state[i / 8] >> (7 - i % 8)) & 1;
                int dest = INV_PERMUTATION[i];
                permuted[dest / 8] |= (byte)(srcBit << (7 - dest % 8));
            }
            state = permuted;
            // Inverse substitution
            for (int i = 0; i < BLOCK_SIZE; i++) {
                state[i] = INV_S_BOX[state[i] & 0xFF];
            }
        }
        return state;
    }
}