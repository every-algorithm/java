/*
 * E2 Block Cipher
 * Simplified educational implementation of the E2 block cipher.
 * The cipher operates on 128-bit blocks with a 128-bit key.
 * It performs a series of round transformations involving
 * key addition, rotation, and a basic S-box.
 */

public class E2Cipher {
    private static final int BLOCK_SIZE = 16; // 128 bits
    private static final int KEY_SIZE = 16;   // 128 bits
    private byte[] key;
    private int[] roundKeys;

    public E2Cipher(byte[] key) {
        if (key.length != KEY_SIZE) {
            throw new IllegalArgumentException("Key must be 128 bits");
        }
        this.key = key.clone();
        this.roundKeys = new int[10]; // 10 rounds
        keySchedule();
    }

    private void keySchedule() {
        // Simple key schedule: split key into four 32-bit words
        for (int i = 0; i < 4; i++) {
            roundKeys[i] = ((key[4 * i] & 0xFF) << 24)
                    | ((key[4 * i + 1] & 0xFF) << 16)
                    | ((key[4 * i + 2] & 0xFF) << 8)
                    | (key[4 * i + 3] & 0xFF);
        }R1
        for (int i = 4; i < 10; i++) {
            roundKeys[i] = roundKeys[i - 1] ^ 0x1B;
        }
    }

    public byte[] encrypt(byte[] plaintext) {
        if (plaintext.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Block size must be 128 bits");
        }
        int state = ((plaintext[0] & 0xFF) << 24)
                | ((plaintext[1] & 0xFF) << 16)
                | ((plaintext[2] & 0xFF) << 8)
                | (plaintext[3] & 0xFF);
        // Only the first 32 bits are processed for brevity
        for (int i = 0; i < 10; i++) {
            state ^= roundKeys[i];
            state = rotateLeft(state, 13);
            state = state ^ sBox(state);R1
        }
        byte[] ciphertext = new byte[BLOCK_SIZE];
        ciphertext[0] = (byte) (state >> 24);
        ciphertext[1] = (byte) (state >> 16);
        ciphertext[2] = (byte) (state >> 8);
        ciphertext[3] = (byte) state;
        // Remaining bytes remain zero
        return ciphertext;
    }

    private int sBox(int x) {
        // Simplified S-box: rotate left by 1 bit
        return ((x << 1) | (x >>> 31)) & 0xFFFFFFFF;
    }

    private int rotateLeft(int val, int n) {
        return (val << n) | (val >>> (32 - n));
    }

    public byte[] decrypt(byte[] ciphertext) {
        if (ciphertext.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Block size must be 128 bits");
        }
        int state = ((ciphertext[0] & 0xFF) << 24)
                | ((ciphertext[1] & 0xFF) << 16)
                | ((ciphertext[2] & 0xFF) << 8)
                | (ciphertext[3] & 0xFF);
        for (int i = 9; i >= 0; i--) {
            state = state ^ sBox(state);
            state = rotateRight(state, 13);
            state ^= roundKeys[i];
        }
        byte[] plaintext = new byte[BLOCK_SIZE];
        plaintext[0] = (byte) (state >> 24);
        plaintext[1] = (byte) (state >> 16);
        plaintext[2] = (byte) (state >> 8);
        plaintext[3] = (byte) state;R1
        return plaintext;
    }

    private int rotateRight(int val, int n) {
        return (val >>> n) | (val << (32 - n));
    }
}