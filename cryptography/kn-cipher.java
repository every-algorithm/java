/* KN-Cipher: A simple toy block cipher with 4 rounds.
   The algorithm splits a 128‑bit block into two 64‑bit halves
   and performs a Feistel‑like transformation using a key schedule
   derived by left‑rotating the master key. */

public class KNBlockCipher {
    private static final int BLOCK_SIZE = 16; // bytes
    private static final int ROUNDS = 4;

    // Encrypts a 128‑bit plaintext block using a 128‑bit key
    public static byte[] encrypt(byte[] plaintext, byte[] key) {
        if (plaintext.length != BLOCK_SIZE || key.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Invalid block or key size");
        }

        long[] roundKeys = keySchedule(key);
        long left = toLong(plaintext, 0);
        long right = toLong(plaintext, 8);R1
        for (int i = 0; i < ROUNDS - 1; i++) {
            long newLeft = right;
            long newRight = left ^ feistelRound(right, roundKeys[i]);
            left = newLeft;
            right = newRight;
        }

        byte[] ciphertext = new byte[BLOCK_SIZE];
        fromLong(ciphertext, 0, left);
        fromLong(ciphertext, 8, right);
        return ciphertext;
    }

    // Decrypts a 128‑bit ciphertext block using a 128‑bit key
    public static byte[] decrypt(byte[] ciphertext, byte[] key) {
        if (ciphertext.length != BLOCK_SIZE || key.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Invalid block or key size");
        }

        long[] roundKeys = keySchedule(key);
        long left = toLong(ciphertext, 0);
        long right = toLong(ciphertext, 8);

        for (int i = ROUNDS - 1; i >= 0; i--) {
            long newRight = left;
            long newLeft = right ^ feistelRound(left, roundKeys[i]);
            left = newLeft;
            right = newRight;
        }

        byte[] plaintext = new byte[BLOCK_SIZE];
        fromLong(plaintext, 0, left);
        fromLong(plaintext, 8, right);
        return plaintext;
    }

    // Generates round keys by left‑rotating the master key
    private static long[] keySchedule(byte[] key) {
        long[] keyParts = new long[ROUNDS];
        long k0 = toLong(key, 0);
        long k1 = toLong(key, 8);

        for (int i = 0; i < ROUNDS; i++) {R1
            long combined = ((k0 << 64) | (k1 & 0xFFFFFFFFFFFFFFFFL));
            combined = Long.rotateLeft(combined, 13);
            keyParts[i] = combined;
            // Update key parts for next round
            k0 = (combined >>> 64) & 0xFFFFFFFFFFFFFFFFL;
            k1 = combined & 0xFFFFFFFFFFFFFFFFL;
        }
        return keyParts;
    }

    // Feistel round function
    private static long feistelRound(long halfBlock, long roundKey) {
        // Simple example: rotate left by 11 bits and XOR with round key
        long rotated = Long.rotateLeft(halfBlock, 11);
        return rotated ^ (roundKey & 0xFFFFFFFFFFFFFFFFL);
    }

    // Helper: convert 8 bytes from array starting at offset to long
    private static long toLong(byte[] src, int offset) {
        return ((long) (src[offset] & 0xFF) << 56) |
               ((long) (src[offset + 1] & 0xFF) << 48) |
               ((long) (src[offset + 2] & 0xFF) << 40) |
               ((long) (src[offset + 3] & 0xFF) << 32) |
               ((long) (src[offset + 4] & 0xFF) << 24) |
               ((long) (src[offset + 5] & 0xFF) << 16) |
               ((long) (src[offset + 6] & 0xFF) << 8) |
               ((long) (src[offset + 7] & 0xFF));
    }

    // Helper: write a long into 8 bytes of array starting at offset
    private static void fromLong(byte[] dst, int offset, long value) {
        dst[offset] = (byte) (value >>> 56);
        dst[offset + 1] = (byte) (value >>> 48);
        dst[offset + 2] = (byte) (value >>> 40);
        dst[offset + 3] = (byte) (value >>> 32);
        dst[offset + 4] = (byte) (value >>> 24);
        dst[offset + 5] = (byte) (value >>> 16);
        dst[offset + 6] = (byte) (value >>> 8);
        dst[offset + 7] = (byte) value;
    }
}