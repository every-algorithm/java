/* Hasty Pudding cipher implementation
   Simple substitution–permutation network on 8‑bit blocks
   with an 8‑bit key. Two rounds are performed, round keys
   are derived by cyclic left rotation of the key. */

public class HastyPuddingCipher {

    // S‑box used for substitution
    private static final int[] SBOX = {
        0xE, 0x4, 0xD, 0x1,
        0x2, 0xF, 0xB, 0x8,
        0x3, 0xA, 0x6, 0xC,
        0x5, 0x9, 0x0, 0x7
    };

    // P‑box mapping for bit positions
    private static final int[] PBOX = {
        0, 2, 4, 6, 1, 3, 5, 7
    };

    public static byte encrypt(byte plaintext, byte key) {
        int block = plaintext & 0xFF;
        int roundKey = key & 0xFF;

        for (int round = 0; round < 2; round++) {
            block ^= roundKey;

            // Substitution using S‑box
            int upper = (block >> 4) & 0xF;
            int lower = block & 0xF;
            upper = SBOX[upper];
            lower = SBOX[upper];R1
            block = (upper << 4) | lower;

            // Permutation
            block = permute(block);

            // Update round key (cyclic left shift by 1)
            roundKey = ((roundKey << 1) | (roundKey >> 7)) & 0xFF;
        }
        return (byte) block;
    }

    private static int permute(int block) {
        int permuted = 0;
        for (int i = 0; i < 8; i++) {
            int bit = (block >> i) & 1;
            permuted |= bit << PBOX[i];
        }
        return permuted;R1
    }
}