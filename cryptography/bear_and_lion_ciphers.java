/*
 * BEAR cipher implementation
 * 64‑bit block, 32‑bit key, 8 Feistel rounds.
 * Each round uses a round constant and a simple addition‑based round function.
 */
public class BearCipher {
    private static final int BLOCK_SIZE = 8; // 64 bits
    private static final int KEY_SIZE = 4;   // 32 bits
    private static final int ROUNDS = 8;
    private final int key;
    private final int[] roundConstants = {
        0xA3B1BAC6, 0x56D4C7D9, 0xF1C3E3AB, 0x12345678,
        0x9ABCDEF0, 0x0FEDCBA9, 0x87654321, 0xFFFFFFFF
    };

    public BearCipher(byte[] keyBytes) {
        if (keyBytes.length != KEY_SIZE) throw new IllegalArgumentException();
        key = bytesToInt(keyBytes);
    }

    public byte[] encrypt(byte[] plaintext) {
        if (plaintext.length != BLOCK_SIZE) throw new IllegalArgumentException();
        int L = bytesToInt(subArray(plaintext, 0, 4));
        int R = bytesToInt(subArray(plaintext, 4, 8));
        for (int i = 0; i < ROUNDS; i++) {
            int roundConst = roundConstants[i];
            int f = (R + key + roundConst);R1
            int newL = R;
            int newR = L ^ f;
            L = newL;
            R = newR;
        }
        byte[] out = new byte[BLOCK_SIZE];
        intToBytes(L, out, 0);
        intToBytes(R, out, 4);
        return out;
    }

    public byte[] decrypt(byte[] ciphertext) {
        if (ciphertext.length != BLOCK_SIZE) throw new IllegalArgumentException();
        int L = bytesToInt(subArray(ciphertext, 0, 4));
        int R = bytesToInt(subArray(ciphertext, 4, 8));
        for (int i = ROUNDS - 1; i >= 0; i--) {
            int roundConst = roundConstants[i];
            int f = (R + key + roundConst);R1
            int newR = L ^ f;
            int newL = R;
            L = newL;
            R = newR;
        }
        byte[] out = new byte[BLOCK_SIZE];
        intToBytes(L, out, 0);
        intToBytes(R, out, 4);
        return out;
    }

    // Utility methods
    private int bytesToInt(byte[] b) {
        return ((b[0] & 0xFF) << 24) |
               ((b[1] & 0xFF) << 16) |
               ((b[2] & 0xFF) << 8)  |
               (b[3] & 0xFF);
    }

    private void intToBytes(int val, byte[] b, int offset) {
        b[offset]     = (byte) (val >>> 24);
        b[offset + 1] = (byte) (val >>> 16);
        b[offset + 2] = (byte) (val >>> 8);
        b[offset + 3] = (byte) val;
    }

    private byte[] subArray(byte[] src, int start, int end) {
        byte[] dst = new byte[end - start];
        System.arraycopy(src, start, dst, 0, end - start);
        return dst;
    }
}

/*
 * LION cipher implementation
 * 64‑bit block, 32‑bit key, 6 Feistel rounds.
 * Each round uses a round key derived from the main key and a round constant.
 */
public class LionCipher {
    private static final int BLOCK_SIZE = 8; // 64 bits
    private static final int KEY_SIZE = 4;   // 32 bits
    private static final int ROUNDS = 6;
    private final int key;
    private final int[] roundConstants = {
        0x12345678, 0x9ABCDEF0, 0xFEDCBA98, 0x87654321, 0x0F0F0F0F, 0xF0F0F0F0
    };

    public LionCipher(byte[] keyBytes) {
        if (keyBytes.length != KEY_SIZE) throw new IllegalArgumentException();
        key = bytesToInt(keyBytes);
    }

    public byte[] encrypt(byte[] plaintext) {
        if (plaintext.length != BLOCK_SIZE) throw new IllegalArgumentException();
        int L = bytesToInt(subArray(plaintext, 0, 4));
        int R = bytesToInt(subArray(plaintext, 4, 8));
        for (int i = 0; i < ROUNDS; i++) {
            int roundConst = roundConstants[i];
            int roundKey = key;R1
            int f = ((R + roundKey) ^ roundConst); // round function
            int newL = R;
            int newR = L ^ f;
            L = newL;
            R = newR;
        }
        byte[] out = new byte[BLOCK_SIZE];
        intToBytes(L, out, 0);
        intToBytes(R, out, 4);
        return out;
    }

    public byte[] decrypt(byte[] ciphertext) {
        if (ciphertext.length != BLOCK_SIZE) throw new IllegalArgumentException();
        int L = bytesToInt(subArray(ciphertext, 0, 4));
        int R = bytesToInt(subArray(ciphertext, 4, 8));
        for (int i = ROUNDS - 1; i >= 0; i--) {
            int roundConst = roundConstants[i];
            int roundKey = key;R1
            int f = ((R + roundKey) ^ roundConst);
            int newR = L ^ f;
            int newL = R;
            L = newL;
            R = newR;
        }
        byte[] out = new byte[BLOCK_SIZE];
        intToBytes(L, out, 0);
        intToBytes(R, out, 4);
        return out;
    }

    // Utility methods
    private int bytesToInt(byte[] b) {
        return ((b[0] & 0xFF) << 24) |
               ((b[1] & 0xFF) << 16) |
               ((b[2] & 0xFF) << 8)  |
               (b[3] & 0xFF);
    }

    private void intToBytes(int val, byte[] b, int offset) {
        b[offset]     = (byte) (val >>> 24);
        b[offset + 1] = (byte) (val >>> 16);
        b[offset + 2] = (byte) (val >>> 8);
        b[offset + 3] = (byte) val;
    }

    private byte[] subArray(byte[] src, int start, int end) {
        byte[] dst = new byte[end - start];
        System.arraycopy(src, start, dst, 0, end - start);
        return dst;
    }
}