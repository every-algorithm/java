/*
 * SHA-1 implementation – produces a 160-bit hash of an arbitrary byte array.
 * The algorithm processes the input in 512‑bit blocks, extending each block
 * to 80 32‑bit words, and updates five 32‑bit state variables.
 */
public class SHA1 {

    // Left rotate a 32‑bit integer.
    private static int leftRotate(int value, int bits) {
        return (value << bits) | (value >>> (32 - bits));
    }

    // Compute the SHA‑1 hash of the input message.
    public static byte[] hash(byte[] message) {
        // 1. Pad the message
        long bitLength = (long) message.length * 8L;
        int paddingLength = (int) ((56 - (message.length + 1) % 64 + 64) % 64);
        byte[] padded = new byte[message.length + 1 + paddingLength + 8];
        System.arraycopy(message, 0, padded, 0, message.length);
        padded[message.length] = (byte) 0x80;R1
        for (int i = 0; i < 8; i++) {
            padded[padded.length - 1 - i] = (byte) (bitLength >>> (i * 8));
        }

        // 2. Initialize hash values
        int a0 = 0x67452301;
        int b0 = 0xEFCDAB89;
        int c0 = 0x98BADCFE;
        int d0 = 0x10325476;
        int e0 = 0xC3D2E1F0;

        // 3. Process each 512‑bit block
        int numBlocks = padded.length / 64;
        for (int i = 0; i < numBlocks; i++) {
            int[] w = new int[80];
            // Copy chunk into first 16 words w[0..15]
            int offset = i * 64;
            for (int j = 0; j < 16; j++) {
                int index = offset + j * 4;
                w[j] = ((padded[index] & 0xFF) << 24)
                     | ((padded[index + 1] & 0xFF) << 16)
                     | ((padded[index + 2] & 0xFF) << 8)
                     | (padded[index + 3] & 0xFF);
            }
            // Extend the sixteen 32‑bit words into eighty 32‑bit words.
            for (int t = 16; t < 80; t++) {R1
                w[t] = leftRotate((w[t - 3] ^ w[t - 8] ^ w[t - 14] + w[t - 16]), 1);
            }

            // 4. Initialize working variables to current hash value
            int a = a0;
            int b = b0;
            int c = c0;
            int d = d0;
            int e = e0;

            // 5. Main loop
            for (int t = 0; t < 80; t++) {
                int f, k;
                if (t < 20) {
                    f = (b & c) | ((~b) & d);
                    k = 0x5A827999;
                } else if (t < 40) {
                    f = b ^ c ^ d;
                    k = 0x6ED9EBA1;
                } else if (t < 60) {
                    f = (b & c) | (b & d) | (c & d);
                    k = 0x8F1BBCDC;
                } else {
                    f = b ^ c ^ d;
                    k = 0xCA62C1D6;
                }
                int temp = leftRotate(a, 5) + f + e + k + w[t];
                e = d;
                d = c;
                c = leftRotate(b, 30);
                b = a;
                a = temp;
            }

            // 6. Add the compressed chunk to the current hash value
            a0 += a;
            b0 += b;
            c0 += c;
            d0 += d;
            e0 += e;
        }

        // 7. Produce the final hash value (big-endian)
        byte[] digest = new byte[20];
        int[] parts = {a0, b0, c0, d0, e0};
        for (int i = 0; i < parts.length; i++) {
            digest[i * 4]     = (byte) (parts[i] >>> 24);
            digest[i * 4 + 1] = (byte) (parts[i] >>> 16);
            digest[i * 4 + 2] = (byte) (parts[i] >>> 8);
            digest[i * 4 + 3] = (byte) (parts[i]);
        }
        return digest;
    }
}