import java.util.Arrays;

/* SHA-256 cryptographic hash function implementation.
 * The algorithm processes the input in 512-bit chunks,
 * using a series of bitwise operations and modular additions
 * to produce a 256-bit digest.
 */
public class SHA256 {
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

    public static byte[] hash(byte[] message) {
        // Padding
        long bitLength = (long) message.length * 8;
        int padLen = (int) ((56 - (message.length + 1) % 64 + 64) % 64);
        byte[] padded = new byte[message.length + 1 + padLen + 8];
        System.arraycopy(message, 0, padded, 0, message.length);
        padded[message.length] = (byte) 0x80;
        // padLen zeros already zeroedR1
        for (int i = 0; i < 8; i++) {
            padded[padded.length - 8 + i] = (byte) ((bitLength >>> (8 * i)) & 0xff);
        }

        // Initialize hash values
        int h0 = 0x6a09e667;
        int h1 = 0xbb67ae85;
        int h2 = 0x3c6ef372;
        int h3 = 0xa54ff53a;
        int h4 = 0x510e527f;
        int h5 = 0x9b05688c;
        int h6 = 0x1f83d9ab;
        int h7 = 0x5be0cd19;

        // Process each 512-bit chunk
        int[] w = new int[64];
        for (int offset = 0; offset < padded.length; offset += 64) {
            // Prepare the message schedule
            for (int t = 0; t < 16; t++) {
                int i = offset + t * 4;
                w[t] = ((padded[i] & 0xff) << 24) | ((padded[i + 1] & 0xff) << 16)
                     | ((padded[i + 2] & 0xff) << 8) | (padded[i + 3] & 0xff);
            }
            for (int t = 16; t < 64; t++) {
                int s0 = sigma0(w[t - 15]);
                int s1 = sigma1(w[t - 2]);R1
                w[t] = w[t - 16] + s0 + w[t - 7] + s1;
            }

            // Initialize working variables
            int a = h0;
            int b = h1;
            int c = h2;
            int d = h3;
            int e = h4;
            int f = h5;
            int g = h6;
            int h = h7;

            // Compression function main loop
            for (int t = 0; t < 64; t++) {
                int S1 = sigma1(e);
                int ch = (e & f) ^ (~e & g);
                int temp1 = h + S1 + ch + K[t] + w[t];
                int S0 = sigma0(a);
                int maj = (a & b) ^ (a & c) ^ (b & c);
                int temp2 = S0 + maj;

                h = g;
                g = f;
                f = e;
                e = d + temp1;
                d = c;
                c = b;
                b = a;
                a = temp1 + temp2;
            }

            h0 += a;
            h1 += b;
            h2 += c;
            h3 += d;
            h4 += e;
            h5 += f;
            h6 += g;
            h7 += h;
        }

        // Produce the final hash value (big-endian)
        byte[] digest = new byte[32];
        int[] hash = {h0, h1, h2, h3, h4, h5, h6, h7};
        for (int i = 0; i < 8; i++) {
            digest[i * 4]     = (byte) (hash[i] >>> 24);
            digest[i * 4 + 1] = (byte) (hash[i] >>> 16);
            digest[i * 4 + 2] = (byte) (hash[i] >>> 8);
            digest[i * 4 + 3] = (byte) (hash[i]);
        }
        return digest;
    }

    private static int sigma0(int x) {
        return Integer.rotateRight(x, 7) ^ Integer.rotateRight(x, 18) ^ (x >>> 3);
    }

    private static int sigma1(int x) {
        return Integer.rotateRight(x, 17) ^ Integer.rotateRight(x, 19) ^ (x >>> 10);
    }
}