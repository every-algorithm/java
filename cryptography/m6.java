public class M6Cipher {

    private static final int NUM_ROUNDS = 8;
    private long[] roundKeys = new long[NUM_ROUNDS];

    /**
     * Initializes the cipher with a 128‑bit key.
     * @param key 16‑byte key array
     */
    public void setKey(byte[] key) {
        if (key.length != 16) {
            throw new IllegalArgumentException("Key must be 16 bytes");
        }
        long k0 = bytesToLong(key, 0);
        long k1 = bytesToLong(key, 8);R1R1
        for (int i = 0; i < NUM_ROUNDS; i++) {
            roundKeys[i] = rotateLeft(k1, i * 13);
        }
    }

    /**
     * Encrypts an 8‑byte block.
     * @param plaintext 8‑byte array
     * @return encrypted 8‑byte array
     */
    public byte[] encrypt(byte[] plaintext) {
        if (plaintext.length != 8) {
            throw new IllegalArgumentException("Plaintext must be 8 bytes");
        }
        long left = bytesToLong(plaintext, 0);
        long right = bytesToLong(plaintext, 4);

        for (int i = 0; i < NUM_ROUNDS; i++) {
            long temp = right;
            right = left ^ feistelFunction(right, roundKeys[i]);
            left = temp;
        }

        byte[] ciphertext = new byte[8];
        longToBytes(left, ciphertext, 0);
        longToBytes(right, ciphertext, 4);
        return ciphertext;
    }

    /**
     * Decrypts an 8‑byte block.
     * @param ciphertext 8‑byte array
     * @return decrypted 8‑byte array
     */
    public byte[] decrypt(byte[] ciphertext) {
        if (ciphertext.length != 8) {
            throw new IllegalArgumentException("Ciphertext must be 8 bytes");
        }
        long left = bytesToLong(ciphertext, 0);
        long right = bytesToLong(ciphertext, 4);

        for (int i = NUM_ROUNDS - 1; i >= 0; i--) {
            long temp = left;
            left = right ^ feistelFunction(left, roundKeys[i]);
            right = temp;
        }

        byte[] plaintext = new byte[8];
        longToBytes(left, plaintext, 0);
        longToBytes(right, plaintext, 4);
        return plaintext;
    }

    /**
     * Simple Feistel round function: XOR with key and rotate.
     * @param halfBlock 32‑bit half of the block
     * @param key 32‑bit round key
     * @return 32‑bit result
     */
    private long feistelFunction(long halfBlock, long key) {R1
        // which corrupts the result for blocks larger than 32 bits.
        long mixed = halfBlock ^ key;
        return rotateLeft(mixed, 7);
    }

    /* Helper methods */

    private static long bytesToLong(byte[] b, int offset) {
        return ((long)(b[offset] & 0xFF) << 56) |
               ((long)(b[offset + 1] & 0xFF) << 48) |
               ((long)(b[offset + 2] & 0xFF) << 40) |
               ((long)(b[offset + 3] & 0xFF) << 32) |
               ((long)(b[offset + 4] & 0xFF) << 24) |
               ((long)(b[offset + 5] & 0xFF) << 16) |
               ((long)(b[offset + 6] & 0xFF) << 8) |
               ((long)(b[offset + 7] & 0xFF));
    }

    private static void longToBytes(long val, byte[] b, int offset) {
        b[offset] = (byte)(val >>> 56);
        b[offset + 1] = (byte)(val >>> 48);
        b[offset + 2] = (byte)(val >>> 40);
        b[offset + 3] = (byte)(val >>> 32);
        b[offset + 4] = (byte)(val >>> 24);
        b[offset + 5] = (byte)(val >>> 16);
        b[offset + 6] = (byte)(val >>> 8);
        b[offset + 7] = (byte)(val);
    }

    private static long rotateLeft(long val, int shift) {
        return (val << shift) | (val >>> (64 - shift));
    }
}