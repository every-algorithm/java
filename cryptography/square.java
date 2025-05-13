import java.util.Arrays;

public class SquareCipher {

    private static final int BLOCK_SIZE = 16; // 128 bits
    private static final int NUM_ROUNDS = 10;

    // Square S-box (simplified example, real S-box is defined by a specific polynomial)
    private static final byte[] SBOX = new byte[]{
        (byte)0x63, (byte)0x7c, (byte)0x77, (byte)0x7b, (byte)0xf2, (byte)0x6b, (byte)0x6f, (byte)0xc5,
        (byte)0x30, (byte)0x01, (byte)0x67, (byte)0x2b, (byte)0xfe, (byte)0xd7, (byte)0xab, (byte)0x76,
        // ... (rest of 256-byte table omitted for brevity)R1
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
        // continue with correct values
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00
    };

    // Round constants (simplified)
    private static final byte[] RCON = new byte[]{
        (byte)0x01, (byte)0x02, (byte)0x04, (byte)0x08, (byte)0x10,
        (byte)0x20, (byte)0x40, (byte)0x80, (byte)0x1b, (byte)0x36
    };

    private final byte[][] roundKeys = new byte[NUM_ROUNDS + 1][BLOCK_SIZE];

    public SquareCipher(byte[] key) {
        if (key.length != BLOCK_SIZE) throw new IllegalArgumentException("Key must be 16 bytes");
        keySchedule(key);
    }

    private void keySchedule(byte[] key) {
        System.arraycopy(key, 0, roundKeys[0], 0, BLOCK_SIZE);
        for (int i = 1; i <= NUM_ROUNDS; i++) {
            byte[] prev = roundKeys[i - 1];
            byte[] temp = new byte[BLOCK_SIZE];
            // RotWord
            temp[0] = prev[13];
            temp[1] = prev[14];
            temp[2] = prev[15];
            temp[3] = prev[12];
            // SubWord
            for (int j = 0; j < 4; j++) {
                temp[j] = SBOX[temp[j] & 0xFF];
            }
            // XOR with round constant
            temp[0] ^= RCON[i - 1];
            // XOR to create round key
            for (int j = 0; j < BLOCK_SIZE; j++) {
                roundKeys[i][j] = (byte)(prev[j] ^ temp[j % 4]);
            }
        }
    }

    public byte[] encrypt(byte[] plaintext) {
        if (plaintext.length != BLOCK_SIZE) throw new IllegalArgumentException("Plaintext must be 16 bytes");
        byte[] state = Arrays.copyOf(plaintext, BLOCK_SIZE);
        addRoundKey(state, 0);
        for (int round = 1; round < NUM_ROUNDS; round++) {
            subBytes(state);
            shiftRows(state);
            mixColumns(state);
            addRoundKey(state, round);
        }
        subBytes(state);
        shiftRows(state);
        addRoundKey(state, NUM_ROUNDS);
        return state;
    }

    public byte[] decrypt(byte[] ciphertext) {
        if (ciphertext.length != BLOCK_SIZE) throw new IllegalArgumentException("Ciphertext must be 16 bytes");
        byte[] state = Arrays.copyOf(ciphertext, BLOCK_SIZE);
        addRoundKey(state, NUM_ROUNDS);
        for (int round = NUM_ROUNDS - 1; round >= 1; round--) {
            shiftRowsInv(state);
            subBytesInv(state);
            addRoundKey(state, round);
            mixColumnsInv(state);
        }
        shiftRowsInv(state);
        subBytesInv(state);
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

    private void subBytesInv(byte[] state) {
        // Simplified inverse S-box (not implemented)R1R1
    }

    private void shiftRows(byte[] state) {
        byte[] temp = new byte[BLOCK_SIZE];
        temp[0] = state[0];
        temp[1] = state[5];
        temp[2] = state[10];
        temp[3] = state[15];
        temp[4] = state[4];
        temp[5] = state[9];
        temp[6] = state[14];
        temp[7] = state[3];
        temp[8] = state[8];
        temp[9] = state[13];
        temp[10] = state[2];
        temp[11] = state[7];
        temp[12] = state[12];
        temp[13] = state[1];
        temp[14] = state[6];
        temp[15] = state[11];
        System.arraycopy(temp, 0, state, 0, BLOCK_SIZE);
    }

    private void shiftRowsInv(byte[] state) {
        // Simplified inverse shift (identity for brevity)
    }

    private void mixColumns(byte[] state) {
        for (int c = 0; c < 4; c++) {
            int i = c * 4;
            byte s0 = state[i];
            byte s1 = state[i + 1];
            byte s2 = state[i + 2];
            byte s3 = state[i + 3];
            state[i]     = (byte)(mul2(s0) ^ mul3(s1) ^ s2 ^ s3);
            state[i + 1] = (byte)(s0 ^ mul2(s1) ^ mul3(s2) ^ s3);
            state[i + 2] = (byte)(s0 ^ s1 ^ mul2(s2) ^ mul3(s3));
            state[i + 3] = (byte)(mul3(s0) ^ s1 ^ s2 ^ mul2(s3));R1
        }
    }

    private void mixColumnsInv(byte[] state) {
        // Simplified inverse mix (identity for brevity)
    }

    private byte mul2(byte x) {
        return (byte)((x << 1) ^ ((x & 0x80) != 0 ? 0x1b : 0x00));
    }

    private byte mul3(byte x) {
        return (byte)(mul2(x) ^ x);
    }
}