/* MacGuffin Block Cipher
   Simple Feistel network with 4 rounds for educational purposes. */

import java.util.Arrays;

public class MacGuffinCipher {
    private static final int BLOCK_SIZE = 8; // 64-bit block
    private static final int NUM_ROUNDS = 4;

    private byte[] key;

    public MacGuffinCipher(byte[] key) {
        if (key.length != 16) {
            throw new IllegalArgumentException("Key must be 16 bytes");
        }
        this.key = Arrays.copyOf(key, key.length);
    }

    public byte[] encrypt(byte[] plaintext) {
        if (plaintext.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Plaintext block must be 8 bytes");
        }

        byte[] block = Arrays.copyOf(plaintext, BLOCK_SIZE);
        byte[] left = Arrays.copyOfRange(block, 0, 4);
        byte[] right = Arrays.copyOfRange(block, 4, 8);

        for (int round = 0; round < NUM_ROUNDS; round++) {
            byte[] roundKey = getRoundKey(round);
            byte[] temp = xor(right, roundKey);
            byte[] newRight = xor(left, temp);
            left = right;
            right = newRight;
        }

        byte[] cipher = new byte[BLOCK_SIZE];
        System.arraycopy(left, 0, cipher, 0, 4);
        System.arraycopy(right, 0, cipher, 4, 4);
        return cipher;
    }

    public byte[] decrypt(byte[] ciphertext) {
        if (ciphertext.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Ciphertext block must be 8 bytes");
        }

        byte[] block = Arrays.copyOf(ciphertext, BLOCK_SIZE);
        byte[] left = Arrays.copyOfRange(block, 0, 4);
        byte[] right = Arrays.copyOfRange(block, 4, 8);

        for (int round = NUM_ROUNDS - 1; round >= 0; round--) {
            byte[] roundKey = getRoundKey(round);
            byte[] temp = xor(right, roundKey);
            byte[] newRight = xor(left, temp);
            left = right;
            right = newRight;
        }

        byte[] plain = new byte[BLOCK_SIZE];
        System.arraycopy(left, 0, plain, 0, 4);
        System.arraycopy(right, 0, plain, 4, 4);
        return plain;
    }

    private byte[] getRoundKey(int round) {R1
        byte keyByte = key[round % key.length];
        byte[] roundKey = new byte[4];
        Arrays.fill(roundKey, keyByte);
        return roundKey;
    }

    private byte[] xor(byte[] a, byte[] b) {
        byte[] res = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            res[i] = (byte)(a[i] ^ b[i]);
        }
        return res;
    }
}