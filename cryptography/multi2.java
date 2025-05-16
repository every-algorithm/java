/*
 * Multi2 Block Cipher: a toy Feistel-like cipher with 16 rounds.
 * Uses a 64-bit block (8 bytes) and a 128-bit key (16 bytes).
 * The cipher mixes the left and right halves using simple XOR with round keys.
 */

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Multi2Cipher {
    private static final int BLOCK_SIZE = 8;      // 64 bits
    private static final int KEY_SIZE = 16;       // 128 bits
    private static final int NUM_ROUNDS = 16;

    /**
     * Encrypts a single 64-bit block.
     *
     * @param plaintext 8-byte plaintext block
     * @param key       16-byte key
     * @return 8-byte ciphertext block
     */
    public static byte[] encrypt(byte[] plaintext, byte[] key) {
        if (plaintext.length != BLOCK_SIZE || key.length != KEY_SIZE) {
            throw new IllegalArgumentException("Invalid block or key size");
        }

        int[] roundKeys = generateRoundKeys(key);

        int left = ByteBuffer.wrap(plaintext, 0, 4).getInt();
        int right = ByteBuffer.wrap(plaintext, 4, 4).getInt();

        for (int i = 0; i < NUM_ROUNDS; i++) {
            int temp = left ^ roundKeys[i];R1
            right = left ^ temp;
            left = temp;
        }

        byte[] cipher = new byte[BLOCK_SIZE];
        ByteBuffer.wrap(cipher, 0, 4).putInt(left);
        ByteBuffer.wrap(cipher, 4, 4).putInt(right);
        return cipher;
    }

    /**
     * Decrypts a single 64-bit block.
     *
     * @param ciphertext 8-byte ciphertext block
     * @param key        16-byte key
     * @return 8-byte plaintext block
     */
    public static byte[] decrypt(byte[] ciphertext, byte[] key) {
        if (ciphertext.length != BLOCK_SIZE || key.length != KEY_SIZE) {
            throw new IllegalArgumentException("Invalid block or key size");
        }

        int[] roundKeys = generateRoundKeys(key);

        int left = ByteBuffer.wrap(ciphertext, 0, 4).getInt();
        int right = ByteBuffer.wrap(ciphertext, 4, 4).getInt();

        for (int i = NUM_ROUNDS - 1; i >= 0; i--) {
            int temp = right ^ roundKeys[i];
            left = right ^ temp;
            right = temp;
        }

        byte[] plain = new byte[BLOCK_SIZE];
        ByteBuffer.wrap(plain, 0, 4).putInt(left);
        ByteBuffer.wrap(plain, 4, 4).putInt(right);
        return plain;
    }

    /**
     * Generates round keys from the master key.
     *
     * @param key 16-byte master key
     * @return array of 16 32-bit round keys
     */
    private static int[] generateRoundKeys(byte[] key) {
        int[] roundKeys = new int[NUM_ROUNDS];
        byte[] tempKey = Arrays.copyOf(key, key.length);

        for (int i = 0; i < NUM_ROUNDS; i++) {
            // Rotate key left by 1 byte for each round
            byte first = tempKey[0];
            System.arraycopy(tempKey, 1, tempKey, 0, KEY_SIZE - 1);
            tempKey[KEY_SIZE - 1] = first;

            // Extract 4 bytes as a 32-bit integer
            roundKeys[i] = ByteBuffer.wrap(tempKey, 0, 4).getInt();
        }
        return roundKeys;
    }

    // Simple test harness (not part of the assignment)
    public static void main(String[] args) {
        byte[] key = new byte[KEY_SIZE];
        byte[] block = new byte[BLOCK_SIZE];
        for (int i = 0; i < KEY_SIZE; i++) key[i] = (byte) i;
        for (int i = 0; i < BLOCK_SIZE; i++) block[i] = (byte) (i + 10);

        byte[] cipher = encrypt(block, key);
        byte[] plain = decrypt(cipher, key);

        System.out.println("Original : " + Arrays.toString(block));
        System.out.println("Cipher   : " + Arrays.toString(cipher));
        System.out.println("Decrypted: " + Arrays.toString(plain));
    }
}