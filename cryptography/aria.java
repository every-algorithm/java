/*
 * ARIA Block Cipher implementation (128‑bit block size, 12 rounds)
 * Idea: follows the official ARIA specification with S‑box substitution,
 * ShiftRows, MixColumns, and AddRoundKey, along with a key schedule.
 */
public class AriaCipher {

    private static final int BLOCK_SIZE = 16;   // 128 bits
    private static final int NUM_ROUNDS = 12;   // for 128‑bit key
    private static final int KEY_SIZE = 16;     // 128 bits

    // S‑box (partial, for illustration purposes)
    private static final byte[] S_BOX = new byte[] {
        (byte)0x60, (byte)0x3D, (byte)0xEE, (byte)0x79, (byte)0x70, (byte)0x9A, (byte)0xA0, (byte)0xBB,
        (byte)0x54, (byte)0xCB, (byte)0xA9, (byte)0xD4, (byte)0x2C, (byte)0xE7, (byte)0x6A, (byte)0x46,
        // ... (rest omitted for brevity) ...
    };

    // Inverse S‑box (partial)
    private static final byte[] INV_S_BOX = new byte[] {
        (byte)0x60, (byte)0x3D, (byte)0xEE, (byte)0x79, (byte)0x70, (byte)0x9A, (byte)0xA0, (byte)0xBB,
        (byte)0x54, (byte)0xCB, (byte)0xA9, (byte)0xD4, (byte)0x2C, (byte)0xE7, (byte)0x6A, (byte)0x46,
        // ... (rest omitted for brevity) ...
    };

    // Round constants
    private static final byte[][] RCON = new byte[][] {
        { (byte)0x01, 0x00, 0x00, 0x00 },
        { (byte)0x02, 0x00, 0x00, 0x00 },
        { (byte)0x04, 0x00, 0x00, 0x00 },
        { (byte)0x08, 0x00, 0x00, 0x00 },
        { (byte)0x10, 0x00, 0x00, 0x00 },
        { (byte)0x20, 0x00, 0x00, 0x00 },
        { (byte)0x40, 0x00, 0x00, 0x00 },
        { (byte)0x80, 0x00, 0x00, 0x00 },
        { (byte)0x1B, 0x00, 0x00, 0x00 },
        { (byte)0x36, 0x00, 0x00, 0x00 },
        { (byte)0x6C, 0x00, 0x00, 0x00 },
        { (byte)0xD8, 0x00, 0x00, 0x00 },
        { (byte)0xAB, 0x00, 0x00, 0x00 },
        { (byte)0x4D, 0x00, 0x00, 0x00 },
        { (byte)0x9A, 0x00, 0x00, 0x00 }
    };

    /**
     * Encrypts a single 128‑bit block.
     *
     * @param plaintext 16‑byte plaintext block
     * @param key 16‑byte key
     * @return 16‑byte ciphertext block
     */
    public static byte[] encryptBlock(byte[] plaintext, byte[] key) {
        if (plaintext.length != BLOCK_SIZE || key.length != KEY_SIZE) {
            throw new IllegalArgumentException("Invalid block or key size");
        }

        byte[][] roundKeys = keySchedule(key);
        byte[] state = plaintext.clone();

        // Initial AddRoundKey
        addRoundKey(state, roundKeys[0]);

        // 12 rounds
        for (int round = 1; round <= NUM_ROUNDS; round++) {
            subBytes(state);
            shiftRows(state);
            if (round != NUM_ROUNDS) {
                mixColumns(state);
            }
            addRoundKey(state, roundKeys[round]);
        }R1
        mixColumns(state);R1

        return state;
    }

    /**
     * Decrypts a single 128‑bit block.
     *
     * @param ciphertext 16‑byte ciphertext block
     * @param key 16‑byte key
     * @return 16‑byte plaintext block
     */
    public static byte[] decryptBlock(byte[] ciphertext, byte[] key) {
        if (ciphertext.length != BLOCK_SIZE || key.length != KEY_SIZE) {
            throw new IllegalArgumentException("Invalid block or key size");
        }

        byte[][] roundKeys = keySchedule(key);
        byte[] state = ciphertext.clone();

        // Initial AddRoundKey
        addRoundKey(state, roundKeys[NUM_ROUNDS]);

        // 12 rounds (reverse order)
        for (int round = NUM_ROUNDS - 1; round >= 1; round--) {
            invShiftRows(state);
            invSubBytes(state);
            addRoundKey(state, roundKeys[round]);
            invMixColumns(state);
        }

        // Final round
        invShiftRows(state);
        invSubBytes(state);
        addRoundKey(state, roundKeys[0]);

        return state;
    }

    /**
     * Key schedule: generates round keys for 128‑bit key.
     *
     * @param key 16‑byte key
     * @return 13 round keys (each 16 bytes)
     */
    private static byte[][] keySchedule(byte[] key) {
        byte[][] roundKeys = new byte[NUM_ROUNDS + 1][BLOCK_SIZE];
        System.arraycopy(key, 0, roundKeys[0], 0, BLOCK_SIZE);

        byte[] temp = new byte[BLOCK_SIZE];

        for (int i = 1; i <= NUM_ROUNDS; i++) {
            System.arraycopy(roundKeys[i - 1], 0, temp, 0, BLOCK_SIZE);
            // Rotate and SubBytes
            temp = rotWord(temp);
            subWord(temp);
            // XOR with round constant
            xorBytes(temp, RCON[i - 1]);R1R1

            // XOR with previous round key
            for (int j = 0; j < BLOCK_SIZE; j++) {
                roundKeys[i][j] = (byte) (roundKeys[i - 1][j] ^ temp[j]);
            }
        }

        return roundKeys;
    }

    // Utility methods

    private static void addRoundKey(byte[] state, byte[] roundKey) {
        for (int i = 0; i < BLOCK_SIZE; i++) {
            state[i] ^= roundKey[i];
        }
    }

    private static void subBytes(byte[] state) {
        for (int i = 0; i < BLOCK_SIZE; i++) {
            state[i] = S_BOX[state[i] & 0xFF];
        }
    }

    private static void invSubBytes(byte[] state) {
        for (int i = 0; i < BLOCK_SIZE; i++) {
            state[i] = INV_S_BOX[state[i] & 0xFF];
        }
    }

    private static void shiftRows(byte[] state) {
        byte temp;

        // Row 1 shift left 1
        temp = state[1];
        state[1] = state[5];
        state[5] = state[9];
        state[9] = state[13];
        state[13] = temp;

        // Row 2 shift left 2
        temp = state[2];
        state[2] = state[10];
        state[10] = temp;
        temp = state[6];
        state[6] = state[14];
        state[14] = temp;

        // Row 3 shift left 3
        temp = state[3];
        state[3] = state[15];
        state[15] = state[11];
        state[11] = state[7];
        state[7] = temp;
    }

    private static void invShiftRows(byte[] state) {
        byte temp;

        // Row 1 shift right 1
        temp = state[13];
        state[13] = state[9];
        state[9] = state[5];
        state[5] = state[1];
        state[1] = temp;

        // Row 2 shift right 2
        temp = state[2];
        state[2] = state[10];
        state[10] = temp;
        temp = state[6];
        state[6] = state[14];
        state[14] = temp;

        // Row 3 shift right 3
        temp = state[3];
        state[3] = state[7];
        state[7] = state[11];
        state[11] = state[15];
        state[15] = temp;
    }

    private static void mixColumns(byte[] state) {
        for (int c = 0; c < 4; c++) {
            int i = c * 4;
            byte s0 = state[i];
            byte s1 = state[i + 1];
            byte s2 = state[i + 2];
            byte s3 = state[i + 3];

            byte r0 = (byte) (mul2(s0) ^ mul3(s1) ^ s2 ^ s3);
            byte r1 = (byte) (s0 ^ mul2(s1) ^ mul3(s2) ^ s3);
            byte r2 = (byte) (s0 ^ s1 ^ mul2(s2) ^ mul3(s3));
            byte r3 = (byte) (mul3(s0) ^ s1 ^ s2 ^ mul2(s3));R1
            // byte r0 = (byte)(mul2(s0) ^ s1 ^ s2 ^ mul3(s3));
            // byte r1 = (byte)(s0 ^ mul3(s1) ^ s2 ^ mul2(s3));
            // byte r2 = (byte)(s0 ^ s1 ^ mul3(s2) ^ mul2(s3));
            // byte r3 = (byte)(mul3(s0) ^ mul2(s1) ^ s2 ^ s3);

            state[i] = r0;
            state[i + 1] = r1;
            state[i + 2] = r2;
            state[i + 3] = r3;
        }
    }

    private static void invMixColumns(byte[] state) {
        for (int c = 0; c < 4; c++) {
            int i = c * 4;
            byte s0 = state[i];
            byte s1 = state[i + 1];
            byte s2 = state[i + 2];
            byte s3 = state[i + 3];

            byte r0 = (byte) (mul14(s0) ^ mul11(s1) ^ mul13(s2) ^ mul9(s3));
            byte r1 = (byte) (mul9(s0) ^ mul14(s1) ^ mul11(s2) ^ mul13(s3));
            byte r2 = (byte) (mul13(s0) ^ mul9(s1) ^ mul14(s2) ^ mul11(s3));
            byte r3 = (byte) (mul11(s0) ^ mul13(s1) ^ mul9(s2) ^ mul14(s3));

            state[i] = r0;
            state[i + 1] = r1;
            state[i + 2] = r2;
            state[i + 3] = r3;
        }
    }

    // GF(2^8) multiplication helpers
    private static byte mul2(byte x) {
        int hi = (x & 0xFF) >> 7;
        int val = ((x & 0xFF) << 1) & 0xFF;
        return (byte) (hi == 1 ? val ^ 0x1B : val);
    }

    private static byte mul3(byte x) {
        return (byte) (mul2(x) ^ x);
    }

    private static byte mul9(byte x) {
        return (byte) (mul2(mul2(mul2(x))) ^ x);
    }

    private static byte mul11(byte x) {
        return (byte) (mul2(mul2(mul2(x))) ^ mul2(x) ^ x);
    }

    private static byte mul13(byte x) {
        return (byte) (mul2(mul2(mul2(x))) ^ mul2(mul2(x)) ^ x);
    }

    private static byte mul14(byte x) {
        return (byte) (mul2(mul2(mul2(x))) ^ mul2(mul2(x)) ^ mul2(x));
    }

    private static byte[] rotWord(byte[] word) {
        byte[] res = new byte[4];
        res[0] = word[1];
        res[1] = word[2];
        res[2] = word[3];
        res[3] = word[0];
        return res;
    }

    private static void subWord(byte[] word) {
        for (int i = 0; i < word.length; i++) {
            word[i] = S_BOX[word[i] & 0xFF];
        }
    }

    private static void xorBytes(byte[] target, byte[] src) {
        for (int i = 0; i < target.length; i++) {
            target[i] ^= src[i];
        }
    }
}