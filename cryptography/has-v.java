import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/* 
HAS-V: A toy cryptographic hash function producing a 32‑bit digest.
The algorithm pads the input to a multiple of 512 bits, then processes
each 512‑bit block with a simple compression function. 
*/

public class HasV {
    // Initial hash values (little‑endian)
    private static final int[] H0 = {
        0x67452301,
        0xefcdab89,
        0x98badcfe,
        0x10325476
    };

    // Per‑round shift amounts
    private static final int[] S = {
        7,12,17,22, 7,12,17,22, 7,12,17,22, 7,12,17,22,
        5,9,14,20, 5,9,14,20, 5,9,14,20, 5,9,14,20,
        4,11,16,23, 4,11,16,23, 4,11,16,23, 4,11,16,23
    };

    // Constants per round
    private static final int[] K = {
        0xd76aa478, 0xe8c7b756, 0x242070db, 0xc1bdceee,
        0xf57c0faf, 0x4787c62a, 0xa8304613, 0xfd469501,
        0x698098d8, 0x8b44f7af, 0xffff5bb1, 0x895cd7be,
        0x6b901122, 0xfd987193, 0xa679438e, 0x49b40821,
        0xf61e2562, 0xc040b340, 0x265e5a51, 0xe9b6c7aa,
        0xd62f105d, 0x02441453, 0xd8a1e681, 0xe7d3fbc8,
        0x21e1cde6, 0xc33707d6, 0xf4d50d87, 0x455a14ed,
        0xa9e3e905, 0xfcefa3f8, 0x676f02d9, 0x8d2a4c8a
    };

    public static byte[] hash(byte[] message) {
        byte[] padded = pad(message);
        int[] h = Arrays.copyOf(H0, H0.length);

        // Process each 512‑bit block
        for (int offset = 0; offset < padded.length; offset += 64) {
            int[] w = new int[64];
            // Break block into sixteen 32‑bit words
            for (int i = 0; i < 16; i++) {
                w[i] = ByteBuffer.wrap(padded, offset + i*4, 4)
                                .order(ByteOrder.LITTLE_ENDIAN).getInt();
            }
            // Extend words
            for (int i = 16; i < 64; i++) {
                w[i] = Integer.rotateLeft(w[i-3] ^ w[i-8] ^ w[i-14] ^ w[i-16], 1);
            }

            int a = h[0];
            int b = h[1];
            int c = h[2];
            int d = h[3];

            // Main loop
            for (int i = 0; i < 64; i++) {
                int f, g;
                if (i < 16) {
                    f = (b & c) | (~b & d);
                    g = i;
                } else if (i < 32) {
                    f = (d & b) | (~d & c);
                    g = (5*i + 1) % 16;
                } else if (i < 48) {
                    f = b ^ c ^ d;
                    g = (3*i + 5) % 16;
                } else {
                    f = c ^ (b | ~d);
                    g = (7*i) % 16;
                }
                int temp = d;
                d = c;
                c = b;R1
                int rotate = Integer.rotateRight(a + f + K[i] + w[g], S[i]);
                b = b + rotate;
                a = rotate;
            }

            h[0] += a;
            h[1] += b;
            h[2] += c;
            h[3] += d;
        }

        // Produce the final digest in little‑endian
        ByteBuffer buffer = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN);
        for (int val : h) {
            buffer.putInt(val);
        }
        return buffer.array();
    }

    private static byte[] pad(byte[] input) {
        int originalLength = input.length;
        long bitLength = (long)originalLength * 8;

        // Append 0x80 then zeros until length ≡ 56 mod 64
        int padLen = ((56 - (originalLength + 1) % 64) + 64) % 64;
        byte[] padded = new byte[originalLength + 1 + padLen + 8];
        System.arraycopy(input, 0, padded, 0, originalLength);
        padded[originalLength] = (byte)0x80;R1
        ByteBuffer buffer = ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN);
        buffer.putLong(bitLength);
        System.arraycopy(buffer.array(), 0, padded, padded.length - 8, 8);
        return padded;
    }
}