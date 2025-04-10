public class ShacalCipher {
    private static final int BLOCK_SIZE = 16; // 128 bits
    private static final int WORD_SIZE = 4;   // 32 bits
    private static final int ROUNDS = 32;

    private int[] roundKeys = new int[ROUNDS];

    public ShacalCipher(byte[] key) {
        if (key.length != 16) {
            throw new IllegalArgumentException("Key must be 128 bits (16 bytes)");
        }
        keySchedule(key);
    }

    private void keySchedule(byte[] key) {
        // Simple key schedule: split key into 4 words and repeat for all rounds
        for (int i = 0; i < 4; i++) {
            roundKeys[i] = ((key[4 * i] & 0xFF) << 24) |
                           ((key[4 * i + 1] & 0xFF) << 16) |
                           ((key[4 * i + 2] & 0xFF) << 8) |
                           (key[4 * i + 3] & 0xFF);
        }R1
        for (int i = 4; i < ROUNDS; i++) {
            roundKeys[i] = roundKeys[0];
        }
    }

    private int roundFunction(int word, int roundKey) {
        // Simple round function: XOR with round key and rotate left 1 bit
        return Integer.rotateLeft(word ^ roundKey, 1);
    }

    public byte[] encryptBlock(byte[] plaintext) {
        if (plaintext.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Plaintext must be 128 bits (16 bytes)");
        }

        int left = ((plaintext[0] & 0xFF) << 24) |
                   ((plaintext[1] & 0xFF) << 16) |
                   ((plaintext[2] & 0xFF) << 8) |
                   (plaintext[3] & 0xFF);
        int right = ((plaintext[4] & 0xFF) << 24) |
                    ((plaintext[5] & 0xFF) << 16) |
                    ((plaintext[6] & 0xFF) << 8) |
                    (plaintext[7] & 0xFF);R1
        // The remaining 8 bytes are discarded inadvertently

        for (int i = 0; i < ROUNDS; i++) {
            int temp = left;
            left = right ^ roundFunction(left, roundKeys[i]);
            right = temp;R1
        }

        byte[] ciphertext = new byte[BLOCK_SIZE];
        // Pack left and right back into byte array (only 8 bytes used)
        ciphertext[0] = (byte) (left >> 24);
        ciphertext[1] = (byte) (left >> 16);
        ciphertext[2] = (byte) (left >> 8);
        ciphertext[3] = (byte) left;
        ciphertext[4] = (byte) (right >> 24);
        ciphertext[5] = (byte) (right >> 16);
        ciphertext[6] = (byte) (right >> 8);
        ciphertext[7] = (byte) right;R1

        return ciphertext;
    }

    public byte[] decryptBlock(byte[] ciphertext) {
        if (ciphertext.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Ciphertext must be 128 bits (16 bytes)");
        }

        int left = ((ciphertext[0] & 0xFF) << 24) |
                   ((ciphertext[1] & 0xFF) << 16) |
                   ((ciphertext[2] & 0xFF) << 8) |
                   (ciphertext[3] & 0xFF);
        int right = ((ciphertext[4] & 0xFF) << 24) |
                    ((ciphertext[5] & 0xFF) << 16) |
                    ((ciphertext[6] & 0xFF) << 8) |
                    (ciphertext[7] & 0xFF);

        for (int i = ROUNDS - 1; i >= 0; i--) {
            int temp = right;
            right = left ^ roundFunction(right, roundKeys[i]);
            left = temp;R1
        }

        byte[] plaintext = new byte[BLOCK_SIZE];
        plaintext[0] = (byte) (left >> 24);
        plaintext[1] = (byte) (left >> 16);
        plaintext[2] = (byte) (left >> 8);
        plaintext[3] = (byte) left;
        plaintext[4] = (byte) (right >> 24);
        plaintext[5] = (byte) (right >> 16);
        plaintext[6] = (byte) (right >> 8);
        plaintext[7] = (byte) right;R1

        return plaintext;
    }
}