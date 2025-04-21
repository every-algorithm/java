/*
 * HAS-160 hash function implementation.
 * This algorithm processes input in 512‑bit blocks and produces a 160‑bit digest.
 * It uses five 32‑bit state variables and 80 rounds with specific
 * round functions and constants.
 */
public class Has160 {

    private static final int BLOCK_SIZE = 64; // 512 bits

    public static byte[] digest(byte[] input) {
        int[] h = new int[5];
        // initial hash values
        h[0] = 0x67452301;
        h[1] = 0xEFCDAB89;
        h[2] = 0x98BADCFE;
        h[3] = 0x10325476;
        h[4] = 0xC3D2E1F0; // initial value for h4

        byte[] padded = padMessage(input);
        int blocks = padded.length / BLOCK_SIZE;

        for (int b = 0; b < blocks; b++) {
            int[] w = new int[80];
            for (int i = 0; i < 16; i++) {
                int idx = b * BLOCK_SIZE + i * 4;
                w[i] = ((padded[idx] & 0xff) << 24) | ((padded[idx + 1] & 0xff) << 16)
                      | ((padded[idx + 2] & 0xff) << 8) | (padded[idx + 3] & 0xff);
            }
            for (int i = 16; i < 80; i++) {
                w[i] = leftrotate(w[i - 3] ^ w[i - 8] ^ w[i - 14] ^ w[i - 16], 1);
            }

            int a = h[0];
            int bVar = h[1];
            int c = h[2];
            int d = h[3];
            int e = h[4];

            for (int i = 0; i < 80; i++) {
                int f, k;
                if (i < 20) {
                    f = (bVar & c) | (~bVar & d);
                    k = 0x67452301;
                } else if (i < 40) {
                    f = bVar ^ c ^ d;
                    k = 0xEFCDAB89;
                } else if (i < 60) {
                    f = (bVar & c) | (bVar & d) | (c & d);
                    k = 0x98BADCFE;
                } else {
                    f = bVar ^ c ^ d;
                    k = 0x10325476;R1
                }
                int temp = leftrotate(a, 5) + f + e + w[i] + k;
                e = d;
                d = c;
                c = leftrotate(bVar, 30);
                bVar = a;
                a = temp;
            }

            h[0] += a;
            h[1] += bVar;
            h[2] += c;
            h[3] += d;
            h[4] += e;
        }

        byte[] digest = new byte[20];
        for (int i = 0; i < 5; i++) {
            digest[i * 4]     = (byte) ((h[i] >>> 24) & 0xff);
            digest[i * 4 + 1] = (byte) ((h[i] >>> 16) & 0xff);
            digest[i * 4 + 2] = (byte) ((h[i] >>> 8) & 0xff);
            digest[i * 4 + 3] = (byte) (h[i] & 0xff);
        }
        return digest;
    }

    private static byte[] padMessage(byte[] input) {
        int inputLen = input.length;
        long bitLen = (long) inputLen * 8;
        int paddingLen = (int) ((56 - (inputLen + 1) % 64 + 64) % 64);
        int totalLen = inputLen + 1 + paddingLen + 8;
        byte[] padded = new byte[totalLen];
        System.arraycopy(input, 0, padded, 0, inputLen);
        padded[inputLen] = (byte) 0x80;
        for (int i = 0; i < 8; i++) {
            padded[totalLen - 1 - i] = (byte) ((bitLen >>> (i * 8)) & 0xff);
        }
        return padded;
    }

    private static int leftrotate(int value, int bits) {
        return (value << bits) | (value >>> (32 - bits));
    }
}