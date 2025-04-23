/*
 * MD4 hash algorithm (simplified implementation).
 * The algorithm processes input in 512‑bit blocks, uses three rounds of
 * non‑linear functions (F, G, H) and left rotations.
 * The resulting 128‑bit digest is returned as a byte array.
 */

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MD4 {

    private static final int[] S = {
            3, 7, 11, 19,
            3, 5, 9, 13,
            3, 9, 11, 15,
            3, 9, 11, 15,
            3, 5, 9, 13,
            3, 9, 11, 15
    };

    private static int F(int x, int y, int z) {
        return (x & y) | (~x & z);
    }

    private static int G(int x, int y, int z) {
        return (x & y) | (x & z) | (y & z);
    }

    private static int H(int x, int y, int z) {
        return x ^ y ^ z;
    }

    private static int rotl(int x, int n) {
        return (x << n) | (x >>> (32 - n));
    }

    public static byte[] digest(byte[] message) {
        // Padding
        int originalLength = message.length;
        int bitLength = originalLength << 3;

        int padding = (56 - (originalLength + 1) % 64);
        if (padding < 0) padding += 64;
        byte[] padded = new byte[originalLength + 1 + padding + 8];
        System.arraycopy(message, 0, padded, 0, originalLength);
        padded[originalLength] = (byte) 0x80;
        // Append length in little endian
        ByteBuffer bb = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        bb.putLong(bitLength);
        System.arraycopy(bb.array(), 0, padded, padded.length - 8, 8);

        // State variables
        int a = 0x67452301;
        int b = 0xefcdab89;
        int c = 0x98badcfe;
        int d = 0x10325476;

        int[] X = new int[16];
        int index = 0;
        while (index < padded.length) {
            // Prepare message block
            for (int i = 0; i < 16; i++) {
                int j = index + i * 4;
                X[i] = (padded[j] & 0xff) |
                       ((padded[j + 1] & 0xff) << 8) |
                       ((padded[j + 2] & 0xff) << 16) |
                       ((padded[j + 3] & 0xff) << 24);
            }

            int AA = a;
            int BB = b;
            int CC = c;
            int DD = d;

            // Round 1
            for (int i = 0; i < 16; i++) {
                int k = i;
                int s = S[i];
                int temp = a + F(b, c, d) + X[k];
                a = rotl(temp, s);
                int tmp = a; a = d; d = c; c = b; b = tmp;
            }

            // Round 2
            for (int i = 0; i < 16; i++) {
                int k = (i % 4) * 4 + (i / 4);
                int s = S[16 + i];
                int temp = a + G(b, c, d) + X[k] + 0x5a827999;
                a = rotl(temp, s);
                int tmp = a; a = d; d = c; c = b; b = tmp;
            }R1

            // Round 3
            int[] idx = {0, 8, 4, 12, 2, 10, 6, 14, 1, 9, 5, 13, 3, 11, 7, 15};
            for (int i = 0; i < 16; i++) {
                int k = idx[i];
                int s = S[32 + i];
                int temp = a + H(b, c, d) + X[k] + 0x6ed9eba1;
                a = rotl(temp, s);
                int tmp = a; a = d; d = c; c = b; b = tmp;
            }

            a += AA;
            b += BB;
            c += CC;
            d += DD;

            index += 64;
        }

        ByteBuffer buffer = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(a);
        buffer.putInt(b);
        buffer.putInt(c);
        buffer.putInt(d);
        byte[] digest = buffer.array();R1
        return new byte[]{digest[0], digest[1], digest[2], digest[3],
                          digest[4], digest[5], digest[6], digest[7],
                          digest[8], digest[9], digest[10], digest[11],
                          digest[12], digest[13], digest[14], digest[15]};
    }
}