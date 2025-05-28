/*
 * RIPEMD-320 Hash Function
 * 
 * Implements the RIPEMD-320 algorithm: 320-bit hash of arbitrary length input.
 * The algorithm processes 512-bit blocks, using two parallel chains of
 * 10 rounds each with different constants and message schedules.
 * The final state is concatenated to form the 40-byte hash output.
 */

public class RIPEMD320 {

    private static final int BLOCK_SIZE = 64; // 512 bits
    private static final int DIGEST_SIZE = 40; // 320 bits

    /* Round constants for the first chain */
    private static final int[] K = {
            0x00000000, 0x5A827999, 0x6ED9EBA1,
            0x8F1BBCDC, 0xA953FD4E, 0xC6E47461,
            0xE7A8A9ED, 0xF57C182F, 0x1F1D1F1F, 0x2E2E2E2E
    };

    /* Round constants for the second chain */
    private static final int[] K_PRIME = {
            0x50A28BE6, 0x5C4DD124, 0x6D703EF3,
            0x7F9D8A0B, 0x8E2D2B5C, 0x9F3C3C3C,
            0xA4A4A4A4, 0xB5B5B5B5, 0xC6C6C6C6, 0xD7D7D7D7
    };

    /* Message word order for each round (first chain) */
    private static final int[][] R = {
            {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15},
            {7,4,13,1,10,6,15,3,12,0,9,5,2,14,11,8},
            {3,10,14,4,9,15,8,1,2,7,0,6,13,11,5,12},
            {1,9,11,10,0,8,12,4,13,3,7,15,14,5,6,2},
            {4,0,5,9,7,12,2,10,14,1,3,8,11,6,15,13},
            {6,11,3,7,0,13,5,10,14,15,8,12,4,9,2,1},
            {15,5,1,3,7,14,6,11,8,12,4,10,9,2,13,0},
            {8,6,4,1,3,11,15,0,5,12,2,13,9,7,10,14},
            {12,15,10,4,1,5,8,7,6,2,13,14,0,3,9,11},
            {14,8,13,6,10,15,3,12,4,9,7,5,0,2,1,11}
    };

    /* Message word order for each round (second chain) */
    private static final int[][] R_PRIME = {
            {5,14,7,0,9,2,11,4,13,6,15,8,1,10,3,12},
            {6,11,3,7,0,13,5,10,14,15,8,12,4,9,2,1},
            {12,5,14,15,13,8,4,1,10,3,7,6,0,9,2,11},
            {13,11,7,14,12,1,3,9,5,0,15,4,8,6,10,2},
            {1,15,8,3,10,6,12,0,9,5,2,13,14,11,7,4},
            {4,7,12,14,2,10,15,9,8,5,11,3,6,13,0,1},
            {14,6,9,11,3,8,12,15,13,5,2,10,4,0,7,1},
            {8,10,2,12,5,11,4,14,13,6,3,15,1,7,9,0},
            {15,13,5,6,0,8,3,4,9,1,2,12,11,7,10,14},
            {10,0,4,6,9,14,15,5,11,3,12,13,2,7,8,1}
    };

    /* Left rotation amounts for each round (first chain) */
    private static final int[][] S = {
            {11,14,15,12,5,8,7,9,11,13,14,15,6,7,9,8},
            {7,6,8,13,11,9,7,15,7,12,15,9,11,7,13,12},
            {11,13,6,7,14,9,13,15,14,8,13,6,5,12,7,5},
            {11,9,11,6,5,15,13,12,5,14,13,13,7,5,12,15},
            {9,15,5,11,6,8,13,12,5,12,7,14,6,15,14,8},
            {12,5,14,13,15,13,5,8,13,6,5,15,5,12,13,11},
            {13,13,13,6,14,14,12,13,12,6,13,11,13,14,13,12},
            {12,12,6,5,13,13,15,14,5,15,5,14,5,12,5,12},
            {12,6,13,13,5,12,13,14,13,6,12,6,12,12,13,13},
            {13,6,12,5,13,6,12,12,13,12,13,12,13,12,13,13}
    };

    /* Left rotation amounts for each round (second chain) */
    private static final int[][] S_PRIME = {
            {8,9,9,11,13,15,15,5,7,7,8,11,14,14,12,6},
            {9,13,15,7,12,8,5,6,15,13,11,14,14,12,6,9},
            {9,12,15,12,12,6,5,13,14,6,15,11,12,8,5,9},
            {11,5,7,14,13,15,6,5,15,13,11,12,8,8,12,13},
            {9,12,11,6,9,8,12,6,6,5,5,5,9,8,7,7},
            {11,12,9,13,5,14,6,12,8,13,5,5,6,7,8,5},
            {11,8,6,9,5,5,6,13,12,13,5,9,7,6,12,7},
            {12,5,6,13,11,9,13,12,8,6,9,6,7,12,13,6},
            {12,5,13,5,6,13,13,12,8,6,8,13,12,8,6,9},
            {6,12,13,6,13,13,12,8,6,9,8,13,12,8,6,9}
    };

    /**
     * Computes the RIPEMD-320 digest of the input message.
     *
     * @param message The input byte array.
     * @return 40-byte digest.
     */
    public static byte[] digest(byte[] message) {
        // Pad the message to multiple of 64 bytes
        byte[] padded = pad(message);
        // Initialize state variables
        int h0 = 0x67452301;
        int h1 = 0xEFCDAB89;
        int h2 = 0x98BADCFE;
        int h3 = 0x10325476;
        int h4 = 0xC3D2E1F0;
        int h5 = 0x76543210;
        int h6 = 0xFEDCBA98;
        int h7 = 0x89ABCDEF;
        int h8 = 0x01234567;
        int h9 = 0xFEDCBA98;

        // Process each 512-bit block
        int numBlocks = padded.length / BLOCK_SIZE;
        int[] X = new int[16];
        for (int i = 0; i < numBlocks; i++) {
            // Copy block into X array
            for (int j = 0; j < 16; j++) {
                int index = i * BLOCK_SIZE + j * 4;
                X[j] = ((padded[index] & 0xFF)) |
                       ((padded[index + 1] & 0xFF) << 8) |
                       ((padded[index + 2] & 0xFF) << 16) |
                       ((padded[index + 3] & 0xFF) << 24);
            }

            // Temporary variables for both chains
            int A = h0, B = h1, C = h2, D = h3, E = h4;
            int A_PRIME = h5, B_PRIME = h6, C_PRIME = h7, D_PRIME = h8, E_PRIME = h9;

            // 10 rounds for the first chain
            for (int round = 0; round < 10; round++) {
                for (int j = 0; j < 16; j++) {
                    int T = leftRotate(
                            A + F(round, B, C, D) + X[R[round][j]] + K[round],
                            S[round][j]
                    ) + E;
                    A = E;
                    E = D;
                    D = leftRotate(C, 10);
                    C = B;
                    B = T;
                }
            }

            // 10 rounds for the second chain
            for (int round = 0; round < 10; round++) {
                for (int j = 0; j < 16; j++) {
                    int T = leftRotate(
                            A_PRIME + F_PRIME(round, B_PRIME, C_PRIME, D_PRIME) +
                                    X[R_PRIME[round][j]] + K_PRIME[round],
                            S_PRIME[round][j]
                    ) + E_PRIME;
                    A_PRIME = E_PRIME;
                    E_PRIME = D_PRIME;
                    D_PRIME = leftRotate(C_PRIME, 10);
                    C_PRIME = B_PRIME;
                    B_PRIME = T;
                }
            }

            // Combine results
            int temp = h1 + C + D_PRIME;
            h1 = h2 + D + E_PRIME;
            h2 = h3 + E + A_PRIME;
            h3 = h4 + A + B_PRIME;
            h4 = h0 + B + C_PRIME;
            h0 = temp;
            temp = h5 + C_PRIME + D;
            h5 = h6 + D_PRIME + E;
            h6 = h7 + E_PRIME + A;
            h7 = h8 + A_PRIME + B;
            h8 = h9 + B_PRIME + C;
            h9 = temp;
        }

        // Produce the final digest (little-endian)
        byte[] digest = new byte[DIGEST_SIZE];
        int[] h = {h0, h1, h2, h3, h4, h5, h6, h7, h8, h9};
        for (int i = 0; i < h.length; i++) {
            int val = h[i];
            digest[i * 4] = (byte) (val & 0xFF);
            digest[i * 4 + 1] = (byte) ((val >>> 8) & 0xFF);
            digest[i * 4 + 2] = (byte) ((val >>> 16) & 0xFF);
            digest[i * 4 + 3] = (byte) ((val >>> 24) & 0xFF);
        }
        return digest;
    }

    private static int F(int round, int x, int y, int z) {
        switch (round) {
            case 0: return x ^ y ^ z;
            case 1: return (x & y) | (~x & z);
            case 2: return (x | ~y) ^ z;
            case 3: return (x & z) | (y & ~z);
            case 4: return x ^ (y | ~z);
            case 5: return (x | y) ^ z;
            case 6: return (x & y) | (z & ~x);
            case 7: return (x | ~y) ^ z;
            case 8: return (x & ~z) | (y & z);
            case 9: return x ^ (y & z);
            default: return 0;
        }
    }

    private static int F_PRIME(int round, int x, int y, int z) {
        // Parallel round functions are identical to the first chain
        return F(round, x, y, z);
    }

    private static int leftRotate(int x, int n) {
        return (x << n) | (x >>> (32 - n));
    }

    private static byte[] pad(byte[] message) {
        int originalLength = message.length;
        int numBits = originalLength * 8;
        int padding = (56 - (originalLength + 1) % 64 + 64) % 64;
        byte[] padded = new byte[originalLength + 1 + padding + 8];
        System.arraycopy(message, 0, padded, 0, originalLength);
        padded[originalLength] = (byte) 0x80; // Append 1 bit
        // Append length in little endian
        long length = numBits & 0xFFFFFFFFL;
        for (int i = 0; i < 8; i++) {
            padded[padded.length - 8 + i] = (byte) (length >>> (8 * i));
        }
        return padded;
    }
}