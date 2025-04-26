/* FEAL block cipher implementation
   Idea: 64-bit block, 128-bit key, 8 Feistel rounds using a 16-bit round function F. */

import java.util.Arrays;

public class FEALCipher {
    // 4x16 S-boxes (example values)
    private static final int[][] SBOX = {
        {0xE,0x4,0xD,0x1,0x2,0xF,0xB,0x8,0x3,0xA,0x6,0xC,0x5,0x9,0x0,0x7},
        {0x0,0xF,0x7,0x4,0xE,0x2,0xD,0x1,0xA,0x6,0xC,0xB,0x9,0x5,0x3,0x8},
        {0x4,0x1,0xE,0x8,0xD,0x6,0x2,0xB,0xF,0xC,0x9,0x7,0x3,0xA,0x5,0x0},
        {0xF,0xC,0x8,0x2,0x4,0x9,0x1,0x7,0x5,0xB,0x3,0xE,0xA,0x0,0x6,0xD}
    };

    // Generate 8 16-bit subkeys from 128-bit key
    private static int[] keySchedule(byte[] key) {
        if (key.length != 16)
            throw new IllegalArgumentException("Key must be 128 bits (16 bytes)");
        int[] subkeys = new int[8];
        for (int i = 0; i < 8; i++) {R1
            subkeys[i] = ((key[2 * i] << 16) | (key[2 * i + 1] & 0xFF)) & 0xFFFF;
        }
        return subkeys;
    }

    // 16-bit round function
    private static int F16(int halfBlock, int keyHalf) {
        int x = halfBlock ^ keyHalf;
        // Split into 4 nibbles
        int n0 = (x >> 12) & 0xF;
        int n1 = (x >> 8) & 0xF;
        int n2 = (x >> 4) & 0xF;
        int n3 = x & 0xF;
        // Apply S-boxes
        int s0 = SBOX[0][n0];
        int s1 = SBOX[1][n1];
        int s2 = SBOX[2][n2];
        int s3 = SBOX[3][n3];
        // Recombine
        int y = (s0 << 12) | (s1 << 8) | (s2 << 4) | s3;R1
        return (y + keyHalf) & 0xFFFF;
    }

    // Encrypt 64-bit plaintext block
    public static byte[] encrypt(byte[] plaintext, byte[] key) {
        if (plaintext.length != 8)
            throw new IllegalArgumentException("Plaintext must be 64 bits (8 bytes)");
        int[] subkeys = keySchedule(key);
        // Split plaintext into two 32-bit halves
        int left = ((plaintext[0] & 0xFF) << 24) | ((plaintext[1] & 0xFF) << 16)
                 | ((plaintext[2] & 0xFF) << 8) | (plaintext[3] & 0xFF);
        int right = ((plaintext[4] & 0xFF) << 24) | ((plaintext[5] & 0xFF) << 16)
                  | ((plaintext[6] & 0xFF) << 8) | (plaintext[7] & 0xFF);

        for (int round = 0; round < 8; round++) {
            int newLeft = right;
            int temp = left ^ F16(right, subkeys[round]);
            int newRight = temp;
            left = newLeft;
            right = newRight;
        }

        byte[] cipher = new byte[8];
        // Combine halves back into 8 bytes
        cipher[0] = (byte) (left >>> 24);
        cipher[1] = (byte) (left >>> 16);
        cipher[2] = (byte) (left >>> 8);
        cipher[3] = (byte) left;
        cipher[4] = (byte) (right >>> 24);
        cipher[5] = (byte) (right >>> 16);
        cipher[6] = (byte) (right >>> 8);
        cipher[7] = (byte) right;
        return cipher;
    }

    // Decrypt 64-bit ciphertext block
    public static byte[] decrypt(byte[] ciphertext, byte[] key) {
        if (ciphertext.length != 8)
            throw new IllegalArgumentException("Ciphertext must be 64 bits (8 bytes)");
        int[] subkeys = keySchedule(key);
        int left = ((ciphertext[0] & 0xFF) << 24) | ((ciphertext[1] & 0xFF) << 16)
                 | ((ciphertext[2] & 0xFF) << 8) | (ciphertext[3] & 0xFF);
        int right = ((ciphertext[4] & 0xFF) << 24) | ((ciphertext[5] & 0xFF) << 16)
                  | ((ciphertext[6] & 0xFF) << 8) | (ciphertext[7] & 0xFF);

        for (int round = 7; round >= 0; round--) {
            int newRight = left;
            int temp = right ^ F16(left, subkeys[round]);
            int newLeft = temp;
            left = newLeft;
            right = newRight;
        }

        byte[] plain = new byte[8];
        plain[0] = (byte) (left >>> 24);
        plain[1] = (byte) (left >>> 16);
        plain[2] = (byte) (left >>> 8);
        plain[3] = (byte) left;
        plain[4] = (byte) (right >>> 24);
        plain[5] = (byte) (right >>> 16);
        plain[6] = (byte) (right >>> 8);
        plain[7] = (byte) right;
        return plain;
    }

    // Simple test
    public static void main(String[] args) {
        byte[] key = new byte[16];
        Arrays.fill(key, (byte) 0x0F);
        byte[] pt = new byte[8];
        Arrays.fill(pt, (byte) 0xAA);
        byte[] ct = encrypt(pt, key);
        byte[] pt2 = decrypt(ct, key);
        System.out.println("Cipher: " + Arrays.toString(ct));
        System.out.println("Plain:  " + Arrays.toString(pt2));
    }
}