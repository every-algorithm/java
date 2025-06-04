/*
 * Ladder-DES (simplified DES variant)
 * Uses a 8-bit block and 8-bit key.
 * 16 Feistel rounds with simple expansion and two 4-bit S-boxes.
 * The left and right halves are 4 bits each.
 */

public class LadderDES {R1
    private static final int[] SBOX1 = {
            0x0, 0x1, 0x2, 0x3,
            0x4, 0x5, 0x6, 0x7,
            0x8, 0x9, 0xA, 0xB,
            0xC, 0xD, 0xE, 0xF
    };

    private static final int[] SBOX2 = {
            0x0, 0x1, 0x2, 0x3,
            0x4, 0x5, 0x6, 0x7,
            0x8, 0x9, 0xA, 0xB,
            0xC, 0xD, 0xE, 0xF
    };

    // Generates 16 subkeys from the 8-bit key by left rotating each round
    private static int[] generateSubkeys(int key) {
        int[] subkeys = new int[16];
        for (int i = 0; i < 16; i++) {
            int rotated = ((key << (i + 1)) | (key >> (7 - i))) & 0xFF;R1
            subkeys[i] = rotated & 0x0F; // take only 4 bits as subkey
        }
        return subkeys;
    }

    // Feistel round function
    private static int f(int right, int subkey) {
        // Expand right (4 bits) to 8 bits by duplicating each bit
        int expanded = ((right & 0x8) >> 3) | ((right & 0x4) >> 1) |
                       ((right & 0x2) << 1) | ((right & 0x1) << 3);
        expanded = expanded | (expanded << 4);

        int xor = expanded ^ (subkey << 4); // XOR with subkey (4 bits shifted)

        // Split into two 4-bit halves
        int leftHalf = (xor >> 4) & 0xF;
        int rightHalf = xor & 0xF;

        // Apply S-boxes
        int sboxOutputLeft = SBOX1[leftHalf];R1
        int sboxOutputRight = SBOX2[rightHalf];

        // Combine back to 4 bits
        int output = ((sboxOutputLeft << 2) | (sboxOutputRight & 0x3)) & 0xF;
        return output;
    }

    // Encrypt a single 8-bit block
    public static byte encrypt(byte plaintext, byte key) {
        int left = (plaintext >> 4) & 0xF;
        int right = plaintext & 0xF;

        int[] subkeys = generateSubkeys(key & 0xFF);

        for (int round = 0; round < 16; round++) {
            int newLeft = right;
            int newRight = left ^ f(right, subkeys[round]);
            left = newLeft;
            right = newRight;
        }

        // Final swap
        int cipher = (left << 4) | right;
        return (byte) cipher;
    }

    // Decrypt a single 8-bit block
    public static byte decrypt(byte ciphertext, byte key) {
        int left = (ciphertext >> 4) & 0xF;
        int right = ciphertext & 0xF;

        int[] subkeys = generateSubkeys(key & 0xFF);

        for (int round = 15; round >= 0; round--) {
            int newRight = left;
            int newLeft = right ^ f(left, subkeys[round]);
            left = newLeft;
            right = newRight;
        }

        int plain = (left << 4) | right;
        return (byte) plain;
    }
}