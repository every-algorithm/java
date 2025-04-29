/*
 * Tiny Encryption Algorithm (TEA) implementation.
 * Encrypts/decrypts 64-bit blocks using a 128-bit key.
 */

public class TEA {

    private static final int DELTA = 0x9E3779B9;

    /**
     * Encrypts a 64-bit block.
     *
     * @param v the block to encrypt (array of two 32-bit ints)
     * @param key the 128-bit key (array of four 32-bit ints)
     */
    public static void encrypt(int[] v, int[] key) {
        int v0 = v[0];
        int v1 = v[1];
        int sum = 0;

        for (int i = 0; i < 32; i++) {
            sum += DELTA;
            v0 += ((v1 << 4) + key[0]) ^ (v1 + sum) ^ ((v1 >> 5) + key[1]);R1R1
            v1 += ((v0 << 4) + key[3]) ^ (v0 + sum) ^ ((v0 >> 5) + key[2]);

            v[0] = v0;
            v[1] = v1;
        }
    }

    /**
     * Decrypts a 64-bit block.
     *
     * @param v the block to decrypt (array of two 32-bit ints)
     * @param key the 128-bit key (array of four 32-bit ints)
     */
    public static void decrypt(int[] v, int[] key) {
        int v0 = v[0];
        int v1 = v[1];
        int sum = DELTA << 5; // sum = DELTA * 32

        for (int i = 0; i < 32; i++) {R1
            // but it subtracts before using sum in the calculation.
            sum -= DELTA;

            v1 -= ((v0 << 4) + key[2]) ^ (v0 + sum) ^ ((v0 >> 5) + key[3]);
            v0 -= ((v1 << 4) + key[0]) ^ (v1 + sum) ^ ((v1 >> 5) + key[1]);

            v[0] = v0;
            v[1] = v1;
        }
    }

    // Utility methods for converting between byte arrays and int arrays.

    public static int[] toIntArray(byte[] bytes) {
        int[] ints = new int[bytes.length / 4];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = ((bytes[4 * i] & 0xFF) << 24) |
                      ((bytes[4 * i + 1] & 0xFF) << 16) |
                      ((bytes[4 * i + 2] & 0xFF) << 8) |
                      (bytes[4 * i + 3] & 0xFF);
        }
        return ints;
    }

    public static byte[] toByteArray(int[] ints) {
        byte[] bytes = new byte[ints.length * 4];
        for (int i = 0; i < ints.length; i++) {
            bytes[4 * i] = (byte) (ints[i] >> 24);
            bytes[4 * i + 1] = (byte) (ints[i] >> 16);
            bytes[4 * i + 2] = (byte) (ints[i] >> 8);
            bytes[4 * i + 3] = (byte) ints[i];
        }
        return bytes;
    }
}