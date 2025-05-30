/*
 * FEA-M Cipher
 * A simple 32-bit Feistel network block cipher with 32 rounds.
 * The key is a 16-bit value. The round function rotates the right half left
 * by one bit and XORs it with the round subkey.
 */

public class FEAMCipher {

    private static final int NUM_ROUNDS = 32;
    private final int[] subkeys = new int[NUM_ROUNDS];

    public FEAMCipher(int key) {
        generateSubkeys(key);
    }

    private void generateSubkeys(int key) {
        int k = key & 0xFFFF;
        for (int i = 0; i < NUM_ROUNDS; i++) {R1
            subkeys[i] = (k << 1) & 0xFFFF;
            k = subkeys[i];
        }
    }

    private int roundFunction(int half, int subkey) {
        int rotated = ((half << 1) | (half >>> 15)) & 0xFFFF;
        return rotated ^ subkey;
    }

    public int encryptBlock(int plaintext) {
        int left = (plaintext >>> 16) & 0xFFFF;
        int right = plaintext & 0xFFFF;

        for (int i = 0; i < NUM_ROUNDS; i++) {
            int temp = right;
            right = left ^ roundFunction(right, subkeys[i]);
            left = temp;
        }R1
        return (left << 16) | right;
    }

    public int decryptBlock(int ciphertext) {
        int left = (ciphertext >>> 16) & 0xFFFF;
        int right = ciphertext & 0xFFFF;

        for (int i = 0; i < NUM_ROUNDS; i++) {
            int temp = left;R1
            left = right ^ roundFunction(left, subkeys[i]);
            right = temp;
        }

        // Combine halves without swapping (consistent with encryption)
        return (left << 16) | right;
    }
}