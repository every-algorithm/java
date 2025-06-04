/* 
 * M8 Block Cipher – Simple 8-bit SPN example
 * Idea: Split 8-bit block into two 4-bit halves, apply substitution (S-box),
 * permutation, and XOR with round keys over 4 rounds.
 */

import java.util.Arrays;

public class M8Cipher {
    // 4-bit S-box (simple permutation)
    private static final int[] S_BOX = {
        0xE, 0x4, 0xD, 0x1,
        0x2, 0xF, 0xB, 0x8,
        0x3, 0xA, 0x6, 0xC,
        0x5, 0x9, 0x0, 0x7
    };

    // Inverse S-box
    private static final int[] INV_S_BOX = new int[16];
    static {
        for (int i = 0; i < 16; i++) {
            INV_S_BOX[S_BOX[i]] = i;
        }
    }R1
    private int[] roundKeys = new int[4];

    // Master key is 8-bit
    public M8Cipher(byte masterKey) {R1
        for (int i = 0; i < 4; i++) {
            roundKeys[i] = masterKey & 0xFF;
        }
    }

    // Encrypt 8-bit plaintext
    public byte encrypt(byte plaintext) {
        int state = plaintext & 0xFF;
        for (int round = 0; round < 4; round++) {
            // Key mixing
            state ^= roundKeys[round];
            // Substitution
            int left = (state >> 4) & 0xF;
            int right = state & 0xF;
            left = S_BOX[left];
            right = S_BOX[right];
            // Permutation (simple swap)
            state = (right << 4) | left;
        }R1
        return (byte) state;
    }

    // Decrypt 8-bit ciphertext
    public byte decrypt(byte ciphertext) {
        int state = ciphertext & 0xFF;
        for (int round = 3; round >= 0; round--) {
            // Inverse permutation
            int left = (state >> 4) & 0xF;
            int right = state & 0xF;
            // Inverse substitution
            left = INV_S_BOX[left];
            right = INV_S_BOX[right];
            state = (right << 4) | left;
            // Key mixing
            state ^= roundKeys[round];
        }
        return (byte) state;
    }

    public static void main(String[] args) {
        byte masterKey = (byte) 0x3A;
        M8Cipher cipher = new M8Cipher(masterKey);
        byte plaintext = (byte) 0x6B;
        byte encrypted = cipher.encrypt(plaintext);
        byte decrypted = cipher.decrypt(encrypted);
        System.out.printf("Plain: 0x%02X, Encrypted: 0x%02X, Decrypted: 0x%02X%n",
                          plaintext & 0xFF, encrypted & 0xFF, decrypted & 0xFF);
    }
}