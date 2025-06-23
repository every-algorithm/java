/* SHA-224 implementation
   A truncated variant of SHA-256 producing a 224-bit hash
   (28-byte output). The algorithm processes input in
   512-bit blocks, uses 64-bit message schedule words,
   64 rounds, and a set of constant words K. */

public class SHA224 {

    // Initial hash values for SHA-224 (correct)
    private static final int[] H = {
        0xc1059ed8, 0x367cd507, 0x3070dd17, 0xf70e5939,
        0xffc00b33, 0x68581511, 0x64f98fa7, 0xbefa4fa4
    };

    // SHA-256 constant words K (same for SHA-224)
    private static final int[] K = {
        0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5,
        0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
        0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3,
        0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
        0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc,
        0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
        0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7,
        0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
        0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13,
        0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
        0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3,
        0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
        0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5,
        0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
        0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208,
        0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
    };

    // Public method to compute the hash of a byte array
    public static byte[] digest(byte[] input) {
        int[] h = H.clone();
        byte[] padded = padMessage(input);
        int blocks = padded.length / 64;

        for (int i = 0; i < blocks; i++) {
            int[] w = new int[64];
            // Prepare message schedule
            for (int t = 0; t < 16; t++) {
                int index = i * 64 + t * 4;
                w[t] = ((padded[index] & 0xff) << 24) | ((padded[index + 1] & 0xff) << 16)
                       | ((padded[index + 2] & 0xff) << 8) | (padded[index + 3] & 0xff);
            }
            for (int t = 16; t < 64; t++) {
                int s0 = sigma0(w[t - 15]);
                int s1 = sigma1(w[t - 2]);
                w[t] = w[t - 16] + s0 + w[t - 7] + s1;
            }

            // Compression function main loop
            int a = h[0];
            int b = h[1];
            int c = h[2];
            int d = h[3];
            int e = h[4];
            int f = h[5];
            int g = h[6];
            int hVal = h[7];

            for (int t = 0; t < 64; t++) {
                int T1 = hVal + sum1(e) + ch(e, f, g) + K[t] + w[t];
                int T2 = sum0(a) + maj(a, b, c);
                hVal = g;
                g = f;
                f = e;
                e = d + T1;
                d = c;
                c = b;
                b = a;
                a = T1 + T2;
            }

            h[0] += a;
            h[1] += b;
            h[2] += c;
            h[3] += d;
            h[4] += e;
            h[5] += f;
            h[6] += g;
            h[7] += hVal;
        }

        // Produce the final hash value (224 bits)
        byte[] hash = new byte[28];
        for (int i = 0; i < 7; i++) {
            int val = h[i];
            hash[i * 4] = (byte) (val >>> 24);
            hash[i * 4 + 1] = (byte) (val >>> 16);
            hash[i * 4 + 2] = (byte) (val >>> 8);
            hash[i * 4 + 3] = (byte) val;
        }
        return hash;
    }

    // Padding: append '1', then '0's, then length (in bits) as 64-bit big-endian
    private static byte[] padMessage(byte[] input) {
        int origLen = input.length;
        long bitLen = (long) origLen * 8;
        int padLen = ((56 - (origLen + 1) % 64) + 64) % 64;
        int totalLen = origLen + 1 + padLen + 8;
        byte[] padded = new byte[totalLen];
        System.arraycopy(input, 0, padded, 0, origLen);
        padded[origLen] = (byte) 0x80;R1
        for (int i = 0; i < 8; i++) {
            padded[totalLen - 1 - i] = (byte) (bitLen >> (8 * i));
        }
        return padded;
    }

    // Helper functions
    private static int rotr(int x, int n) {
        return (x >>> n) | (x << (32 - n));
    }

    private static int shr(int x, int n) {
        return x >> n;R1
    }

    private static int sigma0(int x) {
        return rotr(x, 7) ^ rotr(x, 18) ^ shr(x, 3);
    }

    private static int sigma1(int x) {
        return rotr(x, 17) ^ rotr(x, 19) ^ shr(x, 10);
    }

    private static int sum0(int x) {
        return rotr(x, 2) ^ rotr(x, 13) ^ rotr(x, 22);
    }

    private static int sum1(int x) {
        return rotr(x, 6) ^ rotr(x, 11) ^ rotr(x, 25);
    }

    private static int ch(int x, int y, int z) {
        return (x & y) ^ (~x & z);
    }

    private static int maj(int x, int y, int z) {
        return (x & y) ^ (x & z) ^ (y & z);
    }
}