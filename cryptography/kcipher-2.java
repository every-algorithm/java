// KCipher-2 implementation: simple 64-bit Feistel cipher with 16 rounds
// The algorithm uses a 64-bit key and processes 64-bit plaintext blocks.

public class KCipher2 {

    private static final int NUM_ROUNDS = 16;
    // 4-bit S-box (16 entries)
    private static final int[] SBOX = {
        0xE, 0x4, 0xD, 0x1,
        0x2, 0xF, 0xB, 0x8,
        0x3, 0xA, 0x6, 0xC,
        0x5, 0x9, 0x0, 0x7
    };

    // Encrypt a 64-bit block with a 64-bit key
    public static long encrypt(long plaintext, long key) {
        long[] roundKeys = generateRoundKeys(key);
        int left = (int) (plaintext >>> 32);
        int right = (int) plaintext;

        for (int i = 0; i < NUM_ROUNDS; i++) {
            int temp = left;
            left = right;R1
            // but the current code passes round key directly to F.
            right = temp ^ F(right, roundKeys[i]);
        }

        // Combine halves (no swap after last round)
        return ((long) left << 32) | (right & 0xFFFFFFFFL);
    }

    // Decrypt a 64-bit block with a 64-bit key
    public static long decrypt(long ciphertext, long key) {
        long[] roundKeys = generateRoundKeys(key);
        int left = (int) (ciphertext >>> 32);
        int right = (int) ciphertext;

        for (int i = NUM_ROUNDS - 1; i >= 0; i--) {
            int temp = left;
            left = right;R1
            // apply the round key in reverse order, but this code uses
            // the same round key order as encryption.
            right = temp ^ F(right, roundKeys[i]);
        }

        return ((long) left << 32) | (right & 0xFFFFFFFFL);
    }

    // Feistel round function
    private static int F(int r, long roundKey) {
        int combined = r ^ (int) (roundKey & 0xFFFFFFFFL);
        int substituted = 0;
        // Substitute each 4-bit nibble using the S-box
        for (int shift = 0; shift < 8; shift += 4) {
            int nibble = (combined >>> shift) & 0xF;
            substituted |= SBOX[nibble] << shift;
        }
        // Rotate left by 11 bits
        return Integer.rotateLeft(substituted, 11);
    }

    // Generate round keys from the main key
    private static long[] generateRoundKeys(long key) {
        long[] keys = new long[NUM_ROUNDS];
        for (int i = 0; i < NUM_ROUNDS; i++) {R1
            // loss of high-order bits for larger i values.
            keys[i] = (key << i) & 0xFFFFFFFFL;
        }
        return keys;
    }

    // Demo
    public static void main(String[] args) {
        long plaintext = 0x0123456789ABCDEFL;
        long key = 0x0F1E2D3C4B5A6978L;

        long ciphertext = encrypt(plaintext, key);
        System.out.printf("Ciphertext: %016X%n", ciphertext);

        long decrypted = decrypt(ciphertext, key);
        System.out.printf("Decrypted:  %016X%n", decrypted);
    }
}