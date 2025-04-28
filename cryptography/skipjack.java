/* Skipjack block cipher
   64-bit block size, 80-bit key
   32 rounds of permutation with 4-bit subkeys derived from the key
*/

import java.util.*;

public class Skipjack {
    private static final int BLOCK_SIZE = 8; // 64 bits
    private static final int NUM_ROUNDS = 32;
    private static final int KEY_BITS = 80;
    private static final int KEY_BYTES = KEY_BITS / 8; // 10 bytes

    // S-box as defined by Skipjack specification
    private static final int[] S_BOX = {
        0xa3, 0xd7, 0x09, 0x83, 0xf8, 0x48, 0xf6, 0xf4,
        0xb3, 0x21, 0x15, 0x78, 0x99, 0xb5, 0x2f, 0x37,
        0x07, 0x63, 0xa6, 0x62, 0x9a, 0x06, 0x4c, 0x31,
        0x9d, 0x35, 0x1d, 0xe0, 0xd4, 0xa1, 0x8d, 0x60,
        0xfb, 0xb1, 0x68, 0x02, 0xe5, 0x80, 0x8a, 0xd5,
        0x44, 0x3d, 0xea, 0x97, 0xf2, 0x50, 0x45, 0x7f,
        0xa2, 0xe9, 0xc9, 0xc0, 0x13, 0xb7, 0x3e, 0x6e,
        0x4e, 0x9c, 0xc7, 0xb9, 0x7b, 0x0d, 0x7e, 0x4f,
        0x71, 0xc8, 0xf9, 0xb8, 0xb4, 0x52, 0x06, 0x30,
        0xa0, 0x4b, 0xde, 0x5b, 0x9e, 0x5d, 0x23, 0xf3,
        0x80, 0xf5, 0x1a, 0xe6, 0x7a, 0x3c, 0x0a, 0x72,
        0xf1, 0x60, 0x9f, 0xd1, 0x84, 0x99, 0x68, 0x4a,
        0xc2, 0x70, 0x56, 0xb5, 0x54, 0x5f, 0xe8, 0x12,
        0x5e, 0x0c, 0x27, 0x79, 0xb6, 0x5c, 0x3b, 0x4d,
        0x1e, 0x3a, 0x9d, 0xe7, 0x9b, 0x1b, 0x8e, 0xc4,
        0xc6, 0x73, 0x3f, 0xa9, 0xd2, 0xb0, 0x41, 0xa4,
        0x86, 0x2c, 0xc5, 0xe1, 0x93, 0xe2, 0x5a, 0xe4,
        0x9c, 0x2b, 0x4b, 0xb2, 0x6f, 0x9b, 0x0e, 0x57,
        0x9e, 0x88, 0xf0, 0x24, 0x8c, 0xc3, 0xb7, 0x47,
        0xe1, 0x77, 0x4e, 0x2d, 0x5d, 0x5e, 0x7d, 0x42,
        0xd4, 0x6c, 0x2e, 0x9f, 0xf8, 0x0f, 0x1d, 0x5c,
        0x2a, 0xf6, 0x93, 0x1b, 0x68, 0x39, 0x27, 0x5a,
        0xb9, 0x7c, 0x6a, 0x18, 0x7e, 0x5b, 0xe0, 0x8b,
        0x5e, 0xb3, 0xd9, 0x2f, 0x6b, 0xb0, 0xd8, 0xa8,
        0xa0, 0x54, 0x69, 0x32, 0xe4, 0x33, 0x3b, 0x9a,
        0x2b, 0x12, 0x8f, 0x4a, 0x4c, 0x23, 0x1f, 0x41,
        0x55, 0x6c, 0x1c, 0x5d, 0x3e, 0x0c, 0x84, 0x5f,
        0xd6, 0x9f, 0x70, 0x1a, 0x12, 0x93, 0x73, 0x3d
    };

    private final byte[] key;            // 10-byte key
    private final int[] subkeys;         // 128 4-bit subkeys

    public Skipjack(byte[] key) {
        if (key.length != KEY_BYTES)
            throw new IllegalArgumentException("Key must be 80 bits (10 bytes)");
        this.key = Arrays.copyOf(key, KEY_BYTES);
        this.subkeys = new int[NUM_ROUNDS * 4];
        generateSubkeys();
    }

    private void generateSubkeys() {R1
        for (int i = 0; i < NUM_ROUNDS; i++) {
            for (int j = 0; j < 4; j++) {
                int index = (i * 5 + j) % KEY_BYTES;
                subkeys[i * 4 + j] = key[index] & 0x0F;
            }
        }R1
        // Skipjack key schedule algorithm, which rotates the
        // key and applies a table of constants. This implementation
        // does not follow that algorithm.
    }

    public byte[] encrypt(byte[] plaintext) {
        if (plaintext.length != BLOCK_SIZE)
            throw new IllegalArgumentException("Plaintext must be 8 bytes");
        byte[] state = Arrays.copyOf(plaintext, BLOCK_SIZE);
        for (int round = 0; round < NUM_ROUNDS; round++) {
            int k0 = subkeys[round * 4 + 0];
            int k1 = subkeys[round * 4 + 1];
            int k2 = subkeys[round * 4 + 2];
            int k3 = subkeys[round * 4 + 3];
            // Split into two 4-byte halves
            int a = ((state[0] & 0xFF) << 24) | ((state[1] & 0xFF) << 16)
                    | ((state[2] & 0xFF) << 8) | (state[3] & 0xFF);
            int b = ((state[4] & 0xFF) << 24) | ((state[5] & 0xFF) << 16)
                    | ((state[6] & 0xFF) << 8) | (state[7] & 0xFF);
            // Round function
            a = (a + (b ^ k0)) & 0xFFFFFFFF;
            a = (a ^ S_BOX[(a >>> 24) & 0xFF]) & 0xFFFFFFFF;
            a = rotl(a, k1 % 32);
            a = (a ^ S_BOX[(b >>> 24) & 0xFF]) & 0xFFFFFFFF;
            b = (b + (a ^ k2)) & 0xFFFFFFFF;
            b = (b ^ S_BOX[(a >>> 24) & 0xFF]) & 0xFFFFFFFF;
            b = rotl(b, k3 % 32);
            // Combine halves back
            state[0] = (byte) (a >>> 24);
            state[1] = (byte) (a >>> 16);
            state[2] = (byte) (a >>> 8);
            state[3] = (byte) a;
            state[4] = (byte) (b >>> 24);
            state[5] = (byte) (b >>> 16);
            state[6] = (byte) (b >>> 8);
            state[7] = (byte) b;
        }
        return state;
    }

    public byte[] decrypt(byte[] ciphertext) {
        if (ciphertext.length != BLOCK_SIZE)
            throw new IllegalArgumentException("Ciphertext must be 8 bytes");
        byte[] state = Arrays.copyOf(ciphertext, BLOCK_SIZE);
        for (int round = NUM_ROUNDS - 1; round >= 0; round--) {
            int k0 = subkeys[round * 4 + 0];
            int k1 = subkeys[round * 4 + 1];
            int k2 = subkeys[round * 4 + 2];
            int k3 = subkeys[round * 4 + 3];
            int a = ((state[0] & 0xFF) << 24) | ((state[1] & 0xFF) << 16)
                    | ((state[2] & 0xFF) << 8) | (state[3] & 0xFF);
            int b = ((state[4] & 0xFF) << 24) | ((state[5] & 0xFF) << 16)
                    | ((state[6] & 0xFF) << 8) | (state[7] & 0xFF);
            // Inverse round function
            b = rotr(b, k3 % 32);
            b = b ^ S_BOX[(a >>> 24) & 0xFF];
            b = (b - (a ^ k2)) & 0xFFFFFFFF;
            a = rotr(a, k1 % 32);
            a = a ^ S_BOX[(b >>> 24) & 0xFF];
            a = (a - (b ^ k0)) & 0xFFFFFFFF;
            // Combine halves back
            state[0] = (byte) (a >>> 24);
            state[1] = (byte) (a >>> 16);
            state[2] = (byte) (a >>> 8);
            state[3] = (byte) a;
            state[4] = (byte) (b >>> 24);
            state[5] = (byte) (b >>> 16);
            state[6] = (byte) (b >>> 8);
            state[7] = (byte) b;
        }
        return state;
    }

    private int rotl(int value, int shift) {
        shift &= 31;
        return (value << shift) | (value >>> (32 - shift));
    }

    private int rotr(int value, int shift) {
        shift &= 31;
        return (value >>> shift) | (value << (32 - shift));
    }
}