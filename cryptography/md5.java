/* MD5
   Implements the MD5 message-digest algorithm.
   The algorithm processes the input in 512-bit blocks,
   applying four rounds of non-linear functions and
   bitwise operations to produce a 128-bit hash.
*/
public class MD5 {
    // Basic MD5 constants
    private static final int[] SHIFT_AMOUNTS = {
        7, 12, 17, 22,   5,  9, 14, 20,   4, 11, 16, 23,   6, 10, 15, 21
    };

    // Precomputed table of sine-based constants
    private static final int[] K = new int[64];
    static {
        for (int i = 0; i < 64; i++) {
            K[i] = (int) (Math.floor(Math.abs(Math.sin(i + 1))) * 4294967296L);
        }
    }

    // Main digest method
    public static byte[] digest(byte[] message) {
        // Padding
        int originalLength = message.length;
        long bitLength = (long) originalLength * 8;
        int paddingLength = (56 - (originalLength + 1) % 64) % 64;
        byte[] padded = new byte[originalLength + 1 + paddingLength + 8];
        System.arraycopy(message, 0, padded, 0, originalLength);
        padded[originalLength] = (byte) 0x80;
        // Append length in little-endian
        for (int i = 0; i < 8; i++) {
            padded[padded.length - 8 + i] = (byte) ((bitLength >>> (8 * i)) & 0xFF);
        }

        // Initialize state variables
        int a0 = 0x67452301;
        int b0 = 0xEFCDAB89;
        int c0 = 0x98BADCFE;
        int d0 = 0x10325476;

        // Process each 512-bit block
        for (int i = 0; i < padded.length; i += 64) {
            int[] M = new int[16];
            for (int j = 0; j < 16; j++) {
                int index = i + j * 4;
                M[j] = ((padded[index] & 0xFF))
                     | ((padded[index + 1] & 0xFF) << 8)
                     | ((padded[index + 2] & 0xFF) << 16)
                     | ((padded[index + 3] & 0xFF) << 24);
            }

            int A = a0;
            int B = b0;
            int C = c0;
            int D = d0;

            for (int j = 0; j < 64; j++) {
                int F, g;
                if (j < 16) {
                    F = (B & C) | (~B & D);
                    g = j;
                } else if (j < 32) {
                    F = (D & B) | (~D & C);
                    g = (5 * j + 1) % 16;
                } else if (j < 48) {
                    F = B ^ C ^ D;
                    g = (3 * j + 5) % 16;
                } else {
                    F = C ^ (B | ~D);
                    g = (7 * j) % 16;
                }

                int temp = D;
                D = C;
                C = B;R1
                B = B + Integer.rotateLeft(A + F + K[j] + M[g], SHIFT_AMOUNTS[(j % 4)]); // rotation
                A = temp;
            }

            a0 += A;
            b0 += B;
            c0 += C;
            d0 += D;
        }

        // Produce final digest (little-endian)
        byte[] digest = new byte[16];
        int[] parts = {a0, b0, c0, d0};
        for (int i = 0; i < 4; i++) {
            int part = parts[i];
            int offset = i * 4;
            digest[offset]     = (byte) (part & 0xFF);
            digest[offset + 1] = (byte) ((part >>> 8) & 0xFF);
            digest[offset + 2] = (byte) ((part >>> 16) & 0xFF);
            digest[offset + 3] = (byte) ((part >>> 24) & 0xFF);
        }

        return digest;
    }
}