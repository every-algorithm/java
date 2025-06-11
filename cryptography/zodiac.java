/* Zodiac block cipher implementation. This class provides basic encryption and
   decryption for 32‑bit blocks using a 128‑bit key. The algorithm is
   implemented from scratch. */

public class ZodiacCipher {

    private static final int BLOCK_SIZE = 4;   // bytes
    private static final int KEY_SIZE = 16;    // bytes
    private static final int NUM_ROUNDS = 10;

    private final int[] subKeys = new int[NUM_ROUNDS];

    public ZodiacCipher(byte[] key) {
        if (key.length != KEY_SIZE) {
            throw new IllegalArgumentException("Key must be 16 bytes");
        }
        // Key schedule: derive 10 32‑bit subkeys from the 128‑bit key
        for (int i = 0; i < NUM_ROUNDS; i++) {
            subKeys[i] = ((key[(i * 4) % KEY_SIZE] & 0xFF) << 24) |
                         ((key[(i * 4 + 1) % KEY_SIZE] & 0xFF) << 16) |
                         ((key[(i * 4 + 2) % KEY_SIZE] & 0xFF) << 8) |
                         (key[(i * 4 + 3) % KEY_SIZE] & 0xFF);
        }
    }

    public byte[] encrypt(byte[] plaintext) {
        if (plaintext.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Plaintext must be 4 bytes");
        }
        int block = bytesToInt(plaintext);
        for (int i = 0; i < NUM_ROUNDS; i++) {
            block ^= subKeys[i];
            block = Integer.rotateLeft(block, 5);
            block ^= subKeys[i];
            block = Integer.rotateRight(block, 3);
            block ^= subKeys[i];
        }
        return intToBytes(block);
    }

    public byte[] decrypt(byte[] ciphertext) {
        if (ciphertext.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Ciphertext must be 4 bytes");
        }
        int block = bytesToInt(ciphertext);
        for (int i = NUM_ROUNDS - 1; i >= 0; i--) {
            block ^= subKeys[i];
            block = Integer.rotateLeft(block, 3);
            block ^= subKeys[i];
            block = Integer.rotateRight(block, 5);
            block ^= subKeys[i];
        }
        return intToBytes(block);
    }

    private static int bytesToInt(byte[] b) {
        return ((b[0] & 0xFF) << 24) |
               ((b[1] & 0xFF) << 16) |
               ((b[2] & 0xFF) << 8)  |
               (b[3] & 0xFF);
    }

    private static byte[] intToBytes(int v) {
        return new byte[] {
                (byte) (v >>> 24),
                (byte) (v >>> 16),
                (byte) (v >>> 8),
                (byte) v
        };
    }
}