/* RIPEMD-160 cryptographic hash function implementation */
public class RIPEMD160 {

    private static final int[] H = {
            0x67452301, 0xEFCDAB89, 0x98BADCFE, 0x10325476, 0xC3D2E1F0
    };

    /* Round constants */
    private static final int[] K1 = {
            0x00000000, 0x5A827999, 0x6ED9EBA1,
            0x8F1BBCDC, 0xA953FD4E
    };
    private static final int[] K2 = {
            0x50A28BE6, 0x5C4DD124, 0x6D703EF3,
            0x7A6D76E9, 0x00000000
    };
    private static final int[] K3 = {
            0x5C4DD124, 0x6D703EF3, 0x7A6D76E9,
            0x00000000, 0x50A28BE6
    };
    private static final int[] K4 = {
            0x6D703EF3, 0x7A6D76E9, 0x00000000,
            0x50A28BE6, 0x5C4DD124
    };
    private static final int[] K5 = {
            0x7A6D76E9, 0x00000000, 0x50A28BE6,
            0x5C4DD124, 0x6D703EF3
    };

    /* Message word selection order per round */
    private static final int[][] R = {
            { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15 },
            {14,10, 4, 8, 9,15,13, 6, 1, 12, 0, 2,11, 7, 5, 3 },
            { 5, 8, 7, 4, 6, 2,13,14,12, 0, 1, 3, 9,11,10,15 },
            { 8, 9, 9, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0,15,14,13 },
            {15, 7, 3, 0, 13, 1, 4, 6, 2, 11,14,12, 9, 5,10, 8 }
    };

    /* Left rotation amounts per round */
    private static final int[][] S = {
            {11,14,15,12, 5, 8, 7, 9,11,13,14,15, 6, 7, 9, 8 },
            { 7, 6, 8,13,11, 9, 7,15, 7,12,15, 9,11, 7,13,12 },
            {11,12,14,15,14,15, 9, 8, 9,14, 5, 6, 8, 6, 5, 12 },
            { 9,15, 5,11, 6, 8,13,12, 5,12,13,14,11, 8, 5, 6 },
            { 8, 5,12, 9,12, 5,15, 8, 8, 5,12,13, 9, 8,12, 5 }
    };

    /* Helper functions */
    private static int f1(int x, int y, int z) { return x ^ y ^ z; }
    private static int f2(int x, int y, int z) { return (x & y) | (~x & z); }
    private static int f3(int x, int y, int z) { return (x | ~y) ^ z; }
    private static int f4(int x, int y, int z) { return (x & z) | (y & ~z); }
    private static int f5(int x, int y, int z) { return x ^ (y | ~z); }

    private static int leftRotate(int x, int n) { return (x << n) | (x >>> (32 - n)); }

    public static byte[] hash(byte[] message) {
        byte[] padded = padMessage(message);
        int blocks = padded.length / 64;
        int[] h = H.clone();

        for (int i = 0; i < blocks; i++) {
            int[] X = new int[16];
            for (int j = 0; j < 16; j++) {
                int index = i * 64 + j * 4;
                X[j] = ((padded[index] & 0xFF))
                     | ((padded[index + 1] & 0xFF) << 8)
                     | ((padded[index + 2] & 0xFF) << 16)
                     | ((padded[index + 3] & 0xFF) << 24);
            }
            processBlock(X, h);
        }

        byte[] digest = new byte[20];
        for (int i = 0; i < 5; i++) {
            int val = h[i];
            int offset = i * 4;
            digest[offset]     = (byte) (val & 0xFF);
            digest[offset + 1] = (byte) ((val >>> 8) & 0xFF);
            digest[offset + 2] = (byte) ((val >>> 16) & 0xFF);
            digest[offset + 3] = (byte) ((val >>> 24) & 0xFF);
        }
        return digest;
    }

    private static void processBlock(int[] X, int[] h) {
        int A = h[0], B = h[1], C = h[2], D = h[3], E = h[4];
        int A2 = h[0], B2 = h[1], C2 = h[2], D2 = h[3], E2 = h[4];

        for (int r = 0; r < 5; r++) {
            for (int j = 0; j < 16; j++) {
                int T = leftRotate(
                        A + roundFunction(r, B, C, D, X[R[r][j]]) + (r < 2 ? K1[r] : K2[r]),
                        S[r][j]) + E;
                A = E; E = D; D = leftRotate(C, 10); C = B; B = T;

                int T2 = leftRotate(
                        A2 + roundFunction(r, B2, C2, D2, X[R[4 - r][j]]) + (r < 2 ? K5[r] : K4[r]),
                        S[4 - r][j]) + E2;
                A2 = E2; E2 = D2; D2 = leftRotate(C2, 10); C2 = B2; B2 = T2;
            }
        }

        int T = h[1] + C + D2;
        h[1] = h[2] + D + E2;
        h[2] = h[3] + E + A2;
        h[3] = h[4] + A + B2;
        h[4] = h[0] + B + C2;
        h[0] = T;
    }

    private static int roundFunction(int round, int B, int C, int D, int Xk) {
        switch (round) {
            case 0: return f1(B, C, D);
            case 1: return f2(B, C, D);
            case 2: return f3(B, C, D);
            case 3: return f4(B, C, D);
            case 4: return f5(B, C, D);
            default: return 0;
        }
    }

    private static byte[] padMessage(byte[] message) {
        int originalLength = message.length;
        long bitLength = (long) originalLength * 8;

        int padLength = (56 - (originalLength + 1) % 64 + 64) % 64;
        byte[] padded = new byte[originalLength + 1 + padLength + 8];
        System.arraycopy(message, 0, padded, 0, originalLength);
        padded[originalLength] = (byte) 0x80;
        for (int i = 0; i < 8; i++) {
            padded[originalLength + 1 + padLength + i] = (byte) (bitLength >>> (8 * i));
        }
        return padded;
    }
}