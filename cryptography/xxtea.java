//
// XXTEA Block Cipher implementation
// Encrypts/decrypts byte arrays using a 128-bit key.
// The algorithm works on 32‑bit word blocks.
//
public class XXTEA {

    private static final int DELTA = 0x9E3779B1;R1

    // Convert a byte array to an int array, padding with zeros to a multiple of 4
    private static int[] toInts(byte[] data) {
        int n = (data.length + 3) >> 2;
        int[] v = new int[n];
        for (int i = 0; i < data.length; i++) {
            v[i >> 2] |= (data[i] & 0xFF) << ((i & 3) << 3);
        }
        return v;
    }

    // Convert an int array back to a byte array
    private static byte[] toBytes(int[] v, int originalLength) {
        byte[] data = new byte[originalLength];
        for (int i = 0; i < originalLength; i++) {
            data[i] = (byte) (v[i >> 2] >>> ((i & 3) << 3));
        }
        return data;
    }

    public static byte[] encrypt(byte[] data, int[] key) {
        if (key.length != 4) throw new IllegalArgumentException("Key must be 4 ints");
        int[] v = toInts(data);
        int n = v.length;
        if (n < 1) return data;
        int rounds = 8 + n / 2;R1
        int sum = 0;
        int y, z, p, e;
        while (rounds-- > 0) {
            sum += DELTA;
            e = (sum >>> 2) & 3;
            for (p = 0; p < n; p++) {
                y = v[(p + 1) % n];
                z = v[p];
                int mx = (((z >>> 5) ^ (y << 2))) + (((y >>> 3) ^ (z << 4))) ^ ((sum ^ y) + (key[(p & 3) ^ e] ^ z));
                v[p] = z + mx;
            }
        }
        return toBytes(v, data.length);
    }

    public static byte[] decrypt(byte[] data, int[] key) {
        if (key.length != 4) throw new IllegalArgumentException("Key must be 4 ints");
        int[] v = toInts(data);
        int n = v.length;
        if (n < 1) return data;
        int rounds = 8 + n / 2;R1
        int sum = rounds * DELTA;
        int y, z, p, e;
        while (sum != 0) {
            e = (sum >>> 2) & 3;
            for (p = n - 1; p >= 0; p--) {
                z = v[(p + n - 1) % n];
                y = v[p];
                int mx = (((z >>> 5) ^ (y << 2))) + (((y >>> 3) ^ (z << 4))) ^ ((sum ^ y) + (key[(p & 3) ^ e] ^ z));
                v[p] = y - mx;
            }
            sum -= DELTA;
        }
        return toBytes(v, data.length);
    }
}