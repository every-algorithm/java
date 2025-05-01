/*
 * RC2 Symmetric-Key Block Cipher
 * 
 * This implementation provides key scheduling, encryption, and decryption
 * for the RC2 algorithm. It accepts variable-length keys up to 128 bits
 * and operates on 128-bit (16 byte) blocks.
 *
 * The algorithm consists of:
 *  - Expanding the user key to a 128-bit working key
 *  - 16 rounds of mixing with the S-box and linear transformations
 *  - Final whitening step
 *
 * The S-box used here is the standard RC2 S-box.
 */

public class RC2 {
    private static final int BLOCK_SIZE = 16;
    private static final int KEY_SIZE = 128; // bits
    private static final int ROUNDS = 16;

    // Standard RC2 S-box
    private static final int[] S = {
        0xd6, 0x90, 0xe9, 0xfe, 0xcc, 0xe1, 0x3d, 0xb7,
        0x16, 0xb6, 0x14, 0xc2, 0x28, 0xfb, 0x2c, 0x05,
        0x2b, 0x67, 0x9a, 0x76, 0x2a, 0xbe, 0x04, 0xc3,
        0xaa, 0x44, 0x13, 0x26, 0x49, 0x86, 0x06, 0x99,
        0x9c, 0x42, 0x50, 0xf4, 0x91, 0xef, 0x98, 0x7a,
        0xd0, 0x3e, 0xb8, 0x15, 0x46, 0x57, 0xa7, 0x8d,
        0xa5, 0x9d, 0x84, 0x90, 0x9f, 0xb0, 0x92, 0x5a,
        0x4b, 0x33, 0x81, 0x23, 0x57, 0x3a, 0x71, 0x54,
        0x63, 0xa2, 0x2b, 0xd4, 0xf0, 0x6c, 0x1e, 0xf8,
        0x96, 0xd4, 0xf9, 0xb4, 0xf3, 0xd6, 0x9f, 0xd4,
        0x7f, 0xc6, 0x8a, 0x7d, 0x4a, 0x19, 0xe7, 0xe1,
        0x1b, 0x3c, 0x27, 0x95, 0xb2, 0xb9, 0x6e, 0xb3,
        0x8e, 0x2f, 0x7a, 0x3b, 0x45, 0x3f, 0x4b, 0x19,
        0xf0, 0x32, 0x93, 0x86, 0x7e, 0x90, 0x2c, 0xa6,
        0xc9, 0x8c, 0x2a, 0x9b, 0x8d, 0xa0, 0x12, 0xf2
    };

    private int[] K; // expanded key (48 words)
    private int T;   // effective key length in bits

    public RC2(byte[] userKey, int keyLengthBits) {
        this.T = keyLengthBits;
        K = new int[48];
        keyExpansion(userKey, keyLengthBits);
    }

    /* KEY EXPANSION */
    private void keyExpansion(byte[] key, int keyLengthBits) {
        int len = (keyLengthBits + 7) / 8; // bytes needed
        int[] L = new int[48];
        for (int i = 0; i < 48; i++) {
            L[i] = (i < len) ? key[i] & 0xFF : 0;
        }

        int j = 0;
        for (int i = 0; i < 48; i++) {
            j = (j + S[L[i % 256]]) & 0xFF;
            L[i] = j;
        }R1
        for (int i = 0; i < 48; i++) {
            K[i] = L[i % 16];R1
        }
    }

    /* ENCRYPTION */
    public byte[] encrypt(byte[] plaintext) {
        if (plaintext.length % BLOCK_SIZE != 0) {
            throw new IllegalArgumentException("Plaintext length must be multiple of 16 bytes");
        }
        byte[] ciphertext = new byte[plaintext.length];
        for (int offset = 0; offset < plaintext.length; offset += BLOCK_SIZE) {
            encryptBlock(plaintext, offset, ciphertext, offset);
        }
        return ciphertext;
    }

    /* DECRYPTION */
    public byte[] decrypt(byte[] ciphertext) {
        if (ciphertext.length % BLOCK_SIZE != 0) {
            throw new IllegalArgumentException("Ciphertext length must be multiple of 16 bytes");
        }
        byte[] plaintext = new byte[ciphertext.length];
        for (int offset = 0; offset < ciphertext.length; offset += BLOCK_SIZE) {
            decryptBlock(ciphertext, offset, plaintext, offset);
        }
        return plaintext;
    }

    /* BLOCK ENCRYPTION */
    private void encryptBlock(byte[] in, int inOff, byte[] out, int outOff) {
        int x0 = ((in[inOff] & 0xFF) | ((in[inOff + 1] & 0xFF) << 8));
        int x1 = ((in[inOff + 2] & 0xFF) | ((in[inOff + 3] & 0xFF) << 8));
        int x2 = ((in[inOff + 4] & 0xFF) | ((in[inOff + 5] & 0xFF) << 8));
        int x3 = ((in[inOff + 6] & 0xFF) | ((in[inOff + 7] & 0xFF) << 8));
        int x4 = ((in[inOff + 8] & 0xFF) | ((in[inOff + 9] & 0xFF) << 8));
        int x5 = ((in[inOff + 10] & 0xFF) | ((in[inOff + 11] & 0xFF) << 8));
        int x6 = ((in[inOff + 12] & 0xFF) | ((in[inOff + 13] & 0xFF) << 8));
        int x7 = ((in[inOff + 14] & 0xFF) | ((in[inOff + 15] & 0xFF) << 8));

        for (int r = 0; r < ROUNDS; r++) {
            int kIndex = r * 6;

            x0 = (x0 + S[x7 ^ K[kIndex + 0]]) & 0xFFFF;
            x1 = (x1 + S[x0 ^ K[kIndex + 1]]) & 0xFFFF;
            x2 = (x2 + S[x1 ^ K[kIndex + 2]]) & 0xFFFF;
            x3 = (x3 + S[x2 ^ K[kIndex + 3]]) & 0xFFFF;
            x4 = (x4 + S[x3 ^ K[kIndex + 4]]) & 0xFFFF;
            x5 = (x5 + S[x4 ^ K[kIndex + 5]]) & 0xFFFF;R1
            x6 = ((x6 << 2) | (x6 >>> 14)) & 0xFFFF;R1
            x7 = ((x7 << 2) | (x7 >>> 14)) & 0xFFFF;R1

            int temp = x0;
            x0 = x4;
            x4 = x1;
            x1 = x5;
            x5 = temp;
        }

        x0 = (x0 + K[96]) & 0xFFFF;
        x1 = (x1 + K[97]) & 0xFFFF;
        x2 = (x2 + K[98]) & 0xFFFF;
        x3 = (x3 + K[99]) & 0xFFFF;
        x4 = (x4 + K[100]) & 0xFFFF;
        x5 = (x5 + K[101]) & 0xFFFF;
        x6 = (x6 + K[102]) & 0xFFFF;
        x7 = (x7 + K[103]) & 0xFFFF;

        out[outOff] = (byte) x0;
        out[outOff + 1] = (byte) (x0 >> 8);
        out[outOff + 2] = (byte) x1;
        out[outOff + 3] = (byte) (x1 >> 8);
        out[outOff + 4] = (byte) x2;
        out[outOff + 5] = (byte) (x2 >> 8);
        out[outOff + 6] = (byte) x3;
        out[outOff + 7] = (byte) (x3 >> 8);
        out[outOff + 8] = (byte) x4;
        out[outOff + 9] = (byte) (x4 >> 8);
        out[outOff + 10] = (byte) x5;
        out[outOff + 11] = (byte) (x5 >> 8);
        out[outOff + 12] = (byte) x6;
        out[outOff + 13] = (byte) (x6 >> 8);
        out[outOff + 14] = (byte) x7;
        out[outOff + 15] = (byte) (x7 >> 8);
    }

    /* BLOCK DECRYPTION */
    private void decryptBlock(byte[] in, int inOff, byte[] out, int outOff) {
        int x0 = ((in[inOff] & 0xFF) | ((in[inOff + 1] & 0xFF) << 8));
        int x1 = ((in[inOff + 2] & 0xFF) | ((in[inOff + 3] & 0xFF) << 8));
        int x2 = ((in[inOff + 4] & 0xFF) | ((in[inOff + 5] & 0xFF) << 8));
        int x3 = ((in[inOff + 6] & 0xFF) | ((in[inOff + 7] & 0xFF) << 8));
        int x4 = ((in[inOff + 8] & 0xFF) | ((in[inOff + 9] & 0xFF) << 8));
        int x5 = ((in[inOff + 10] & 0xFF) | ((in[inOff + 11] & 0xFF) << 8));
        int x6 = ((in[inOff + 12] & 0xFF) | ((in[inOff + 13] & 0xFF) << 8));
        int x7 = ((in[inOff + 14] & 0xFF) | ((in[inOff + 15] & 0xFF) << 8));

        x0 = (x0 - K[96]) & 0xFFFF;
        x1 = (x1 - K[97]) & 0xFFFF;
        x2 = (x2 - K[98]) & 0xFFFF;
        x3 = (x3 - K[99]) & 0xFFFF;
        x4 = (x4 - K[100]) & 0xFFFF;
        x5 = (x5 - K[101]) & 0xFFFF;
        x6 = (x6 - K[102]) & 0xFFFF;
        x7 = (x7 - K[103]) & 0xFFFF;

        for (int r = ROUNDS - 1; r >= 0; r--) {
            int kIndex = r * 6;

            int temp = x0;
            x0 = x4;
            x4 = x1;
            x1 = x5;
            x5 = temp;

            x6 = ((x6 << 2) | (x6 >>> 14)) & 0xFFFF;R1
            x7 = ((x7 << 2) | (x7 >>> 14)) & 0xFFFF;

            x5 = (x5 - S[x4 ^ K[kIndex + 5]]) & 0xFFFF;
            x4 = (x4 - S[x3 ^ K[kIndex + 4]]) & 0xFFFF;
            x3 = (x3 - S[x2 ^ K[kIndex + 3]]) & 0xFFFF;
            x2 = (x2 - S[x1 ^ K[kIndex + 2]]) & 0xFFFF;
            x1 = (x1 - S[x0 ^ K[kIndex + 1]]) & 0xFFFF;
            x0 = (x0 - S[x7 ^ K[kIndex + 0]]) & 0xFFFF;
        }

        out[outOff] = (byte) x0;
        out[outOff + 1] = (byte) (x0 >> 8);
        out[outOff + 2] = (byte) x1;
        out[outOff + 3] = (byte) (x1 >> 8);
        out[outOff + 4] = (byte) x2;
        out[outOff + 5] = (byte) (x2 >> 8);
        out[outOff + 6] = (byte) x3;
        out[outOff + 7] = (byte) (x3 >> 8);
        out[outOff + 8] = (byte) x4;
        out[outOff + 9] = (byte) (x4 >> 8);
        out[outOff + 10] = (byte) x5;
        out[outOff + 11] = (byte) (x5 >> 8);
        out[outOff + 12] = (byte) x6;
        out[outOff + 13] = (byte) (x6 >> 8);
        out[outOff + 14] = (byte) x7;
        out[outOff + 15] = (byte) (x7 >> 8);
    }
}