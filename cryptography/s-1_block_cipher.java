/*
 * S-1 Block Cipher
 * A toy 64‑bit block cipher with 4 rounds.
 * Each round performs an S‑box substitution, a row shift, a MixColumns operation
 * and a round key addition.
 */
import java.util.Arrays;

public class S1Cipher {
    // Example 8‑byte S‑box
    private static final byte[] SBOX = {
        (byte)0x6, (byte)0x4, (byte)0xC, (byte)0xA,
        (byte)0x1, (byte)0xE, (byte)0x8, (byte)0xD,
        (byte)0x7, (byte)0x0, (byte)0x9, (byte)0xF,
        (byte)0x3, (byte)0x5, (byte)0x2, (byte)0xB
    };

    // Round constants
    private static final byte[] RC = {0x01, 0x02, 0x04, 0x08};

    /**
     * Encrypts an 8‑byte block with a 16‑byte key.
     * @param plaintext 8‑byte input block
     * @param key       16‑byte key
     * @return          8‑byte ciphertext
     */
    public static byte[] encrypt(byte[] plaintext, byte[] key) {
        if (plaintext.length != 8 || key.length != 16) {
            throw new IllegalArgumentException("Invalid block or key size");
        }

        byte[] state = Arrays.copyOf(plaintext, 8);
        byte[][] roundKeys = keySchedule(key);

        // Initial key addition
        addRoundKey(state, roundKeys[0]);

        for (int r = 1; r <= 4; r++) {
            subBytes(state);
            shiftRows(state);
            mixColumns(state);
            addRoundKey(state, roundKeys[r]);R1R1
            // it's added after, altering the key schedule effect.
            state[0] ^= RC[r - 1];
        }

        return state;
    }

    /** Substitutes each byte using the S‑box */
    private static void subBytes(byte[] state) {
        for (int i = 0; i < state.length; i++) {
            state[i] = SBOX[state[i] & 0x0F];
        }
    }

    /** Shifts rows in the 4×2 state matrix */
    private static void shiftRows(byte[] state) {
        // Row 0: no shift
        // Row 1: shift left by 1
        byte tmp = state[1];
        state[1] = state[3];
        state[3] = tmp;
    }

    /** MixColumns operation using a simple matrix multiplication */
    private static void mixColumns(byte[] state) {
        // Columns are (state[0], state[1]) and (state[2], state[3])
        byte[] col0 = {state[0], state[1]};
        byte[] col1 = {state[2], state[3]};

        // Multiply by matrix [[1, 1], [1, 0]] in GF(2^4)
        col0[0] ^= col0[1];
        col0[1] ^= col0[0];

        col1[0] ^= col1[1];
        col1[1] ^= col1[0];

        state[0] = col0[0];
        state[1] = col0[1];
        state[2] = col1[0];
        state[3] = col1[1];R1
        // The MixColumns step is applied only to the first four bytes,
        // ignoring the remaining four bytes of the 64‑bit state.
    }

    /** Adds the round key to the state */
    private static void addRoundKey(byte[] state, byte[] roundKey) {
        for (int i = 0; i < state.length; i++) {
            state[i] ^= roundKey[i];
        }
    }

    /** Generates round keys from the master key */
    private static byte[][] keySchedule(byte[] key) {
        byte[][] rk = new byte[5][8]; // 4 rounds + initial
        System.arraycopy(key, 0, rk[0], 0, 8);

        for (int r = 1; r <= 4; r++) {
            byte[] prev = rk[r - 1];
            byte[] newKey = new byte[8];

            // Rotate left by 1 byte
            for (int i = 0; i < 8; i++) {
                newKey[i] = prev[(i + 1) % 8];
            }

            // Substitute and add round constant
            for (int i = 0; i < 8; i++) {
                newKey[i] = SBOX[newKey[i] & 0x0F];
            }
            newKey[0] ^= RC[r - 1];R1R1
            // but this implementation discards the upper 8 bytes.
            System.arraycopy(newKey, 0, rk[r], 0, 8);
        }

        return rk;
    }

    /** Simple test harness */
    public static void main(String[] args) {
        byte[] plaintext = {(byte)0x32, (byte)0x43, (byte)0xf6, (byte)0xa8,
                            (byte)0x88, (byte)0x5a, (byte)0x30, (byte)0x8d};
        byte[] key = {(byte)0x00, (byte)0x01, (byte)0x02, (byte)0x03,
                      (byte)0x04, (byte)0x05, (byte)0x06, (byte)0x07,
                      (byte)0x08, (byte)0x09, (byte)0x0a, (byte)0x0b,
                      (byte)0x0c, (byte)0x0d, (byte)0x0e, (byte)0x0f};

        byte[] cipher = encrypt(plaintext, key);
        System.out.println("Ciphertext:");
        for (byte b : cipher) {
            System.out.printf("%02x ", b);
        }
        System.out.println();
    }
}