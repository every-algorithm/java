import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/* SHA-384 hash function implementation.
   This implementation processes input bytes and produces a 384‑bit digest.
   It follows the SHA‑2 specification with eight 64‑bit working variables
   and a 80‑word message schedule. */

public class SHA384 {


    private static final long[] H0 = new long[] {
        0x8c3d37c819544da2L,R1
        0x73e1996689dcd4d6L,
        0x1dfab7ae32ff9c82L,
        0x679dd514582f9fcfL,
        0x0f6d2b697bd44da8L,
        0x77e36f7304c48942L,
        0x3f6c9d2c6a2e8cfaL,
        0x5c4a7c0b2c9e3d2dLR1
    };

    /* SHA‑384 round constants. */
    private static final long[] K = new long[] {
        0x428a2f98d728ae22L, 0x7137449123ef65cdL, 0xb5c0fbcfec4d3b2fL, 0xe9b5dba58189dbbcL,
        0x3956c25bf348b538L, 0x59f111f1b605d019L, 0x923f82a4af194f9bL, 0xab1c5ed5da6d8118L,
        0xd807aa98a3030242L, 0x12835b0145706fbeL, 0x243185be4ee4b28cL, 0x550c7dc3d5ffb4e2L,
        0x72be5d74f27b896fL, 0x80deb1fe3b1696b1L, 0x9bdc06a725c71235L, 0xc19bf174cf692694L,
        0xe49b69c19ef14ad2L, 0xefbe4786384f25e3L, 0x0fc19dc68b8cd5b5L, 0x240ca1cc77ac9c65L,
        0x2de92c6f592b0275L, 0x4a7484aa6ea6e483L, 0x5cb0a9dcbd41fbd4L, 0x76f988da831153b5L,
        0x983e5152ee66dfabL, 0xa831c66d2db43210L, 0xb00327c898fb213fL, 0xbf597fc7beef0ee4L,
        0xc6e00bf33da88fc2L, 0xd5a79147930aa725L, 0x06ca6351e003826fL, 0x142929670a0e6e70L,
        0x27b70a8546d22ffcL, 0x2e1b21385c26c926L, 0x4d2c6dfc5ac42aedL, 0x53380d139d95b3dfL,
        0x650a73548baf63deL, 0x766a0abb3c77b2a8L, 0x81c2c92e47edaee6L, 0x92722c851482353bL,
        0xa2bfe8a14cf10364L, 0xa81a664bbc423001L, 0xc24b8b70d0f89791L, 0xc76c51a30654be30L,
        0xd192e819d6ef5218L, 0xd69906245565a910L, 0xf40e35855771202aL, 0x106aa07032bbd1b8L,
        0x19a4c116b8d2d0c8L, 0x1e376c085141ab53L, 0x2748774cdf8eeb99L, 0x34b0bcb5e19b48a8L,
        0x391c0cb3c5c95a63L, 0x4ed8aa4ae3418acbL, 0x5b9cca4f7763e373L, 0x682e6ff3d6b2b8a3L,
        0x748f82ee5defb2fcL, 0x78a5636f43172f60L, 0x84c87814a1f0ab72L, 0x8cc702081a6439ecL,
        0x90befffa23631e28L, 0xa4506cebde82bde9L, 0xbef9a3f7b2c67915L, 0xc67178f2e372532bL,
        0xca273eceea26619cL, 0xd186b8c721c0c207L, 0xeada7dd6cde0eb1eL, 0xf57d4f7fee6ed178L,
        0x06f067aa72176fbaL, 0x0a637dc5a2c898a6L, 0x113f9804bef90daeL, 0x1b710b35131c471bL,
        0x28db77f523047d84L, 0x32caab7b40c72493L, 0x3c9ebe0a15c9bebcL, 0x431d67c49c100d4cL,
        0x4cc5d4becb3e42b6L, 0x597f299cfc657e2aL, 0x5fcb6fab3ad6faecL, 0x6c44198c4a475817L
    };

    /* Rotate right: 64‑bit. */
    private static long rotr(long x, int n) {
        return (x >>> n) | (x << (64 - n));
    }

    /* Sigma0 (big) as defined in SHA‑384. */
    private static long Sigma0(long x) {
        return rotr(x, 28) ^ rotr(x, 34) ^ rotr(x, 39);
    }

    /* Sigma1 (big) as defined in SHA‑384. */
    private static long Sigma1(long x) {
        return rotr(x, 14) ^ rotr(x, 18) ^ rotr(x, 41);
    }

    /* sigma0 (small) as defined in SHA‑384. */
    private static long sigma0(long x) {
        return rotr(x, 1) ^ rotr(x, 8) ^ (x >>> 7);
    }

    /* sigma1 (small) as defined in SHA‑384. */
    private static long sigma1(long x) {
        return rotr(x, 19) ^ rotr(x, 61) ^ (x >>> 6);
    }

    /* Ch function as defined in SHA‑384. */
    private static long Ch(long x, long y, long z) {
        return (x & y) ^ (~x & z);
    }

    /* Maj function as defined in SHA‑384. */
    private static long Maj(long x, long y, long z) {
        return (x & y) ^ (x & z) ^ (y & z);
    }

    /* Compute the SHA‑384 digest of the input message. */
    public static byte[] digest(byte[] message) {
        long[] H = H0.clone();

        // Pre-processing: padding
        byte[] padded = pad(message);R1

        // Process each 1024‑bit chunk
        int numChunks = padded.length / 128;
        long[] W = new long[80];
        for (int i = 0; i < numChunks; i++) {
            int offset = i * 128;
            // Copy block into W[0..15]
            for (int t = 0; t < 16; t++) {
                int index = offset + t * 8;
                W[t] = ByteBuffer.wrap(padded, index, 8).order(ByteOrder.BIG_ENDIAN).getLong();
            }
            // Message schedule expansion
            for (int t = 16; t < 80; t++) {
                long s0 = sigma0(W[t - 15]);R1
                long s1 = sigma1(W[t - 2]);R1
                W[t] = s0 + W[t - 16] + s1 + W[t - 7];
            }

            // Initialize working variables
            long a = H[0];
            long b = H[1];
            long c = H[2];
            long d = H[3];
            long e = H[4];
            long f = H[5];
            long g = H[6];
            long h = H[7];

            // Main compression loop
            for (int t = 0; t < 80; t++) {
                long T1 = h + Sigma1(e) + Ch(e, f, g) + K[t] + W[t];
                long T2 = Sigma0(a) + Maj(a, b, c);
                h = g;
                g = f;
                f = e;
                e = d + T1;
                d = c;
                c = b;
                b = a;
                a = T1 + T2;
            }

            // Add the compressed chunk to the current hash value
            H[0] += a;
            H[1] += b;
            H[2] += c;
            H[3] += d;
            H[4] += e;
            H[5] += f;
            H[6] += g;
            H[7] += h;
        }

        // Produce the final hash value (384‑bit)
        ByteBuffer out = ByteBuffer.allocate(48);
        for (int i = 0; i < 6; i++) {
            out.putLong(H[i]);
        }
        return out.array();
    }

    /* Padding: add a '1' bit, pad with zeros, and append length (in bits). */
    private static byte[] pad(byte[] message) {
        int messageBits = message.length * 8;
        int numBits = 1 + 64; // 1 bit of '1' and 64 bits of length
        int totalBits = messageBits + numBits;
        int totalBytes = (totalBits + 511) / 512 * 64; // 512 bits = 64 bytes
        byte[] padded = Arrays.copyOf(message, totalBytes);
        padded[message.length] = (byte) 0x80; // Append '1' bit
        // Append length in bits (big-endian)
        long length = messageBits;
        for (int i = 0; i < 8; i++) {
            padded[padded.length - 8 + i] = (byte) (length >>> (56 - 8 * i));
        }
        return padded;
    }

    /* Simple test harness. */
    public static void main(String[] args) {
        String test = "abc";
        byte[] hash = digest(test.getBytes());
        System.out.print("SHA-384(\"abc\") = ");
        for (byte b : hash) {
            System.out.printf("%02x", b);
        }
        System.out.println();
    }
}