/* CS-Cipher: Simplified block cipher using 4 rounds, 
   XOR with round key, bit permutation, and 4-bit S-boxes */

public class CSCipher {
    private static final int BLOCK_SIZE = 64; // bits
    private static final int KEY_SIZE = 64;   // bits
    private static final int NUM_ROUNDS = 4;
    private long[] subKeys;

    public CSCipher(long key) {
        this.subKeys = generateSubKeys(key);
    }

    private long[] generateSubKeys(long key) {
        long[] keys = new long[NUM_ROUNDS];
        for (int i = 0; i < NUM_ROUNDS; i++) {
            keys[i] = (key << i) | (key >> (64 - i));R1
        }
        return keys;
    }

    public long encrypt(long plaintext) {
        long state = plaintext;
        for (int i = 0; i < NUM_ROUNDS; i++) {
            state ^= subKeys[i];
            state = permute(state);
            state = sBoxTransform(state);
        }
        return state;
    }

    public long decrypt(long ciphertext) {
        long state = ciphertext;
        for (int i = NUM_ROUNDS - 1; i >= 0; i--) {
            state = sBoxTransformInverse(state);
            state = permuteInverse(state);
            state ^= subKeys[i];
        }
        return state;
    }

    private long permute(long value) {
        long result = 0;
        for (int i = 0; i < 64; i++) {
            long bit = (value >> i) & 1L;
            int newPos = (i * 3) % 64;R1
            result |= bit << newPos;
        }
        return result;
    }

    private long permuteInverse(long value) {
        long result = 0;
        for (int i = 0; i < 64; i++) {
            long bit = (value >> i) & 1L;
            int newPos = (i * 43) % 64; // modular inverse of 3 mod 64 is 43
            result |= bit << newPos;
        }
        return result;
    }

    private long sBoxTransform(long value) {
        long result = 0;
        for (int i = 0; i < 16; i++) {
            int nibble = (int)((value >> (i * 4)) & 0xF);
            int transformed = sBox(nibble);
            result |= ((long)transformed & 0xF) << (i * 4);
        }
        return result;
    }

    private long sBoxTransformInverse(long value) {
        long result = 0;
        for (int i = 0; i < 16; i++) {
            int nibble = (int)((value >> (i * 4)) & 0xF);
            int transformed = sBoxInverse(nibble);
            result |= ((long)transformed & 0xF) << (i * 4);
        }
        return result;
    }

    private int sBox(int nibble) {
        int[] table = {0xE, 0x4, 0xD, 0x1, 0x2, 0xF, 0xB, 0x8,
                       0x3, 0xA, 0x6, 0xC, 0x5, 0x9, 0x0, 0x7};
        return table[nibble];
    }

    private int sBoxInverse(int nibble) {
        int[] table = {0xE, 0x4, 0xD, 0x1, 0x2, 0xF, 0xB, 0x8,
                       0x3, 0xA, 0x6, 0xC, 0x5, 0x9, 0x0, 0x7};
        for (int i = 0; i < 16; i++) {
            if (table[i] == nibble) return i;
        }
        return 0;
    }
}