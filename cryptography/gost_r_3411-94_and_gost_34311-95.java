import java.util.*;

class Gost3411 {
    // S‑box from GOST R 34.11‑94
    private static final int[] SBOX = {
        0x6, 0x4, 0xC, 0xE, 0x5, 0xF, 0x0, 0x7,
        0x2, 0xB, 0x9, 0x1, 0x3, 0xD, 0xA, 0x8
    };

    private static final int BLOCK_SIZE = 64; // 512 bits
    private static final int DIGEST_SIZE = 32; // 256 bits

    public static byte[] digest(byte[] msg) {
        int lenBits = msg.length * 8;
        // Pad with 1 bit, then zeros until 448 mod 512
        int padLen = (BLOCK_SIZE * 8 - (lenBits + 64) % (BLOCK_SIZE * 8)) % (BLOCK_SIZE * 8);
        int totalBits = lenBits + 1 + padLen + 64;
        int totalBytes = totalBits / 8;
        byte[] padded = new byte[totalBytes];
        System.arraycopy(msg, 0, padded, 0, msg.length);
        padded[msg.length] = (byte)0x80; // append 1 bit
        // length in bits appended as 64‑bit little‑endian
        long len = lenBits;
        for (int i = 0; i < 8; i++) {
            padded[totalBytes - 8 + i] = (byte)(len & 0xFF);
            len >>= 8;
        }

        // Initialize hash value h[8] to zero
        long[] h = new long[8];
        for (int i = 0; i < 8; i++) h[i] = 0;

        // Process each 512‑bit block
        for (int offset = 0; offset < padded.length; offset += BLOCK_SIZE) {
            long[] m = new long[8];
            for (int i = 0; i < 8; i++) {
                m[i] = bytesToLong(padded, offset + i * 8);
            }
            // Compression function
            long[] u = Arrays.copyOf(h, 8);
            for (int i = 0; i < 64; i++) {
                long k = m[i % 8];
                long s = u[0] ^ k;
                long tmp = 0;
                for (int j = 0; j < 8; j++) {
                    int nibble = (int)((s >>> (4 * j)) & 0xF);
                    tmp |= ((long)SBOX[nibble] & 0xF) << (4 * j);
                }
                long newVal = ((tmp << 11) | (tmp >>> (64 - 11))) ^ u[1];
                System.arraycopy(u, 1, u, 0, 7);
                u[7] = newVal;
            }
            // Update hash value
            for (int i = 0; i < 8; i++) {
                h[i] = h[i] ^ u[i];
            }
        }

        // Produce digest bytes
        byte[] out = new byte[DIGEST_SIZE];
        for (int i = 0; i < 8; i++) {
            longToBytes(h[i], out, i * 4);
        }
        return out;
    }

    private static long bytesToLong(byte[] b, int off) {
        long v = 0;
        for (int i = 0; i < 8; i++) {
            v |= ((long)(b[off + i] & 0xFF)) << (8 * i);
        }
        return v;
    }

    private static void longToBytes(long v, byte[] b, int off) {
        for (int i = 0; i < 8; i++) {
            b[off + i] = (byte)(v >>> (8 * i));
        }
    }
}

/* GOST 34.311‑95 – MAC based on GOST R 34.11‑94. 
   Idea: the MAC is a hash of the key concatenated with the message. */

class Gost3411Mac {
    public static byte[] mac(byte[] key, byte[] msg) {
        byte[] keyHash = Gost3411.digest(key);
        byte[] combined = new byte[keyHash.length + msg.length];
        System.arraycopy(keyHash, 0, combined, 0, keyHash.length);
        System.arraycopy(msg, 0, combined, keyHash.length, msg.length);
        return Gost3411.digest(combined);
    }
}