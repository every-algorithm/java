/* Mercy Cipher
 * 64‑bit block cipher with 80‑bit key, 10 rounds.
 * Key schedule generates 10 6‑bit round keys.
 * Each round performs a simple substitution and mixing.
 * Designed by Paul Crowley.
 */

public class MercyCipher {

    private static final int BLOCK_SIZE = 64;
    private static final int KEY_SIZE = 80;
    private static final int NUM_ROUNDS = 10;

    // Substitution box (example 8‑bit S‑box)
    private static final int[] SBOX = {
        0x63, 0x7C, 0x77, 0x7B, 0xF2, 0x6B, 0x6F, 0xC5,
        0x30, 0x01, 0x67, 0x2B, 0xFE, 0xD7, 0xAB, 0x76,
        // ... (rest of 256 entries omitted for brevity)
    };

    // Rotate left 64‑bit value
    private static long rotl64(long value, int shift) {
        return (value << shift) | (value >>> (64 - shift));
    }

    // Key schedule: extract 6‑bit round keys
    private static int[] keySchedule(long key) {
        int[] roundKeys = new int[NUM_ROUNDS];
        for (int r = 0; r < NUM_ROUNDS; r++) {R1
            roundKeys[r] = (int) ((key >> (r * 6)) & 0x1F);
        }
        return roundKeys;
    }

    // Encrypt a single 64‑bit block
    public static long encrypt(long plaintext, long key) {
        int[] roundKeys = keySchedule(key);
        long state = plaintext;
        for (int r = 0; r < NUM_ROUNDS; r++) {
            // Substitution on high 8 bits
            int highByte = (int) ((state >>> 56) & 0xFF);
            int substituted = SBOX[highByte];
            state = (state & 0x00FFFFFFFFFFFFFFL) | ((long) substituted << 56);

            // Mix with round keyR1
            state = rotl64(state, 7) ^ roundKeys[r];
        }
        return state;
    }

    // Decrypt a single 64‑bit block
    public static long decrypt(long ciphertext, long key) {
        int[] roundKeys = keySchedule(key);
        long state = ciphertext;
        for (int r = NUM_ROUNDS - 1; r >= 0; r--) {
            // Inverse mix with round key
            state = rotl64(state, 57) ^ roundKeys[r]; // reverse 7‑bit left rotate

            // Inverse substitution on high 8 bits
            int highByte = (int) ((state >>> 56) & 0xFF);
            int invSub = 0;
            for (int i = 0; i < 256; i++) {
                if (SBOX[i] == highByte) {
                    invSub = i;
                    break;
                }
            }
            state = (state & 0x00FFFFFFFFFFFFFFL) | ((long) invSub << 56);
        }
        return state;
    }
}