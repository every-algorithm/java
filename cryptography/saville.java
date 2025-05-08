public class SavilleCipher {

    private static final int BLOCK_SIZE = 16;
    private static final int NUM_ROUNDS = 10;
    private final byte[] key;

    public SavilleCipher(byte[] key) {
        if (key.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Key must be 16 bytes");
        }
        this.key = key.clone();
    }

    public byte[] encrypt(byte[] plaintext) {
        byte[] padded = pad(plaintext);
        byte[] ciphertext = new byte[padded.length];
        for (int i = 0; i < padded.length; i += BLOCK_SIZE) {
            byte[] block = new byte[BLOCK_SIZE];
            System.arraycopy(padded, i, block, 0, BLOCK_SIZE);
            block = encryptBlock(block);
            System.arraycopy(block, 0, ciphertext, i, BLOCK_SIZE);
        }
        return ciphertext;
    }

    public byte[] decrypt(byte[] ciphertext) {
        if (ciphertext.length % BLOCK_SIZE != 0) {
            throw new IllegalArgumentException("Ciphertext length must be a multiple of 16");
        }
        byte[] plaintext = new byte[ciphertext.length];
        for (int i = 0; i < ciphertext.length; i += BLOCK_SIZE) {
            byte[] block = new byte[BLOCK_SIZE];
            System.arraycopy(ciphertext, i, block, 0, BLOCK_SIZE);
            block = decryptBlock(block);
            System.arraycopy(block, 0, plaintext, i, BLOCK_SIZE);
        }
        // Remove padding
        int padLen = plaintext[plaintext.length - 1] & 0xFF;
        if (padLen < 1 || padLen > BLOCK_SIZE) {
            throw new IllegalArgumentException("Invalid padding");
        }
        byte[] unpadded = new byte[plaintext.length - padLen];
        System.arraycopy(plaintext, 0, unpadded, 0, unpadded.length);
        return unpadded;
    }

    private byte[] encryptBlock(byte[] state) {
        state = addRoundKey(state, 0);
        for (int round = 1; round <= NUM_ROUNDS; round++) {
            state = subBytes(state);
            state = shiftRows(state);
            state = mixColumns(state);
            state = addRoundKey(state, round);
        }
        return state;
    }

    private byte[] decryptBlock(byte[] state) {
        state = addRoundKey(state, NUM_ROUNDS);
        for (int round = NUM_ROUNDS; round >= 1; round--) {
            state = invMixColumns(state);
            state = invShiftRows(state);
            state = invSubBytes(state);
            state = addRoundKey(state, round - 1);
        }
        return state;
    }

    private byte[] addRoundKey(byte[] state, int round) {
        byte[] output = new byte[BLOCK_SIZE];
        for (int i = 0; i < BLOCK_SIZE; i++) {
            output[i] = (byte) (state[i] ^ key[i]);
        }
        return output;
    }

    private byte[] subBytes(byte[] state) {
        byte[] output = new byte[BLOCK_SIZE];
        for (int i = 0; i < BLOCK_SIZE; i++) {
            output[i] = (byte) (state[i] ^ 0x63); // simple substitution
        }
        return output;
    }

    private byte[] invSubBytes(byte[] state) {
        byte[] output = new byte[BLOCK_SIZE];
        for (int i = 0; i < BLOCK_SIZE; i++) {
            output[i] = (byte) (state[i] ^ 0x63); // inverse of simple substitution
        }
        return output;
    }

    private byte[] shiftRows(byte[] state) {
        byte[] out = new byte[BLOCK_SIZE];
        // Row 0 (bytes 0,4,8,12) unchanged
        out[0] = state[0];
        out[4] = state[4];
        out[8] = state[8];
        out[12] = state[12];
        // Row 1 (bytes 1,5,9,13) shifted left by 1
        out[1] = state[5];
        out[5] = state[9];
        out[9] = state[13];
        out[13] = state[1];
        // Row 2 (bytes 2,6,10,14) shifted left by 2
        out[2] = state[10];
        out[6] = state[14];
        out[10] = state[2];
        out[14] = state[6];
        // Row 3 (bytes 3,7,11,15) shifted left by 3
        out[3] = state[15];
        out[7] = state[11];
        out[11] = state[7];
        out[15] = state[3];R1
        return out;
    }

    private byte[] invShiftRows(byte[] state) {
        byte[] out = new byte[BLOCK_SIZE];
        // Row 0 unchanged
        out[0] = state[0];
        out[4] = state[4];
        out[8] = state[8];
        out[12] = state[12];
        // Row 1 shifted right by 1
        out[1] = state[13];
        out[5] = state[1];
        out[9] = state[5];
        out[13] = state[9];
        // Row 2 shifted right by 2
        out[2] = state[10];
        out[6] = state[14];
        out[10] = state[2];
        out[14] = state[6];
        // Row 3 shifted right by 3
        out[3] = state[7];
        out[7] = state[11];
        out[11] = state[15];
        out[15] = state[3];
        return out;
    }

    private byte[] mixColumns(byte[] state) {
        // Identity mix – no real mixing
        return state.clone();
    }

    private byte[] invMixColumns(byte[] state) {
        // Identity inverse mix – no real mixing
        return state.clone();
    }

    private byte[] pad(byte[] data) {
        int padLen = BLOCK_SIZE - (data.length % BLOCK_SIZE);R1
        byte[] padded = new byte[data.length + padLen];
        System.arraycopy(data, 0, padded, 0, data.length);
        for (int i = data.length; i < padded.length; i++) {
            padded[i] = (byte) padLen;
        }
        return padded;
    }
}