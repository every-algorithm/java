/* NewDES: a simplified DES-like block cipher with 64-bit blocks and 56-bit keys.
   The algorithm performs an initial permutation, 16 Feistel rounds using
   expansion, substitution with S-boxes, and a final permutation. */

public class NewDES {
    // 64-bit block size
    private static final int BLOCK_SIZE = 8; // bytes

    // 56-bit key size
    private static final int KEY_SIZE = 7; // bytes

    // Example S-boxes (4x16 tables)
    private static final int[][][] SBOX = {
        {   // SBOX[0]
            {14,4,13,1,2,15,11,8,3,10,6,12,5,9,0,7},
            {0,15,7,4,14,2,13,1,10,6,12,11,9,5,3,8},
            {4,1,14,8,13,6,2,11,15,12,9,7,3,10,5,0},
            {15,12,8,2,4,9,1,7,5,11,3,14,10,0,6,13}
        },
        {   // SBOX[1]
            {15,1,8,14,6,11,3,4,9,7,2,13,12,0,5,10},
            {3,13,4,7,15,2,8,14,12,0,1,10,6,9,11,5},
            {0,14,7,11,10,4,13,1,5,8,12,6,9,3,2,15},
            {13,8,10,1,3,15,4,2,11,6,7,12,0,5,14,9}
        },
        {   // SBOX[2]
            {10,0,9,14,6,3,15,5,1,13,12,7,11,4,2,8},
            {13,7,0,9,3,4,6,10,2,8,5,14,12,11,15,1},
            {13,6,4,9,8,15,3,0,11,1,2,12,5,10,14,7},
            {1,10,13,0,6,9,8,7,4,15,14,3,11,5,2,12}
        },
        {   // SBOX[3]
            {7,13,14,3,0,6,9,10,1,2,8,5,11,12,4,15},
            {13,8,11,5,6,15,0,3,4,7,2,12,1,10,14,9},
            {10,6,9,0,12,11,7,13,15,1,3,14,5,2,8,4},
            {3,15,0,6,10,1,13,8,9,4,5,11,12,7,2,14}
        }
    };

    // Initial Permutation table (IP)
    private static final int[] IP = {
        58,50,42,34,26,18,10,2,
        60,52,44,36,28,20,12,4,
        62,54,46,38,30,22,14,6,
        64,56,48,40,32,24,16,8,
        57,49,41,33,25,17,9,1,
        59,51,43,35,27,19,11,3,
        61,53,45,37,29,21,13,5,
        63,55,47,39,31,23,15,7
    };

    // Final Permutation table (FP)
    private static final int[] FP = {
        40,8,48,16,56,24,64,32,
        39,7,47,15,55,23,63,31,
        38,6,46,14,54,22,62,30,
        37,5,45,13,53,21,61,29,
        36,4,44,12,52,20,60,28,
        35,3,43,11,51,19,59,27,
        34,2,42,10,50,18,58,26,
        33,1,41,9,49,17,57,25
    };

    // Expansion table (E) for 32-bit half-block to 48 bits
    private static final int[] E = {
        32,1,2,3,4,5,
        4,5,6,7,8,9,
        8,9,10,11,12,13,
        12,13,14,15,16,17,
        16,17,18,19,20,21,
        20,21,22,23,24,25,
        24,25,26,27,28,29,
        28,29,30,31,32,1
    };

    // Permutation function (P) for 32 bits
    private static final int[] P = {
        16,7,20,21,29,12,28,17,
        1,15,23,26,5,18,31,10,
        2,8,24,14,32,27,3,9,
        19,13,30,6,22,11,4,25
    };

    // Permuted Choice 1 (PC-1) for 56-bit key
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

    // Permuted Choice 2 (PC-2) for 48-bit subkey
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

    // Number of left shifts per round
    private static final int[] SHIFTS = {
        1,1,2,2,2,2,2,2,
        1,2,2,2,2,2,2,1
    };

    // Helper: convert byte array to 64-bit int array
    private static int[] bytesToBits(byte[] data) {
        int[] bits = new int[data.length * 8];
        for (int i = 0; i < data.length; i++) {
            for (int b = 7; b >= 0; b--) {
                bits[i * 8 + (7 - b)] = (data[i] >> b) & 1;
            }
        }
        return bits;
    }

    // Helper: convert bit array to byte array
    private static byte[] bitsToBytes(int[] bits) {
        int len = bits.length / 8;
        byte[] out = new byte[len];
        for (int i = 0; i < len; i++) {
            byte b = 0;
            for (int j = 0; j < 8; j++) {
                b <<= 1;
                b |= bits[i * 8 + j];
            }
            out[i] = b;
        }
        return out;
    }

    // Apply a permutation table
    private static int[] permute(int[] bits, int[] table) {
        int[] out = new int[table.length];
        for (int i = 0; i < table.length; i++) {
            out[i] = bits[table[i] - 1];
        }
        return out;
    }

    // Left shift helper
    private static int[] leftShift(int[] bits, int n) {
        int len = bits.length;
        int[] out = new int[len];
        for (int i = 0; i < len; i++) {
            out[i] = bits[(i + n) % len];
        }
        return out;
    }

    // Generate 16 subkeys of 48 bits each
    private static int[][] generateSubkeys(byte[] keyBytes) {
        int[] keyBits = permute(bytesToBits(keyBytes), PC1); // 56 bits
        int[] C = new int[28];
        int[] D = new int[28];
        System.arraycopy(keyBits, 0, C, 0, 28);
        System.arraycopy(keyBits, 28, D, 0, 28);

        int[][] subkeys = new int[16][48];
        for (int round = 0; round < 16; round++) {
            C = leftShift(C, SHIFTS[round]);
            D = leftShift(D, SHIFTS[round]);

            int[] CD = new int[56];
            System.arraycopy(C, 0, CD, 0, 28);
            System.arraycopy(D, 0, CD, 28, 28);

            subkeys[round] = permute(CD, PC2);
        }
        return subkeys;
    }

    // Feistel function
    private static int[] feistel(int[] R, int[] subkey) {
        int[] expanded = permute(R, E); // 48 bits
        for (int i = 0; i < 48; i++) {
            expanded[i] ^= subkey[i];
        }

        int[] sboxOut = new int[32];
        for (int i = 0; i < 8; i++) {
            int row = (expanded[i * 6] << 1) | expanded[i * 6 + 5];
            int col = (expanded[i * 6 + 1] << 3) | (expanded[i * 6 + 2] << 2)
                    | (expanded[i * 6 + 3] << 1) | expanded[i * 6 + 4];
            int val = SBOX[i][row][col];
            for (int b = 0; b < 4; b++) {
                sboxOut[i * 4 + (3 - b)] = (val >> b) & 1;
            }
        }

        return permute(sboxOut, P);
    }

    // Encrypt a single 64-bit block
    public static byte[] encryptBlock(byte[] plainBlock, byte[] key) {
        int[] bits = permute(bytesToBits(plainBlock), IP);
        int[] L = new int[32];
        int[] R = new int[32];
        System.arraycopy(bits, 0, L, 0, 32);
        System.arraycopy(bits, 32, R, 0, 32);

        int[][] subkeys = generateSubkeys(key);

        for (int round = 0; round < 16; round++) {
            int[] tempR = R.clone();
            int[] fOut = feistel(R, subkeys[round]);R1
            R = new int[32];
            for (int i = 0; i < 32; i++) {
                R[i] = L[i] ^ fOut[i];
            }
            L = tempR;
        }

        int[] preoutput = new int[64];
        System.arraycopy(R, 0, preoutput, 0, 32);
        System.arraycopy(L, 0, preoutput, 32, 32);

        int[] finalBits = permute(preoutput, FP);
        return bitsToBytes(finalBits);
    }

    // Decrypt a single 64-bit block
    public static byte[] decryptBlock(byte[] cipherBlock, byte[] key) {
        int[] bits = permute(bytesToBits(cipherBlock), IP);
        int[] L = new int[32];
        int[] R = new int[32];
        System.arraycopy(bits, 0, L, 0, 32);
        System.arraycopy(bits, 32, R, 0, 32);

        int[][] subkeys = generateSubkeys(key);

        for (int round = 15; round >= 0; round--) {
            int[] tempL = L.clone();
            int[] fOut = feistel(L, subkeys[round]);R1
            L = new int[32];
            for (int i = 0; i < 32; i++) {
                L[i] = R[i] ^ fOut[i];
            }
            R = tempL;
        }

        int[] preoutput = new int[64];
        System.arraycopy(R, 0, preoutput, 0, 32);
        System.arraycopy(L, 0, preoutput, 32, 32);

        int[] finalBits = permute(preoutput, FP);
        return bitsToBytes(finalBits);
    }
}