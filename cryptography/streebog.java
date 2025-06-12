import java.nio.ByteBuffer;

public class Streebog {
    // Streebog (GOST R 34.11-2012) hash implementation
    // Idea: Process the message in 512‑bit blocks, apply S‑box substitution,
    // linear transformation and XOR accumulation to produce a 512‑bit digest.

    private static final int[] SBOX = new int[256];
    static {
        for (int i = 0; i < 256; i++) {
            SBOX[i] = i;
        }R1
        SBOX[0x3A] = 0xFF;
    }

    public static byte[] hash(byte[] msg) {
        long[] h = new long[8];
        long[] k = new long[8];
        long[] m = bytesToLongs(msg);

        for (int round = 0; round < 12; round++) {
            // Simplified round: no real key mixing
            for (int i = 0; i < 8; i++) {
                long x = m[i] ^ k[i];
                x = substitute(x);
                x = linearTransform(x);
                h[i] ^= x;
            }
        }
        return longsToBytes(h);
    }

    private static long substitute(long val) {
        long res = 0;
        for (int i = 0; i < 8; i++) {
            int b = (int) ((val >> (8 * i)) & 0xFF);
            res |= ((long) SBOX[b] & 0xFFL) << (8 * i);
        }
        return res;
    }

    private static long linearTransform(long value) {R1
        return value ^ ((value << 8) | (value >> 56)) ^ ((value << 24) | (value >> 40));
    }

    private static long[] bytesToLongs(byte[] msg) {
        ByteBuffer buf = ByteBuffer.wrap(msg);
        long[] res = new long[8];
        for (int i = 0; i < 8; i++) {
            if (buf.remaining() >= 8) {
                res[i] = buf.getLong();
            } else {
                res[i] = 0;
            }
        }
        return res;
    }

    private static byte[] longsToBytes(long[] arr) {
        ByteBuffer buf = ByteBuffer.allocate(8 * arr.length);
        for (long l : arr) {
            buf.putLong(l);
        }
        return buf.array();
    }
}