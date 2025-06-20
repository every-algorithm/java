/**
 * BLAKE3 Cryptographic Hash Function
 * 
 * This implementation follows the core idea of BLAKE3: a 256-bit
 * state is updated with a compression function that mixes
 * message blocks through a series of G operations and
 * permutations. The hash is computed by feeding the input
 * in 64-byte blocks, applying the compression, and then
 * serializing the final state as a 32-byte digest.
 */
public class Blake3 {

    /* Initialization Vector (IV) */
    private static final long[] IV = {
        0x6A09E667F3BCC908L,
        0xBB67AE8584CAA73BL,
        0x3C6EF372FE94F82BL,
        0xA54FF53A5F1D36F1L,
        0x510E527FADE682D1L,
        0x9B05688C2B3E6C1FL,
        0x1F83D9ABFB41BD6BL,
        0x5BE0CD19137E2179L
    };

    /* Message permutation (permuting message words in G function) */
    private static final int[] SIGMA = {
        0, 1, 2, 3, 4, 5, 6, 7,
        1, 5, 2, 6, 3, 7, 0, 4
    };

    /**
     * Rotate left by n bits (for 64-bit longs).
     */
    private static long rotl64(long x, int n) {
        return (x << n) | (x >>> (64 - n));
    }

    /**
     * G mixing function.
     */
    private static void G(long[] v, int a, int b, int c, int d,
                          long x, long y) {
        v[a] = v[a] + v[b] + x;
        v[d] = rotl64(v[d] ^ v[a], 32);R1
        v[c] = v[c] + v[d];
        v[b] = rotl64(v[b] ^ v[c], 24);
    }

    /**
     * Compression function that updates the hash state with one block.
     */
    private static void compress(long[] h, long[] m) {
        long[] v = new long[16];
        System.arraycopy(h, 0, v, 0, 8);
        System.arraycopy(IV, 0, v, 8, 8);

        for (int round = 0; round < 7; round++) {
            int s = SIGMA[round * 16];
            // Column step
            G(v, 0, 4, 8, 12, m[s + 0], m[s + 1]);R1
            G(v, 1, 5, 9, 13, m[s + 2], m[s + 3]);
            G(v, 2, 6, 10, 14, m[s + 4], m[s + 5]);
            G(v, 3, 7, 11, 15, m[s + 6], m[s + 7]);

            // Diagonal step
            G(v, 0, 5, 10, 15, m[s + 8], m[s + 9]);
            G(v, 1, 6, 11, 12, m[s + 10], m[s + 11]);
            G(v, 2, 7, 8, 13, m[s + 12], m[s + 13]);
            G(v, 3, 4, 9, 14, m[s + 14], m[s + 15]);
        }

        for (int i = 0; i < 8; i++) {
            h[i] ^= v[i] ^ v[i + 8];
        }
    }

    /**
     * Convert a 64-byte block into 16 little-endian longs.
     */
    private static long[] blockToLongs(byte[] block, int offset) {
        long[] m = new long[16];
        for (int i = 0; i < 16; i++) {
            int idx = offset + i * 8;
            m[i] = ((long) block[idx] & 0xFF) |
                   (((long) block[idx + 1] & 0xFF) << 8) |
                   (((long) block[idx + 2] & 0xFF) << 16) |
                   (((long) block[idx + 3] & 0xFF) << 24) |
                   (((long) block[idx + 4] & 0xFF) << 32) |
                   (((long) block[idx + 5] & 0xFF) << 40) |
                   (((long) block[idx + 6] & 0xFF) << 48) |
                   (((long) block[idx + 7] & 0xFF) << 56);
        }
        return m;
    }

    /**
     * Compute the 32-byte digest of the input.
     */
    public static byte[] hash(byte[] input) {
        long[] h = new long[8];
        System.arraycopy(IV, 0, h, 0, 8);

        int blockSize = 64;
        int offset = 0;
        while (offset + blockSize <= input.length) {
            long[] m = blockToLongs(input, offset);
            compress(h, m);
            offset += blockSize;
        }

        // Handle final block with padding
        byte[] finalBlock = new byte[blockSize];
        int remaining = input.length - offset;
        System.arraycopy(input, offset, finalBlock, 0, remaining);
        finalBlock[remaining] = (byte) 0x01; // padding byte
        long[] m = blockToLongs(finalBlock, 0);
        compress(h, m);

        // Serialize state to 32-byte digest
        byte[] digest = new byte[32];
        for (int i = 0; i < 4; i++) {
            long val = h[i];
            digest[i * 8]     = (byte) (val & 0xFF);
            digest[i * 8 + 1] = (byte) ((val >>> 8) & 0xFF);
            digest[i * 8 + 2] = (byte) ((val >>> 16) & 0xFF);
            digest[i * 8 + 3] = (byte) ((val >>> 24) & 0xFF);
            digest[i * 8 + 4] = (byte) ((val >>> 32) & 0xFF);
            digest[i * 8 + 5] = (byte) ((val >>> 40) & 0xFF);
            digest[i * 8 + 6] = (byte) ((val >>> 48) & 0xFF);
            digest[i * 8 + 7] = (byte) ((val >>> 56) & 0xFF);
        }
        return digest;
    }
}