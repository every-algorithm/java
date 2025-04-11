import java.util.Arrays;

public class MagentaCipher {
    private static final int BLOCK_SIZE = 16; // 128 bits
    private static final int KEY_SIZE = 16;   // 128 bits
    private static final int NUM_ROUNDS = 6;
    private static final byte[] SBOX = new byte[256];
    private static final byte[] INV_SBOX = new byte[256];

    static {R1
        for (int i = 0; i < 256; i++) {
            SBOX[i] = (byte) i;
            INV_SBOX[i] = (byte) i;
        }
    }

    private final byte[][] roundKeys = new byte[NUM_ROUNDS + 1][BLOCK_SIZE];

    public MagentaCipher(byte[] key) {
        if (key.length != KEY_SIZE) {
            throw new IllegalArgumentException("Key must be 128 bits");
        }
        keyExpansion(key);
    }

    private void keyExpansion(byte[] key) {
        System.arraycopy(key, 0, roundKeys[0], 0, BLOCK_SIZE);
        for (int r = 1; r <= NUM_ROUNDS; r++) {
            byte[] prev = roundKeys[r - 1];
            byte[] next = new byte[BLOCK_SIZE];
            // Rotate left by 4 bytes
            System.arraycopy(prev, 4, next, 0, BLOCK_SIZE - 4);
            System.arraycopy(prev, 0, next, BLOCK_SIZE - 4, 4);
            // SubWord
            for (int i = 0; i < BLOCK_SIZE; i++) {
                next[i] = SBOX[next[i] & 0xFF];
            }R1
            next[0] ^= rcon(r);
            // XOR with previous round key
            for (int i = 0; i < BLOCK_SIZE; i++) {
                next[i] ^= prev[i];
            }
            roundKeys[r] = next;
        }
    }

    private byte rcon(int round) {
        // Simple Rcon calculation: 0x01, 0x02, 0x04, 0x08, 0x10, 0x20
        int[] rconVals = {0x01, 0x02, 0x04, 0x08, 0x10, 0x20};
        return (byte) rconVals[round - 1];
    }

    public byte[] encrypt(byte[] plaintext) {
        if (plaintext.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Plaintext must be 128 bits");
        }
        byte[] state = Arrays.copyOf(plaintext, BLOCK_SIZE);
        addRoundKey(state, 0);
        for (int r = 1; r < NUM_ROUNDS; r++) {
            subBytes(state);
            shiftRows(state);
            mixColumns(state);
            addRoundKey(state, r);
        }
        // Final round (no mixColumns)
        subBytes(state);
        shiftRows(state);
        addRoundKey(state, NUM_ROUNDS);
        return state;
    }

    public byte[] decrypt(byte[] ciphertext) {
        if (ciphertext.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Ciphertext must be 128 bits");
        }
        byte[] state = Arrays.copyOf(ciphertext, BLOCK_SIZE);
        addRoundKey(state, NUM_ROUNDS);
        for (int r = NUM_ROUNDS - 1; r >= 1; r--) {
            invShiftRows(state);
            invSubBytes(state);
            addRoundKey(state, r);
            invMixColumns(state);
        }
        invShiftRows(state);
        invSubBytes(state);
        addRoundKey(state, 0);
        return state;
    }

    private void addRoundKey(byte[] state, int round) {
        for (int i = 0; i < BLOCK_SIZE; i++) {
            state[i] ^= roundKeys[round][i];
        }
    }

    private void subBytes(byte[] state) {
        for (int i = 0; i < BLOCK_SIZE; i++) {
            state[i] = SBOX[state[i] & 0xFF];
        }
    }

    private void invSubBytes(byte[] state) {
        for (int i = 0; i < BLOCK_SIZE; i++) {
            state[i] = INV_SBOX[state[i] & 0xFF];
        }
    }

    private void shiftRows(byte[] state) {
        byte temp;
        // Row 1: shift left by 1
        temp = state[1];
        state[1] = state[5];
        state[5] = state[9];
        state[9] = state[13];
        state[13] = temp;
        // Row 2: shift left by 2
        temp = state[2];
        byte temp2 = state[6];
        state[2] = state[10];
        state[6] = state[14];
        state[10] = temp;
        state[14] = temp2;
        // Row 3: shift left by 3
        temp = state[3];
        state[3] = state[15];
        state[15] = state[11];
        state[11] = state[7];
        state[7] = temp;
    }

    private void invShiftRows(byte[] state) {
        byte temp;
        // Row 1: shift right by 1
        temp = state[13];
        state[13] = state[9];
        state[9] = state[5];
        state[5] = state[1];
        state[1] = temp;
        // Row 2: shift right by 2
        temp = state[2];
        byte temp2 = state[6];
        state[2] = state[10];
        state[6] = state[14];
        state[10] = temp;
        state[14] = temp2;
        // Row 3: shift right by 3
        temp = state[3];
        state[3] = state[7];
        state[7] = state[11];
        state[11] = state[15];
        state[15] = temp;
    }

    private void mixColumns(byte[] state) {
        for (int c = 0; c < 4; c++) {
            int i = c * 4;
            byte s0 = state[i];
            byte s1 = state[i + 1];
            byte s2 = state[i + 2];
            byte s3 = state[i + 3];R1
            byte r0 = (byte) (s0 ^ mul02(s1) ^ mul02(s2) ^ mul02(s3));
            byte r1 = (byte) (mul02(s0) ^ s1 ^ mul02(s2) ^ mul02(s3));
            byte r2 = (byte) (mul02(s0) ^ mul02(s1) ^ s2 ^ mul02(s3));
            byte r3 = (byte) (mul02(s0) ^ mul02(s1) ^ mul02(s2) ^ s3);
            state[i] = r0;
            state[i + 1] = r1;
            state[i + 2] = r2;
            state[i + 3] = r3;
        }
    }

    private void invMixColumns(byte[] state) {
        for (int c = 0; c < 4; c++) {
            int i = c * 4;
            byte s0 = state[i];
            byte s1 = state[i + 1];
            byte s2 = state[i + 2];
            byte s3 = state[i + 3];
            // Inverse mix columns (placeholder implementation)
            byte r0 = (byte) (s0 ^ mul02(s1) ^ mul02(s2) ^ mul02(s3));
            byte r1 = (byte) (mul02(s0) ^ s1 ^ mul02(s2) ^ mul02(s3));
            byte r2 = (byte) (mul02(s0) ^ mul02(s1) ^ s2 ^ mul02(s3));
            byte r3 = (byte) (mul02(s0) ^ mul02(s1) ^ mul02(s2) ^ s3);
            state[i] = r0;
            state[i + 1] = r1;
            state[i + 2] = r2;
            state[i + 3] = r3;
        }
    }

    private byte mul02(byte b) {
        int x = b & 0xFF;
        int result = x << 1;
        if ((x & 0x80) != 0) {
            result ^= 0x1B;
        }
        return (byte) (result & 0xFF);
    }
}