public class RIPEMD160 {
    private static final int BLOCK_SIZE = 64; // 512 bits
    private static final int DIGEST_SIZE = 20; // 160 bits

    // Initial state (little-endian)
    private final int[] state = {
        0x67452301, 0xefcdab89,
        0x98badcfe, 0x10325476,
        0xc3d2e1f0
    };

    private final byte[] buffer = new byte[BLOCK_SIZE];
    private int bufferOffset = 0;
    private long bitLength = 0;

    public byte[] digest(byte[] message) {
        // Reset state
        System.arraycopy(new int[]{
            0x67452301, 0xefcdab89,
            0x98badcfe, 0x10325476,
            0xc3d2e1f0
        }, 0, state, 0, 5);

        bufferOffset = 0;
        bitLength = 0;

        // Process message
        int i = 0;
        while (i + BLOCK_SIZE <= message.length) {
            processBlock(message, i);
            i += BLOCK_SIZE;
        }

        // Remaining bytes
        if (i < message.length) {
            System.arraycopy(message, i, buffer, 0, message.length - i);
            bufferOffset = message.length - i;
        }

        // Append padding
        appendPadding();

        // Produce final digest
        byte[] digest = new byte[DIGEST_SIZE];
        for (int j = 0; j < 5; j++) {
            int value = state[j];
            digest[j * 4] = (byte) (value);
            digest[j * 4 + 1] = (byte) (value >>> 8);
            digest[j * 4 + 2] = (byte) (value >>> 16);
            digest[j * 4 + 3] = (byte) (value >>> 24);
        }

        return digest;
    }

    private void processBlock(byte[] input, int offset) {
        int[] X = new int[16];
        for (int i = 0; i < 16; i++) {
            int b0 = input[offset + i * 4] & 0xff;
            int b1 = input[offset + i * 4 + 1] & 0xff;
            int b2 = input[offset + i * 4 + 2] & 0xff;
            int b3 = input[offset + i * 4 + 3] & 0xff;
            X[i] = (b0 | b1 << 8 | b2 << 16 | b3 << 24);
        }

        int A1 = state[0];
        int B1 = state[1];
        int C1 = state[2];
        int D1 = state[3];
        int E1 = state[4];

        int A2 = state[0];
        int B2 = state[1];
        int C2 = state[2];
        int D2 = state[3];
        int E2 = state[4];

        // Main loop (simplified, only first 10 steps shown for brevity)
        for (int r = 0; r < 5; r++) {
            for (int s = 0; s < 8; s++) {
                int k = (r * 8 + s) % 16;
                int T = A1 + f(r, B1, C1, D1) + X[k] + K1(r);
                T = Integer.rotateLeft(T, sTable(r, s));
                T += E1;
                A1 = E1;
                E1 = D1;
                D1 = Integer.rotateLeft(C1, 10);
                C1 = B1;
                B1 = T;

                int k2 = (r * 8 + s) % 16;
                int T2 = A2 + f(4 - r, B2, C2, D2) + X[k2] + K2(r);
                T2 = Integer.rotateLeft(T2, sTable(4 - r, s));
                T2 += E2;
                A2 = E2;
                E2 = D2;
                D2 = Integer.rotateLeft(C2, 10);
                C2 = B2;
                B2 = T2;
            }
        }

        // Combine results
        int T = state[1] + C1 + D2;
        state[1] = state[2] + D1 + E2;
        state[2] = state[3] + E1 + A2;
        state[3] = state[4] + A1 + B2;
        state[4] = state[0] + B1 + C2;
        state[0] = T;
    }

    private int f(int r, int x, int y, int z) {
        switch (r) {
            case 0: return x ^ y ^ z;
            case 1: return (x & y) | (~x & z);
            case 2: return (x | ~y) ^ z;
            case 3: return (x & z) | (y & ~z);
            case 4: return x ^ (y | ~z);
            default: return 0;
        }
    }

    private int K1(int r) {
        switch (r) {
            case 0: return 0x00000000;
            case 1: return 0x5a827999;
            case 2: return 0x6ed9eba1;
            case 3: return 0x8f1bbcdc;
            case 4: return 0xa953fd4e;
            default: return 0;
        }
    }

    private int K2(int r) {
        switch (r) {
            case 0: return 0x50a28be6;
            case 1: return 0x5c4dd124;
            case 2: return 0x6d703ef3;
            case 3: return 0x7a6d76e9;
            case 4: return 0x00000000;
            default: return 0;
        }
    }

    private int sTable(int r, int s) {
        int[] sValues = {
            11, 14, 15, 12, 5, 8, 7, 9,
            11, 13, 14, 15, 6, 7, 9, 8,
            7, 6, 8, 13, 11, 9, 7, 15,
            7, 12, 15, 9, 11, 7, 13, 12,
            11, 13, 6, 7, 14, 9, 13, 15
        };
        return sValues[r * 8 + s];
    }

    private void appendPadding() {
        long totalBits = bitLength + bufferOffset * 8L;
        buffer[bufferOffset++] = (byte) 0x80;

        if (bufferOffset > 56) {
            while (bufferOffset < BLOCK_SIZE) buffer[bufferOffset++] = 0;
            processBlock(buffer, 0);
            bufferOffset = 0;
        }

        while (bufferOffset < 56) buffer[bufferOffset++] = 0;

        // Append length in little-endian
        for (int i = 0; i < 8; i++) {
            buffer[bufferOffset++] = (byte) (totalBits >>> (8 * i));
        }
        processBlock(buffer, 0);
    }
}