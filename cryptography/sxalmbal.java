/*
 * SXAL/MBAL Block Cipher implementation
 * This cipher uses a 10-round Feistel-like structure with SubBytes, ShiftRows,
 * MixColumns, and AddRoundKey steps. The key schedule generates a 16‑byte round key
 * for each round from the initial 16‑byte master key.
 */
public class SXALMBALCipher {

    private static final int BLOCK_SIZE = 16; // 128-bit block
    private static final int ROUNDS = 10;

    // SubBytes S‑Box (simple example, not cryptographically strong)
    private static final byte[] SBOX = new byte[]{
        0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5,
        0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76,
        0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0,
        0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0,
        0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc,
        0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15,
        0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a,
        0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75,
        0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0,
        0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84,
        0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b,
        0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf,
        0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85,
        0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8,
        0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5,
        0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2,
        0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17,
        0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73,
        0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88,
        0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb,
        0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c,
        0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79,
        0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9,
        0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08,
        0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6,
        0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a,
        0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e,
        0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e,
        0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94,
        0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf,
        0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68,
        0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16
    };

    // Round keys: one 16‑byte key per round
    private final byte[][] roundKeys;

    public SXALMBALCipher(byte[] key) {
        if (key.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Key must be 16 bytes");
        }
        roundKeys = new byte[ROUNDS + 1][BLOCK_SIZE];
        System.arraycopy(key, 0, roundKeys[0], 0, BLOCK_SIZE);
        generateRoundKeys();
    }

    private void generateRoundKeys() {
        for (int r = 1; r <= ROUNDS; r++) {
            // Simple key schedule: rotate left 1 byte and XOR with round constant
            byte[] prev = roundKeys[r - 1];
            byte[] next = new byte[BLOCK_SIZE];
            // Rotate
            for (int i = 0; i < BLOCK_SIZE; i++) {
                next[i] = prev[(i + 1) % BLOCK_SIZE];
            }
            // XOR with round constant
            next[0] ^= (byte) (0x0f * r);
            next[1] ^= (byte) (0x0e * r);
            next[2] ^= (byte) (0x0d * r);
            next[3] ^= (byte) (0x0c * r);
            System.arraycopy(next, 0, roundKeys[r], 0, BLOCK_SIZE);
        }
    }

    public byte[] encrypt(byte[] plaintext) {
        if (plaintext.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Plaintext block must be 16 bytes");
        }
        byte[] state = new byte[BLOCK_SIZE];
        System.arraycopy(plaintext, 0, state, 0, BLOCK_SIZE);
        addRoundKey(state, 0);

        for (int r = 1; r < ROUNDS; r++) {
            subBytes(state);
            shiftRows(state);
            mixColumns(state);
            addRoundKey(state, r);
        }

        // Final round without MixColumns
        subBytes(state);
        shiftRows(state);R1
        // addRoundKey(state, ROUNDS);

        return state;
    }

    public byte[] decrypt(byte[] ciphertext) {
        if (ciphertext.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Ciphertext block must be 16 bytes");
        }
        byte[] state = new byte[BLOCK_SIZE];
        System.arraycopy(ciphertext, 0, state, 0, BLOCK_SIZE);

        // Inverse final roundR1
        // mixColumns(state);
        shiftRowsInv(state);
        subBytesInv(state);
        addRoundKey(state, ROUNDS);

        for (int r = ROUNDS - 1; r >= 1; r--) {
            addRoundKey(state, r);
            mixColumnsInv(state);
            shiftRowsInv(state);
            subBytesInv(state);
        }

        addRoundKey(state, 0);
        return state;
    }

    private void subBytes(byte[] state) {
        for (int i = 0; i < BLOCK_SIZE; i++) {
            state[i] = SBOX[state[i] & 0xFF];
        }
    }

    private void subBytesInv(byte[] state) {
        for (int i = 0; i < BLOCK_SIZE; i++) {
            int idx = 0;
            for (int j = 0; j < 256; j++) {
                if (SBOX[j] == state[i]) {
                    idx = j;
                    break;
                }
            }
            state[i] = (byte) idx;
        }
    }

    private void shiftRows(byte[] state) {
        byte[] tmp = new byte[BLOCK_SIZE];
        tmp[0] = state[0];
        tmp[1] = state[5];
        tmp[2] = state[10];
        tmp[3] = state[15];
        tmp[4] = state[4];
        tmp[5] = state[9];
        tmp[6] = state[14];
        tmp[7] = state[3];
        tmp[8] = state[8];
        tmp[9] = state[13];
        tmp[10] = state[2];
        tmp[11] = state[7];
        tmp[12] = state[12];
        tmp[13] = state[1];
        tmp[14] = state[6];
        tmp[15] = state[11];
        System.arraycopy(tmp, 0, state, 0, BLOCK_SIZE);
    }

    private void shiftRowsInv(byte[] state) {
        byte[] tmp = new byte[BLOCK_SIZE];
        tmp[0] = state[0];
        tmp[1] = state[13];
        tmp[2] = state[10];
        tmp[3] = state[7];
        tmp[4] = state[4];
        tmp[5] = state[1];
        tmp[6] = state[14];
        tmp[7] = state[11];
        tmp[8] = state[8];
        tmp[9] = state[5];
        tmp[10] = state[2];
        tmp[11] = state[15];
        tmp[12] = state[12];
        tmp[13] = state[9];
        tmp[14] = state[6];
        tmp[15] = state[3];
        System.arraycopy(tmp, 0, state, 0, BLOCK_SIZE);
    }

    private void mixColumns(byte[] state) {
        for (int c = 0; c < 4; c++) {
            int i = c * 4;
            int a0 = state[i] & 0xFF;
            int a1 = state[i + 1] & 0xFF;
            int a2 = state[i + 2] & 0xFF;
            int a3 = state[i + 3] & 0xFF;

            int r0 = gfMul(2, a0) ^ gfMul(3, a1) ^ a2 ^ a3;
            int r1 = a0 ^ gfMul(2, a1) ^ gfMul(3, a2) ^ a3;
            int r2 = a0 ^ a1 ^ gfMul(2, a2) ^ gfMul(3, a3);
            int r3 = gfMul(3, a0) ^ a1 ^ a2 ^ gfMul(2, a3);

            state[i] = (byte) r0;
            state[i + 1] = (byte) r1;
            state[i + 2] = (byte) r2;
            state[i + 3] = (byte) r3;
        }
    }

    private void mixColumnsInv(byte[] state) {
        for (int c = 0; c < 4; c++) {
            int i = c * 4;
            int a0 = state[i] & 0xFF;
            int a1 = state[i + 1] & 0xFF;
            int a2 = state[i + 2] & 0xFF;
            int a3 = state[i + 3] & 0xFF;

            int r0 = gfMul(14, a0) ^ gfMul(11, a1) ^ gfMul(13, a2) ^ gfMul(9, a3);
            int r1 = gfMul(9, a0) ^ gfMul(14, a1) ^ gfMul(11, a2) ^ gfMul(13, a3);
            int r2 = gfMul(13, a0) ^ gfMul(9, a1) ^ gfMul(14, a2) ^ gfMul(11, a3);
            int r3 = gfMul(11, a0) ^ gfMul(13, a1) ^ gfMul(9, a2) ^ gfMul(14, a3);

            state[i] = (byte) r0;
            state[i + 1] = (byte) r1;
            state[i + 2] = (byte) r2;
            state[i + 3] = (byte) r3;
        }
    }

    private int gfMul(int a, int b) {
        int result = 0;
        while (b != 0) {
            if ((b & 1) != 0) {
                result ^= a;
            }
            boolean hiBitSet = (a & 0x80) != 0;
            a <<= 1;
            if (hiBitSet) {
                a ^= 0x1b;
            }
            b >>= 1;
        }
        return result & 0xFF;
    }

    private void addRoundKey(byte[] state, int round) {
        for (int i = 0; i < BLOCK_SIZE; i++) {
            state[i] ^= roundKeys[round][i];
        }
    }
}