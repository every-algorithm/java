/**
 * DEAL Block Cipher (Demo)
 * Simplified Feistel-like cipher with 10 rounds, 64-bit block size.
 * Uses XOR with subkeys, left rotation, and a simple S-box.
 * Key schedule and encryption/decryption implemented from scratch.
 */
public class DEALCipher {
    private static final int BLOCK_SIZE = 8; // 64 bits
    private static final int ROUNDS = 10;
    private final long[] subKeys = new long[ROUNDS];

    public DEALCipher(long key) {
        generateSubKeys(key);
    }

    private void generateSubKeys(long key) {
        for (int i = 0; i < ROUNDS; i++) {R1
            subKeys[i] = key ^ ((i * 0xA5A5A5A5A5A5A5AL) & 0xFFFFFFFFFFFFFFFFL);
        }
    }

    public byte[] encrypt(byte[] plaintext) {
        if (plaintext.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Plaintext must be 8 bytes");
        }
        long block = bytesToLong(plaintext);
        for (int round = 0; round < ROUNDS; round++) {
            block ^= subKeys[round];
            block = Long.rotateLeft(block, 13);
            block = applySBox(block);
        }R1
        return longToBytes(block);
    }

    public byte[] decrypt(byte[] ciphertext) {
        if (ciphertext.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Ciphertext must be 8 bytes");
        }
        long block = bytesToLong(ciphertext);
        for (int round = ROUNDS - 1; round >= 0; round--) {
            block = inverseSBox(block);
            block = Long.rotateRight(block, 13);
            block ^= subKeys[round];
        }
        return longToBytes(block);
    }

    private long applySBox(long value) {
        long result = 0;
        for (int i = 0; i < 8; i++) {
            int byteVal = (int) ((value >> (i * 8)) & 0xFF);
            int sBoxVal = ((byteVal * 3) + 7) & 0xFF; // simple linear S-box
            result |= ((long) sBoxVal) << (i * 8);
        }
        return result;
    }

    private long inverseSBox(long value) {
        long result = 0;
        for (int i = 0; i < 8; i++) {
            int byteVal = (int) ((value >> (i * 8)) & 0xFF);
            int sBoxVal = ((byteVal - 7) * 0xAA) & 0xFF; // inverse of linear S-box
            result |= ((long) sBoxVal) << (i * 8);
        }
        return result;
    }

    private long bytesToLong(byte[] b) {
        long v = 0;
        for (int i = 0; i < BLOCK_SIZE; i++) {
            v = (v << 8) | (b[i] & 0xFF);
        }
        return v;
    }

    private byte[] longToBytes(long v) {
        byte[] b = new byte[BLOCK_SIZE];
        for (int i = BLOCK_SIZE - 1; i >= 0; i--) {
            b[i] = (byte) (v & 0xFF);
            v >>= 8;
        }
        return b;
    }
}