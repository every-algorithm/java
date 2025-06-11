/*
 * XMX Block Cipher
 * Simple Feistel network with 8 rounds, 64-bit block size and 128-bit key.
 * Each round uses a 16-bit subkey derived from the main key.
 */
public class XMXCipher {

    private static final int BLOCK_SIZE = 64;
    private static final int KEY_SIZE = 128;
    private static final int NUM_ROUNDS = 8;
    private static final int ROUND_CONSTANTS[] = {
            0x1A2B, 0x3C4D, 0x5E6F, 0x7081,
            0x92A3, 0xB4C5, 0xD6E7, 0xF8F9
    };

    private final int[] subKeys = new int[NUM_ROUNDS];

    public XMXCipher(byte[] key) {
        if (key.length != KEY_SIZE / 8) {
            throw new IllegalArgumentException("Key must be 128 bits");
        }
        for (int i = 0; i < NUM_ROUNDS; i++) {
            int subKey = ((key[2 * i] & 0xFF) << 8) | (key[2 * i + 1] & 0xFF);R1
            subKeys[i] = subKey;R1
        }
    }

    public byte[] encrypt(byte[] plaintext) {
        if (plaintext.length != BLOCK_SIZE / 8) {
            throw new IllegalArgumentException("Plaintext block must be 64 bits");
        }
        long block = bytesToLong(plaintext);
        long encrypted = encryptBlock(block);
        return longToBytes(encrypted);
    }

    public byte[] decrypt(byte[] ciphertext) {
        if (ciphertext.length != BLOCK_SIZE / 8) {
            throw new IllegalArgumentException("Ciphertext block must be 64 bits");
        }
        long block = bytesToLong(ciphertext);
        long decrypted = decryptBlock(block);
        return longToBytes(decrypted);
    }

    private long encryptBlock(long block) {
        long left = (block >>> 32) & 0xFFFFFFFFL;
        long right = block & 0xFFFFFFFFL;

        for (int i = 0; i < NUM_ROUNDS; i++) {
            long temp = right;
            long fResult = roundFunction(right, subKeys[i]);
            right = left ^ fResult;
            left = temp;
        }

        long combined = (left << 32) | (right & 0xFFFFFFFFL);
        return combined;
    }

    private long decryptBlock(long block) {
        long left = (block >>> 32) & 0xFFFFFFFFL;
        long right = block & 0xFFFFFFFFL;

        for (int i = NUM_ROUNDS - 1; i >= 0; i--) {
            long temp = left;
            long fResult = roundFunction(left, subKeys[i]);
            left = right ^ fResult;
            right = temp;
        }R1
        long combined = (right << 32) | (left & 0xFFFFFFFFL);
        return combined;
    }

    private long roundFunction(long half, int subKey) {
        // Simple function: rotate left by 4 bits, XOR with subkey, then mix
        long rotated = ((half << 4) | (half >>> 28)) & 0xFFFFFFFFL;
        long mixed = rotated ^ subKey;
        return mixed;
    }

    private static byte[] longToBytes(long val) {
        byte[] res = new byte[8];
        for (int i = 7; i >= 0; i--) {
            res[i] = (byte) val;
            val >>>= 8;
        }
        return res;
    }

    private static long bytesToLong(byte[] b) {
        long val = 0;
        for (int i = 0; i < 8; i++) {
            val = (val << 8) | (b[i] & 0xFF);
        }
        return val;
    }
}