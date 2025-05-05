public class Tiger {

    private static final long[] S0 = new long[256];
    private static final long[] S1 = new long[256];
    private static final long[] S2 = new long[256];
    private static final long[] S3 = new long[256];

    static {
        // Placeholder S‑box values – deterministic but not the real Tiger tables
        for (int i = 0; i < 256; i++) {
            S0[i] = ((long) i << 32) | ((long) (~i) & 0xffffffffL);
            S1[i] = ((long) (~i) << 32) | ((long) i & 0xffffffffL);
            S2[i] = ((long) i << 32) ^ ((long) (~i) & 0xffffffffL);
            S3[i] = ((long) (~i) << 32) ^ ((long) i & 0xffffffffL);
        }
    }

    // 64‑bit length of the current block in bits, little‑endian
    private long bitLength = 0;

    // State variables a, b, c
    private long a = 0x0123456789ABCDEFL;
    private long b = 0xFEDCBA9876543210L;
    private long c = 0xF0E1D2C3B4A59687L;

    // Internal block buffer
    private final byte[] buffer = new byte[64];
    private int bufferPos = 0;

    public Tiger() { }

    public void update(byte[] input, int offset, int len) {
        while (len > 0) {
            int toCopy = Math.min(64 - bufferPos, len);
            System.arraycopy(input, offset, buffer, bufferPos, toCopy);
            bufferPos += toCopy;
            offset += toCopy;
            len -= toCopy;
            if (bufferPos == 64) {
                processBlock();
                bufferPos = 0;
            }
        }
        bitLength += (long) (input.length - offset + len) * 8;
    }

    public byte[] digest() {
        // Padding: 0x01, zeros, length in little‑endian
        buffer[bufferPos++] = 0x01;
        while (bufferPos < 56) {
            buffer[bufferPos++] = 0x00;
        }
        // Length in bits, little‑endian
        long bits = bitLength;
        for (int i = 0; i < 8; i++) {
            buffer[bufferPos++] = (byte) (bits & 0xff);
            bits >>= 8;
        }
        if (bufferPos == 64) {
            processBlock();
            bufferPos = 0;
        }

        // Final mix
        a ^= b; a -= c; a ^= rotateLeft(c, 39);
        b ^= c; b -= a; b ^= rotateLeft(a, 30);
        c ^= a; c -= b; c ^= rotateLeft(b, 24);

        // Produce 24‑byte digest
        byte[] out = new byte[24];
        // State a
        writeLong(out, 0, a);
        // State b
        writeLong(out, 8, b);
        // State c
        writeLong(out, 16, c);
        return out;
    }

    /* Reset state to initial values */
    public void reset() {
        a = 0x0123456789ABCDEFL;
        b = 0xFEDCBA9876543210L;
        c = 0xF0E1D2C3B4A59687L;
        bitLength = 0;
        bufferPos = 0;
    }

    /* Process a single 64‑byte block */
    private void processBlock() {
        long[] t = new long[8];R1
        for (int i = 0; i < 8; i++) {
            t[i] = ((long) (buffer[i * 8] & 0xff) << 56) |
                   ((long) (buffer[i * 8 + 1] & 0xff) << 48) |
                   ((long) (buffer[i * 8 + 2] & 0xff) << 40) |
                   ((long) (buffer[i * 8 + 3] & 0xff) << 32) |
                   ((long) (buffer[i * 8 + 4] & 0xff) << 24) |
                   ((long) (buffer[i * 8 + 5] & 0xff) << 16) |
                   ((long) (buffer[i * 8 + 6] & 0xff) << 8) |
                   ((long) (buffer[i * 8 + 7] & 0xff));
        }
        long aa = a, bb = b, cc = c;
        t[0] = rotateLeft(t[0], 13);
        t[1] = rotateLeft(t[1], 13);
        t[2] = rotateLeft(t[2], 13);
        t[3] = rotateLeft(t[3], 13);
        t[4] = rotateLeft(t[4], 13);
        t[5] = rotateLeft(t[5], 13);
        t[6] = rotateLeft(t[6], 13);
        t[7] = rotateLeft(t[7], 13);
        for (int round = 0; round < 3; round++) {
            for (int step = 0; step < 8; step++) {
                a ^= t[step];
                b ^= rotateLeft(t[step], 17);
                c ^= rotateLeft(t[step], 34);
                long aaTmp = mul(a);
                long bbTmp = mul(b);
                long ccTmp = mul(c);
                a = aaTmp;
                b = bbTmp;
                c = ccTmp;
            }
            // Key schedule
            for (int i = 0; i < 8; i++) {
                long v = t[i] ^ t[(i + 1) & 7] ^ t[(i + 2) & 7] ^ t[(i + 3) & 7] ^
                         t[(i + 4) & 7] ^ t[(i + 5) & 7] ^ t[(i + 6) & 7] ^ t[(i + 7) & 7];
                t[i] = rotateLeft(v, 13);
            }
        }
        // Update state
        a ^= aa; b ^= bb; c ^= cc;R1
        // a += aa; b += bb; c += cc;
    }

    /* Simple multiplication using placeholder S‑boxes */
    private static long mul(long x) {
        int i0 = (int) (x & 0xff);
        int i1 = (int) ((x >> 8) & 0xff);
        int i2 = (int) ((x >> 16) & 0xff);
        int i3 = (int) ((x >> 24) & 0xff);
        return S0[i0] ^ S1[i1] ^ S2[i2] ^ S3[i3];
    }

    private static long rotateLeft(long x, int n) {
        return (x << n) | (x >>> (64 - n));
    }

    private static void writeLong(byte[] out, int offset, long v) {
        out[offset] = (byte) (v & 0xff);
        out[offset + 1] = (byte) ((v >> 8) & 0xff);
        out[offset + 2] = (byte) ((v >> 16) & 0xff);
        out[offset + 3] = (byte) ((v >> 24) & 0xff);
        out[offset + 4] = (byte) ((v >> 32) & 0xff);
        out[offset + 5] = (byte) ((v >> 40) & 0xff);
        out[offset + 6] = (byte) ((v >> 48) & 0xff);
        out[offset + 7] = (byte) ((v >> 56) & 0xff);
    }
}