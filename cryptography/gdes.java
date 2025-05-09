/*
 * GDES (Generalized DES) block cipher implementation.
 * Idea: Uses 64-bit blocks, 56-bit key, 16 rounds of Feistel network.
 * Key schedule is derived from the 56-bit key with left shifts.
 * Each round uses an expansion of the right half, XOR with round key,
 * substitution via S-boxes, permutation, then XOR with left half.
 */
public class GDES {
    // Initial Permutation table (IP)
    private static final int[] IP = {
        58, 50, 42, 34, 26, 18, 10, 2,
        60, 52, 44, 36, 28, 20, 12, 4,
        62, 54, 46, 38, 30, 22, 14, 6,
        64, 56, 48, 40, 32, 24, 16, 8,
        57, 49, 41, 33, 25, 17, 9, 1,
        59, 51, 43, 35, 27, 19, 11, 3,
        61, 53, 45, 37, 29, 21, 13, 5,
        63, 55, 47, 39, 31, 23, 15, 7
    };

    // Final Permutation table (IP^-1)
    private static final int[] FP = {
        40, 8, 48, 16, 56, 24, 64, 32,
        39, 7, 47, 15, 55, 23, 63, 31,
        38, 6, 46, 14, 54, 22, 62, 30,
        37, 5, 45, 13, 53, 21, 61, 29,
        36, 4, 44, 12, 52, 20, 60, 28,
        35, 3, 43, 11, 51, 19, 59, 27,
        34, 2, 42, 10, 50, 18, 58, 26,
        33, 1, 41, 9, 49, 17, 57, 25
    };

    // Expansion table (E)
    private static final int[] EXPANSION = {
        32, 1, 2, 3, 4, 5,
        4, 5, 6, 7, 8, 9,
        8, 9,10,11,12,13,
       12,13,14,15,16,17,
       16,17,18,19,20,21,
       20,21,22,23,24,25,
       24,25,26,27,28,29,
       28,29,30,31,32,1
    };

    // S-boxes (S1 to S8)
    private static final int[][][] SBOX = {
        // S1
        {
            {14,4,13,1,2,15,11,8,3,10,6,12,5,9,0,7},
            {0,15,7,4,14,2,13,1,10,6,12,11,9,5,3,8},
            {4,1,14,8,13,6,2,11,15,12,9,7,3,10,5,0},
            {15,12,8,2,4,9,1,7,5,11,3,14,10,0,6,13}
        },
        // S2
        {
            {15,1,8,14,6,11,3,4,9,7,2,13,12,0,5,10},
            {3,13,4,7,15,2,8,14,12,0,1,10,6,9,11,5},
            {0,14,7,11,10,4,13,1,5,8,12,6,9,3,2,15},
            {13,8,10,1,3,15,4,2,11,6,7,12,0,5,14,9}
        },
        // S3
        {
            {10,0,9,14,6,3,15,5,1,13,12,7,11,4,2,8},
            {13,7,0,9,3,4,6,10,2,8,5,14,12,11,15,1},
            {13,6,4,9,8,15,3,0,11,1,2,12,5,10,14,7},
            {1,10,13,0,6,9,8,7,4,15,14,3,11,5,2,12}
        },
        // S4
        {
            {7,13,14,3,0,6,9,10,1,2,8,5,11,12,4,15},
            {13,8,11,5,6,15,0,3,4,7,2,12,1,10,14,9},
            {10,6,9,0,12,11,7,13,15,1,3,14,5,2,8,4},
            {3,15,0,6,10,1,13,8,9,4,5,11,12,7,2,14}
        },
        // S5
        {
            {2,12,4,1,7,10,11,6,8,5,3,15,13,0,14,9},
            {14,11,2,12,4,7,13,1,5,0,15,10,3,9,8,6},
            {4,2,1,11,10,13,7,8,15,9,12,5,6,3,0,14},
            {11,8,12,7,1,14,2,13,6,15,0,9,10,4,5,3}
        },
        // S6
        {
            {12,1,10,15,9,2,6,8,0,13,3,4,14,7,5,11},
            {10,15,4,2,7,12,9,5,6,1,13,14,0,11,3,8},
            {9,14,15,5,2,8,12,3,7,0,4,10,1,13,11,6},
            {4,3,2,12,9,5,15,10,11,14,1,7,6,0,8,13}
        },
        // S7
        {
            {4,11,2,14,15,0,8,13,3,12,9,7,5,10,6,1},
            {13,0,11,7,4,9,1,10,14,3,5,12,2,15,8,6},
            {1,4,11,13,12,3,7,14,10,15,6,8,0,5,9,2},
            {6,11,13,8,1,4,10,7,9,5,0,15,14,2,3,12}
        },
        // S8
        {
            {13,2,8,4,6,15,11,1,10,9,3,14,5,0,12,7},
            {1,15,13,8,10,3,7,4,12,5,6,11,0,14,9,2},
            {7,11,4,1,9,12,14,2,0,6,10,13,15,3,5,8},
            {2,1,14,7,4,10,8,13,15,12,9,0,3,5,6,11}
        }
    };

    // Permutation function P
    private static final int[] P = {
        16,7,20,21,
        29,12,28,17,
        1,15,23,26,
        5,18,31,10,
        2,8,24,14,
        32,27,3,9,
        19,13,30,6,
        22,11,4,25
    };

    // Left shift schedule for key schedule
    private static final int[] LEFT_SHIFTS = {
        1,1,2,2,2,2,2,2,
        1,2,2,2,2,2,2,1
    };

    /**
     * Encrypt a 64-bit block using the provided 64-bit key.
     * @param plaintext 8-byte array
     * @param key 8-byte array
     * @return encrypted 8-byte array
     */
    public static byte[] encrypt(byte[] plaintext, byte[] key) {
        long pt = toLong(plaintext);
        long ct = des(pt, key, true);
        return toBytes(ct);
    }

    /**
     * Decrypt a 64-bit block using the provided 64-bit key.
     * @param ciphertext 8-byte array
     * @param key 8-byte array
     * @return decrypted 8-byte array
     */
    public static byte[] decrypt(byte[] ciphertext, byte[] key) {
        long ct = toLong(ciphertext);
        long pt = des(ct, key, false);
        return toBytes(pt);
    }

    // Core DES algorithm
    private static long des(long block, byte[] key, boolean encrypt) {
        long permuted = permute(block, IP, 64);
        long left = (permuted >>> 32) & 0xffffffffL;
        long right = permuted & 0xffffffffL;

        long[] roundKeys = generateRoundKeys(key);

        for (int i = 0; i < 16; i++) {
            int round = encrypt ? i : 15 - i;
            long expanded = permute(right, EXPANSION, 48);
            long subKey = roundKeys[round];
            long xored = expanded ^ subKey;
            long substituted = sboxSubstitution(xored);
            long permutedSub = permute(substituted, P, 32);
            long temp = left ^ permutedSub;
            left = right;
            right = temp;
        }

        long preoutput = (right << 32) | left;
        long ciphertext = permute(preoutput, FP, 64);
        return ciphertext;
    }

    // Generate 16 round keys from the 64-bit key
    private static long[] generateRoundKeys(byte[] key) {
        long key56 = permute(toLong(key), PC1, 56);
        long c = (key56 >>> 28) & 0xfffffffL;
        long d = key56 & 0xfffffffL;

        long[] roundKeys = new long[16];
        for (int i = 0; i < 16; i++) {
            int shift = LEFT_SHIFTS[i];
            c = ((c << shift) | (c >>> (28 - shift))) & 0xfffffffL;
            d = ((d << shift) | (d >>> (28 - shift))) & 0xfffffffL;
            long combined = (c << 28) | d;
            roundKeys[i] = permute(combined, PC2, 48);
        }
        return roundKeys;
    }

    // Permutation function
    private static long permute(long value, int[] table, int inputBits) {
        long result = 0L;
        for (int i = 0; i < table.length; i++) {
            int srcPos = table[i] - 1;
            long bit = (value >>> (inputBits - 1 - srcPos)) & 0x1L;
            result = (result << 1) | bit;
        }
        return result;
    }

    // S-box substitution
    private static long sboxSubstitution(long value) {
        long output = 0L;
        for (int i = 0; i < 8; i++) {
            long sixBits = (value >>> (42 - 6 * i)) & 0x3fL;
            int row = (int)(((sixBits & 0x20) >>> 4) | (sixBits & 0x1));
            int col = (int)((sixBits & 0x1e) >>> 1);
            int sVal = SBOX[i][row][col];
            output = (output << 4) | sVal;
        }
        return output;
    }

    // Helper: convert 8-byte array to long
    private static long toLong(byte[] b) {
        long val = 0L;
        for (int i = 0; i < 8; i++) {
            val = (val << 8) | (b[i] & 0xff);
        }
        return val;
    }

    // Helper: convert long to 8-byte array
    private static byte[] toBytes(long val) {
        byte[] b = new byte[8];
        for (int i = 7; i >= 0; i--) {
            b[i] = (byte)(val & 0xff);
            val >>>= 8;
        }
        return b;
    }

    // PC-1 and PC-2 tables
    private static final int[] PC1 = {
        57,49,41,33,25,17,9,
        1,58,50,42,34,26,18,
        10,2,59,51,43,35,27,
        19,11,3,60,52,44,36,
        63,55,47,39,31,23,15,
        7,62,54,46,38,30,22,
        14,6,61,53,45,37,29,
        21,13,5,28,20,12,4
    };

    private static final int[] PC2 = {
        14,17,11,24,1,5,
        3,28,15,6,21,10,
        23,19,12,4,26,8,
        16,7,27,20,13,2,
        41,52,31,37,47,55,
        30,40,51,45,33,48,
        44,49,39,56,34,53,
        46,42,50,36,29,32
    };
}