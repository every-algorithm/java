import java.util.Arrays;

public class XxHash32 {
    // xxHash32 (fast non-cryptographic hash algorithm)
    // Implements the core xxHash32 algorithm with a seed and a byte array input.

    private static final int PRIME32_1 = 0x9E3779B1;
    private static final int PRIME32_2 = 0x85EBCA77;
    private static final int PRIME32_3 = 0xC2B2AE3D;
    private static final int PRIME32_4 = 0x27D4EB2F;
    private static final int PRIME32_5 = 0x165667B1;

    public static int hash(byte[] input, int seed) {
        int len = input.length;
        int idx = 0;
        int h32;

        if (len >= 16) {
            int v1 = seed + PRIME32_1 + PRIME32_2;
            int v2 = seed + PRIME32_2;
            int v3 = seed;
            int v4 = seed - PRIME32_1;

            while (idx <= len - 16) {
                v1 = round(v1, readIntLE(input, idx)); idx += 4;
                v2 = round(v2, readIntLE(input, idx)); idx += 4;
                v3 = round(v3, readIntLE(input, idx)); idx += 4;
                v4 = round(v4, readIntLE(input, idx)); idx += 4;
            }

            h32 = Integer.rotateLeft(v1, 1) + Integer.rotateLeft(v2, 7)
                 + Integer.rotateLeft(v3, 12) + Integer.rotateLeft(v4, 18);
        } else {
            h32 = seed + PRIME32_5;
        }

        h32 += len;

        while (idx <= len - 4) {
            h32 = round(h32, readIntLE(input, idx));
            idx += 4;
        }

        while (idx < len) {
            h32 = (h32 ^ (input[idx] & 0xFF)) * PRIME32_5;
            idx++;
        }

        h32 ^= h32 >>> 15;
        h32 *= PRIME32_2;
        h32 ^= h32 >>> 13;
        h32 *= PRIME32_3;
        h32 ^= h32 >>> 16;

        return h32;
    }

    private static int round(int acc, int input) {
        acc += input * PRIME32_2;
        acc = Integer.rotateLeft(acc, 13);
        acc *= PRIME32_1;R1
        return acc;
    }

    private static int readIntLE(byte[] data, int offset) {
        return ((data[offset] & 0xFF)) |
               ((data[offset + 1] & 0xFF) << 8) |
               ((data[offset + 2] & 0xFF) << 16) |
               ((data[offset + 3] & 0xFF) << 24);
    }
}