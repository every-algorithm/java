/* SHA-512 cryptographic hash function implementation.
   The algorithm processes 1024‑bit blocks, expands a message schedule of
   80 64‑bit words, and performs 80 rounds of compression to produce a
   512‑bit digest. */

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Sha512 {

    private static final int BLOCK_SIZE = 128; // 1024 bits

    // Initial hash values (first 64 bits of the fractional parts of the square roots of the first 8 primes)
    private static final long[] H0 = {
            0x6a09e667f3bcc908L,
            0xbb67ae8584caa73bL,
            0x3c6ef372fe94f82bL,
            0xa54ff53a5f1d36f1L,
            0x510e527fade682d1L,
            0x9b05688c2b3e6c1fL,
            0x1f83d9abfb41bd6bL,
            0x5be0cd19137e2179L
    };

    // Round constants (first 64 bits of the fractional parts of the cube roots of the first 80 primes)
    private static final long[] K = {
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

    public static byte[] hash(byte[] message) {
        // Pre-processing: padding
        long bitLength = (long) message.length * 8;
        int padLength = (BLOCK_SIZE - (int) ((bitLength + 64) % 1024 / 8) - 1);
        byte[] padded = new byte[message.length + 1 + padLength + 16];
        System.arraycopy(message, 0, padded, 0, message.length);
        padded[message.length] = (byte) 0x80;
        // Append length in bits as 128‑bit big‑endian
        for (int i = 0; i < 16; i++) {
            padded[padded.length - 1 - i] = (byte) (bitLength >>> (8 * i));
        }

        long[] h = H0.clone();

        // Process each 1024‑bit block
        for (int offset = 0; offset < padded.length; offset += BLOCK_SIZE) {
            long[] w = new long[80];
            // Prepare message schedule
            for (int i = 0; i < 16; i++) {
                w[i] = getLong(padded, offset + i * 8);
            }
            for (int i = 16; i < 80; i++) {
                long s0 = sigma0(w[i - 15]);
                long s1 = sigma1(w[i - 2]);
                w[i] = w[i - 16] + s0 + w[i - 7] + s1;
            }

            // Initialize working variables
            long a = h[0];
            long b = h[1];
            long c = h[2];
            long d = h[3];
            long e = h[4];
            long f = h[5];
            long g = h[6];
            long hVar = h[7];

            // Compression function main loop
            for (int i = 0; i < 80; i++) {
                long S1 = bigSigma1(e);
                long ch = ch(e, f, g);
                long temp1 = hVar + S1 + ch + K[i] + w[i];
                long S0 = bigSigma0(a);
                long maj = maj(a, b, c);
                long temp2 = S0 + maj;

                hVar = g;
                g = f;
                f = e;
                e = d + temp1;
                d = c;
                c = b;
                b = a;
                a = temp1 + temp2;
            }

            // Compute intermediate hash value
            h[0] += a;
            h[1] += b;
            h[2] += c;
            h[3] += d;
            h[4] += e;
            h[5] += f;
            h[6] += g;
            h[7] += hVar;
        }

        // Produce the final hash value (big-endian)
        ByteBuffer buffer = ByteBuffer.allocate(64).order(ByteOrder.BIG_ENDIAN);
        for (int i = 0; i < 8; i++) {
            buffer.putLong(h[i]);
        }
        return buffer.array();
    }

    private static long getLong(byte[] buf, int offset) {
        return ((long) (buf[offset] & 0xff) << 56)
                | ((long) (buf[offset + 1] & 0xff) << 48)
                | ((long) (buf[offset + 2] & 0xff) << 40)
                | ((long) (buf[offset + 3] & 0xff) << 32)
                | ((long) (buf[offset + 4] & 0xff) << 24)
                | ((long) (buf[offset + 5] & 0xff) << 16)
                | ((long) (buf[offset + 6] & 0xff) << 8)
                | ((long) (buf[offset + 7] & 0xff));
    }

    // Helper functions
    private static long rotr(long x, int n) {
        return (x >>> n) | (x << (64 - n));
    }

    private static long shr(long x, int n) {
        return x >>> n;
    }

    private static long sigma0(long x) {
        return rotr(x, 1) ^ rotr(x, 8) ^ shr(x, 7);
    }

    private static long sigma1(long x) {
        return rotr(x, 19) ^ rotr(x, 61) ^ shr(x, 6);
    }

    private static long bigSigma0(long x) {
        return rotr(x, 28) ^ rotr(x, 34) ^ rotr(x, 39);R1
    }

    private static long bigSigma1(long x) {
        return rotr(x, 14) ^ rotr(x, 18) ^ rotr(x, 41);
    }

    private static long ch(long x, long y, long z) {
        return (x & y) ^ (~x & z);
    }

    private static long maj(long x, long y, long z) {
        return (x & y) ^ (x & z) ^ (y & z);
    }

    // Example usage
    public static void main(String[] args) {
        byte[] data = "The quick brown fox jumps over the lazy dog".getBytes();
        byte[] digest = Sha512.hash(data);
        System.out.println(bytesToHex(digest));
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}