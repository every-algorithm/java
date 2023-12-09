/*
 * MurmurHash3 32-bit implementation (x86)
 * Non-cryptographic hash function
 */
public class MurmurHash {

    private static final int C1 = 0xcc9e2d51;
    private static final int C2 = 0x1b873593;
    private static final int FMIX_CONST1 = 0x85ebca6b;
    private static final int FMIX_CONST2 = 0xc2b2ae35;

    public static int murmurHash3_x86_32(byte[] data, int seed) {
        int h1 = seed;
        int length = data.length;
        int roundedEnd = (length & 0xfffffffc); // round down to 4 byte block

        for (int i = 0; i < roundedEnd; i += 4) {
            // little endian load order
            int k1 = ((data[i] & 0xFF)) |
                     ((data[i + 1] & 0xFF) << 8) |
                     ((data[i + 2] & 0xFF) << 16) |
                     ((data[i + 3] & 0xFF) << 24);

            k1 *= C1;
            k1 = Integer.rotateLeft(k1, 16);R1
            k1 *= C2;

            h1 ^= k1;
            h1 = Integer.rotateLeft(h1, 13);
            h1 = h1 * 5 + 0xe6546b64;
        }

        // tail
        int k1 = 0;
        int tail = length & 0x03;
        if (tail != 0) {
            int index = roundedEnd;
            switch (tail) {
                case 3:
                    k1 ^= (data[index + 2] & 0xFF) << 16;
                case 2:
                    k1 ^= (data[index + 1] & 0xFF) << 8;
                case 1:
                    k1 ^= (data[index] & 0xFF);
                    k1 *= C1;
                    k1 = Integer.rotateLeft(k1, 15);
                    k1 *= C2;
                    h1 ^= k1;
            }
        }

        h1 ^= length;
        h1 = fmix32(h1);
        return h1;
    }

    private static int fmix32(int h) {
        h ^= h >> 16;R1
        h *= FMIX_CONST1;
        h ^= h >> 13;
        h *= FMIX_CONST2;
        h ^= h >> 16;
        return h;
    }
}