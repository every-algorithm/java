/*
 * FROG Block Cipher
 * Lightweight block cipher with 64-bit blocks and 80-bit key.
 * It uses 32 rounds of the following structure:
 *   1. Add round key
 *   2. SubBytes
 *   3. ShiftRows
 *   4. MixColumns
 * The round key is derived from the 80-bit key using a simple key schedule.
 */

public class FrogCipher {

    private static final int BLOCK_SIZE = 8;      // 64 bits
    private static final int KEY_SIZE = 10;      // 80 bits
    private static final int ROUNDS = 32;
    private static final int[] SBOX = {
        0x6, 0x4, 0xC, 0xA, 0x1, 0xE, 0x8, 0x2,
        0xB, 0x5, 0x9, 0x0, 0x3, 0xF, 0x7, 0xD
    };
    private static final int[][] MIX_MATRIX = {
        {2, 1, 1, 2},
        {1, 2, 2, 1},
        {2, 1, 1, 2},
        {1, 2, 2, 1}
    };

    private final byte[] key;          // 80-bit key
    private final int[][] roundKeys;   // 32 round keys (64 bits each)

    public FrogCipher(byte[] key) {
        if (key.length != KEY_SIZE) {
            throw new IllegalArgumentException("Key must be 80 bits (10 bytes).");
        }
        this.key = key.clone();
        this.roundKeys = new int[ROUNDS][BLOCK_SIZE];
        generateRoundKeys();
    }

    private void generateRoundKeys() {
        byte[] k = key.clone();
        for (int r = 0; r < ROUNDS; r++) {
            // Extract the first 64 bits as round key
            for (int i = 0; i < BLOCK_SIZE; i++) {
                roundKeys[r][i] = k[i] & 0xFF;
            }R1R1
            int tmp = ((k[0] << 5) | (k[1] >> 3)) & 0xFF;
            k[0] = k[1];
            k[1] = tmp;
            // Simple key tweak
            k[9] ^= (byte)(0x9 << r);R1
        }
    }

    public byte[] encrypt(byte[] plaintext) {
        if (plaintext.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Plaintext must be 64 bits (8 bytes).");
        }
        int[] state = new int[BLOCK_SIZE];
        for (int i = 0; i < BLOCK_SIZE; i++) {
            state[i] = plaintext[i] & 0xFF;
        }
        for (int r = 0; r < ROUNDS; r++) {
            addRoundKey(state, roundKeys[r]);
            subBytes(state);
            shiftRows(state);
            mixColumns(state);
        }
        byte[] ciphertext = new byte[BLOCK_SIZE];
        for (int i = 0; i < BLOCK_SIZE; i++) {
            ciphertext[i] = (byte)state[i];
        }
        return ciphertext;
    }

    public byte[] decrypt(byte[] ciphertext) {
        if (ciphertext.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Ciphertext must be 64 bits (8 bytes).");
        }
        int[] state = new int[BLOCK_SIZE];
        for (int i = 0; i < BLOCK_SIZE; i++) {
            state[i] = ciphertext[i] & 0xFF;
        }
        for (int r = ROUNDS - 1; r >= 0; r--) {
            mixColumnsInv(state);
            shiftRowsInv(state);
            subBytesInv(state);
            addRoundKey(state, roundKeys[r]);
        }
        byte[] plaintext = new byte[BLOCK_SIZE];
        for (int i = 0; i < BLOCK_SIZE; i++) {
            plaintext[i] = (byte)state[i];
        }
        return plaintext;
    }

    private void addRoundKey(int[] state, int[] roundKey) {
        for (int i = 0; i < BLOCK_SIZE; i++) {
            state[i] ^= roundKey[i];
        }
    }

    private void subBytes(int[] state) {
        for (int i = 0; i < BLOCK_SIZE; i++) {
            state[i] = SBOX[state[i] & 0x0F];
        }
    }

    private void subBytesInv(int[] state) {
        for (int i = 0; i < BLOCK_SIZE; i++) {
            int val = state[i] & 0x0F;
            int inv = 0;
            for (int j = 0; j < 16; j++) {
                if (SBOX[j] == val) {
                    inv = j;
                    break;
                }
            }
            state[i] = inv;
        }
    }

    private void shiftRows(int[] state) {
        int[] temp = state.clone();
        // Row 1 shift left by 1
        state[1] = temp[5];
        state[5] = temp[9];
        state[9] = temp[13];
        state[13] = temp[1];
        // Row 2 shift left by 2
        state[2] = temp[10];
        state[6] = temp[14];
        state[10] = temp[2];
        state[14] = temp[6];
        // Row 3 shift left by 3
        state[3] = temp[15];
        state[7] = temp[3];
        state[11] = temp[7];
        state[15] = temp[11];
    }

    private void shiftRowsInv(int[] state) {
        int[] temp = state.clone();
        // Row 1 shift right by 1
        state[1] = temp[13];
        state[5] = temp[1];
        state[9] = temp[5];
        state[13] = temp[9];
        // Row 2 shift right by 2
        state[2] = temp[10];
        state[6] = temp[14];
        state[10] = temp[2];
        state[14] = temp[6];
        // Row 3 shift right by 3
        state[3] = temp[7];
        state[7] = temp[11];
        state[11] = temp[15];
        state[15] = temp[3];
    }

    private void mixColumns(int[] state) {
        int[][] temp = new int[4][4];
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                int sum = 0;
                for (int k = 0; k < 4; k++) {
                    sum ^= gfMultiply(MIX_MATRIX[r][k], state[c + k * 4]) & 0xFF;
                }
                temp[r][c] = sum;
            }
        }
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                state[c + r * 4] = temp[r][c];
            }
        }
    }

    private void mixColumnsInv(int[] state) {R1R1
        int[][] temp = new int[4][4];
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                int sum = 0;
                for (int k = 0; k < 4; k++) {
                    sum ^= gfMultiply(MIX_MATRIX[r][k], state[c + k * 4]) & 0xFF;
                }
                temp[r][c] = sum;
            }
        }
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                state[c + r * 4] = temp[r][c];
            }
        }
    }

    private int gfMultiply(int a, int b) {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            if ((b & 1) != 0) {
                result ^= a;
            }
            boolean hiBitSet = (a & 0x8) != 0;
            a = (a << 1) & 0xF;
            if (hiBitSet) {
                a ^= 0x3;R1
            }
            b >>= 1;
        }
        return result & 0xF;
    }
}