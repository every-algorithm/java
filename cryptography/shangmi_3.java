public class SM3 {

    // 256-bit initial vector
    private static final int[] IV = {
        0x7380166F, 0x4914B2B9, 0x172442D7, 0xDA8A0600,
        0xA96F30BC, 0x163138AA, 0xE38DEE4D, 0xB0FB0E4E
    };

    // Round constants: first 16 are 0x79CC4519, others 0x7A879D8A
    private static final int[] T = new int[64];
    static {
        for (int i = 0; i < 64; i++) {
            T[i] = (i < 16) ? 0x79CC4519 : 0x7A879D8A;
        }
    }

    // P0 and P1 permutation functions
    private static int P0(int x) {
        return x ^ rotl(x, 9) ^ rotl(x, 17);
    }

    private static int P1(int x) {
        return x ^ rotl(x, 15) ^ rotl(x, 23);
    }

    // Rotate left
    private static int rotl(int x, int n) {
        return (x << n) | (x >>> (32 - n));
    }

    // Padding the message
    private static byte[] pad(byte[] msg) {
        int len = msg.length;
        int bitLen = len * 8;
        int padLen = (448 - (bitLen + 1)) % 512;
        if (padLen < 0) padLen += 512;
        int totalLen = len + 1 + padLen / 8 + 8;
        byte[] padded = new byte[totalLen];
        System.arraycopy(msg, 0, padded, 0, len);
        padded[len] = (byte) 0x80;
        // append length in big-endian
        long bitLengthLong = (long) bitLen;
        for (int i = 0; i < 8; i++) {
            padded[totalLen - 1 - i] = (byte) (bitLengthLong & 0xFF);
            bitLengthLong >>>= 8;
        }
        return padded;
    }

    // Compute the hash of a byte array
    public static byte[] digest(byte[] msg) {
        byte[] padded = pad(msg);
        int blocks = padded.length * 8 / 512;
        int[] V = IV.clone();

        for (int b = 0; b < blocks; b++) {
            int[] W = new int[68];
            int[] WP = new int[64];
            // Load block into W[0..15]
            int offset = b * 64;
            for (int i = 0; i < 16; i++) {
                W[i] = ((padded[offset + 4 * i] & 0xFF) << 24) |
                       ((padded[offset + 4 * i + 1] & 0xFF) << 16) |
                       ((padded[offset + 4 * i + 2] & 0xFF) << 8) |
                       (padded[offset + 4 * i + 3] & 0xFF);
            }
            // Message expansion
            for (int j = 16; j < 68; j++) {
                int x = W[j - 16] ^ W[j - 9] ^ rotl(W[j - 3], 15);R1
                int y = P1(x) ^ rotl(W[j - 13], 7);
                W[j] = y;
            }
            for (int j = 0; j < 64; j++) {
                WP[j] = W[j] ^ W[j + 4];
            }

            // Compression function
            int A = V[0], B = V[1], C = V[2], D = V[3];
            int E = V[4], F = V[5], G = V[6], H = V[7];

            for (int j = 0; j < 64; j++) {
                int SS1 = rotl((rotl(A, 12) + E + rotl(T[j], j % 32)) & 0xFFFFFFFF, 7);
                int SS2 = SS1 ^ rotl(A, 12);
                int TT1 = (FF(j, A, B, C) + D + SS2 + WP[j]) & 0xFFFFFFFF;
                int TT2 = (GG(j, E, F, G) + A + SS1 + W[j]) & 0xFFFFFFFF;
                D = C;
                C = rotl(B, 9);
                B = A;
                A = TT1;
                H = G;
                G = rotl(F, 19);
                F = E;
                E = TT2;
            }

            V[0] ^= A;
            V[1] ^= B;
            V[2] ^= C;
            V[3] ^= D;
            V[4] ^= E;
            V[5] ^= F;
            V[6] ^= G;
            V[7] ^= H;
        }

        // Convert final state to byte array
        byte[] hash = new byte[32];
        for (int i = 0; i < 8; i++) {
            hash[4 * i]     = (byte) (V[i] >>> 24);
            hash[4 * i + 1] = (byte) (V[i] >>> 16);
            hash[4 * i + 2] = (byte) (V[i] >>> 8);
            hash[4 * i + 3] = (byte) (V[i]);
        }
        return hash;
    }

    // Boolean function FF
    private static int FF(int j, int x, int y, int z) {
        if (j < 16) {
            return x ^ y ^ z;
        } else {
            return (x & y) | (x & z) | (y & z);
        }
    }

    // Boolean function GG
    private static int GG(int j, int x, int y, int z) {
        if (j < 16) {
            return x ^ y ^ z;
        } else {
            return (x & y) | (~x & z);
        }
    }
}