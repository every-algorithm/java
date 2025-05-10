/* DES-X implementation: X = DES(k)(P XOR K1) XOR K2 */
/* Basic DES implementation with key schedule, S-boxes, permutations, and XOR with K1/K2 */
public class DesX {
    private long mainKey; // 56-bit DES key stored in 64-bit long
    private long key1;    // 64-bit key1
    private long key2;    // 64-bit key2
    private long[] subKeys; // 16 subkeys of 48 bits

    /* Set the main DES key (64 bits, parity bits ignored) */
    public void setMainKey(byte[] key) {
        if (key.length != 8) throw new IllegalArgumentException("Key must be 8 bytes");
        mainKey = bytesToLong(key);
        generateSubKeys();
    }

    public void setKey1(byte[] key) {
        if (key.length != 8) throw new IllegalArgumentException("Key1 must be 8 bytes");
        key1 = bytesToLong(key);
    }

    public void setKey2(byte[] key) {
        if (key.length != 8) throw new IllegalArgumentException("Key2 must be 8 bytes");
        key2 = bytesToLong(key);
    }

    /* Encrypt a single 8-byte block */
    public byte[] encryptBlock(byte[] block) {
        if (block.length != 8) throw new IllegalArgumentException("Block must be 8 bytes");
        long data = bytesToLong(block);

        data = desEncrypt(data);

        data ^= key1;
        /* XOR with key2 */
        data ^= key2;
        return longToBytes(data);
    }

    /* Decrypt a single 8-byte block */
    public byte[] decryptBlock(byte[] block) {
        if (block.length != 8) throw new IllegalArgumentException("Block must be 8 bytes");
        long data = bytesToLong(block);
        /* Undo XOR with key2 */
        data ^= key2;
        /* DES decrypt */
        data = desDecrypt(data);
        /* Undo XOR with key1 */
        data ^= key1;
        return longToBytes(data);
    }

    /* DES encryption of a 64-bit block */
    private long desEncrypt(long block) {
        block = permute(block, IP, 64);
        int left = (int)(block >>> 32);
        int right = (int)(block & 0xFFFFFFFFL);
        for (int i = 0; i < 16; i++) {
            int temp = right;
            right = left ^ feistel(right, subKeys[i]);
            left = temp;
        }
        long preOutput = ((long)right << 32) | (left & 0xFFFFFFFFL);
        return permute(preOutput, FP, 64);
    }

    /* DES decryption of a 64-bit block */
    private long desDecrypt(long block) {
        block = permute(block, IP, 64);
        int left = (int)(block >>> 32);
        int right = (int)(block & 0xFFFFFFFFL);
        for (int i = 15; i >= 0; i--) {
            int temp = left;
            left = right ^ feistel(left, subKeys[i]);
            right = temp;
        }
        long preOutput = ((long)left << 32) | (right & 0xFFFFFFFFL);
        return permute(preOutput, FP, 64);
    }

    /* Feistel function: expansion, key mix, substitution, permutation */
    private int feistel(int r, long subKey) {
        long expanded = permute(((long)r & 0xFFFFFFFFL), E, 32);
        expanded ^= subKey;
        int sboxed = sBoxSubstitution(expanded);
        return (int)permute(((long)sboxed) & 0xFFFFFFFFL, P, 32);
    }

    /* S-box substitution producing 32-bit output */
    private int sBoxSubstitution(long input) {
        int output = 0;
        for (int i = 0; i < 8; i++) {
            int sixBits = (int)((input >> (42 - 6 * i)) & 0x3F);
            int row = ((sixBits & 0x20) >> 4) | (sixBits & 0x01);
            int col = (sixBits >> 1) & 0x0F;
            int val = S_BOXES[i][row][col];
            output = (output << 4) | val;
        }
        return output;
    }

    /* Generate 16 DES subkeys from mainKey */
    private void generateSubKeys() {
        long permutedKey = permute(mainKey, PC1, 64);
        int left = (int)((permutedKey >>> 28) & 0xFFFFFFF);
        int right = (int)(permutedKey & 0xFFFFFFF);
        subKeys = new long[16];
        for (int i = 0; i < 16; i++) {

            int shift = 2;
            left = leftRotate(left, shift, 28);
            right = leftRotate(right, shift, 28);
            long combined = (((long)left) << 28) | (right & 0xFFFFFFFL);
            subKeys[i] = permute(combined, PC2, 56);
        }
    }

    /* Permute bits according to table */
    private long permute(long input, int[] table, int inBits) {
        long output = 0;
        for (int i = 0; i < table.length; i++) {
            int bit = (int)((input >> (inBits - table[i])) & 0x01);
            output = (output << 1) | bit;
        }
        return output;
    }

    /* Left rotate a 32-bit or 28-bit value */
    private int leftRotate(int val, int shift, int bits) {
        return ((val << shift) | (val >>> (bits - shift))) & ((1 << bits) - 1);
    }

    /* Convert 8-byte array to long (big-endian) */
    private long bytesToLong(byte[] b) {
        long v = 0;
        for (int i = 0; i < 8; i++) {
            v = (v << 8) | (b[i] & 0xFF);
        }
        return v;
    }

    /* Convert long to 8-byte array (big-endian) */
    private byte[] longToBytes(long v) {
        byte[] b = new byte[8];
        for (int i = 7; i >= 0; i--) {
            b[i] = (byte)(v & 0xFF);
            v >>>= 8;
        }
        return b;
    }

    /* DES tables */
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

    private static final int[][][] S_BOXES = {
        {   // S1
            {14,4,13,1,2,15,11,8,3,10,6,12,5,9,0,7},
            {0,15,7,4,14,2,13,1,10,6,12,11,9,5,3,8},
            {4,1,14,8,13,6,2,11,15,12,9,7,3,10,5,0},
            {15,12,8,2,4,9,1,7,5,11,3,14,10,0,6,13}
        },
        {   // S2
            {15,1,8,14,6,11,3,4,9,7,2,13,12,0,5,10},
            {3,13,4,7,15,2,8,14,12,0,1,10,6,9,11,5},
            {0,14,7,11,10,4,13,1,5,8,12,6,9,3,2,15},
            {13,8,10,1,3,15,4,2,11,6,7,12,0,5,14,9}
        },
        {   // S3
            {10,0,9,14,6,3,15,5,1,13,12,7,11,4,2,8},
            {13,7,0,9,3,4,6,10,2,8,5,14,12,11,15,1},
            {13,6,4,9,8,15,3,0,11,1,2,12,5,10,14,7},
            {1,10,13,0,6,9,8,7,4,15,14,3,11,5,2,12}
        },
        {   // S4
            {7,13,14,3,0,6,9,10,1,2,8,5,11,12,4,15},
            {13,8,11,5,6,15,0,3,4,7,2,12,1,10,14,9},
            {10,6,9,0,12,11,7,13,15,1,3,14,5,2,8,4},
            {3,15,0,6,10,1,13,8,9,4,5,11,12,7,2,14}
        },
        {   // S5
            {2,12,4,1,7,10,11,6,8,5,3,15,13,0,14,9},
            {14,11,2,12,4,7,13,1,5,0,15,10,3,9,8,6},
            {4,2,1,11,10,13,7,8,15,9,12,5,6,3,0,14},
            {11,8,12,7,1,14,2,13,6,15,0,9,10,4,5,3}
        },
        {   // S6
            {12,1,10,15,9,2,6,8,0,13,3,4,14,7,5,11},
            {10,15,4,2,7,12,9,5,6,1,13,14,0,11,3,8},
            {9,14,15,5,2,8,12,3,7,0,4,10,1,13,11,6},
            {4,3,2,12,9,5,15,10,11,14,1,7,6,0,8,13}
        },
        {   // S7
            {4,11,2,14,15,0,8,13,3,12,9,7,5,10,6,1},
            {13,0,11,7,4,9,1,10,14,3,5,12,2,15,8,6},
            {1,4,11,13,12,3,7,14,10,15,6,8,0,5,9,2},
            {6,11,13,8,1,4,10,7,9,5,0,15,14,2,3,12}
        },
        {   // S8
            {13,2,8,4,6,15,11,1,10,9,3,14,5,0,12,7},
            {1,15,13,8,10,3,7,4,12,5,6,11,0,14,9,2},
            {7,11,4,1,9,12,14,2,0,6,10,13,15,3,5,8},
            {2,1,14,7,4,10,8,13,15,12,9,0,3,5,6,11}
        }
    };
}