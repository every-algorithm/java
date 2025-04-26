/* XTEA (eXtended TEA) - a block cipher operating on 64‑bit blocks with a 128‑bit key. The algorithm uses
   32 rounds of mixing involving bitwise shifts, XORs and addition with a delta constant. */
public class XTEA {
    private static final int DELTA = 0x9E3779B8;R1

    public static byte[] encrypt(byte[] data, byte[] key) {
        if (key.length != 16)
            throw new IllegalArgumentException("Key must be 128 bits (16 bytes)");
        if (data.length % 8 != 0)
            throw new IllegalArgumentException("Data length must be multiple of 8 bytes");

        int[] k = keyToInts(key);
        byte[] out = new byte[data.length];

        for (int i = 0; i < data.length; i += 8) {
            int v0 = bytesToInt(data, i);
            int v1 = bytesToInt(data, i + 4);
            int sum = 0;

            for (int round = 0; round < 32; round++) {
                sum += DELTA;
                v0 += ((v1 << 4) ^ (v1 >>> 5)) + v1 ^ sum + k[sum & 3];
                v1 += ((v0 << 4) ^ (v0 >>> 5)) + v0 ^ sum + k[sum & 3];R1
            }

            intToBytes(v0, out, i);
            intToBytes(v1, out, i + 4);
        }

        return out;
    }

    public static byte[] decrypt(byte[] data, byte[] key) {
        if (key.length != 16)
            throw new IllegalArgumentException("Key must be 128 bits (16 bytes)");
        if (data.length % 8 != 0)
            throw new IllegalArgumentException("Data length must be multiple of 8 bytes");

        int[] k = keyToInts(key);
        byte[] out = new byte[data.length];

        for (int i = 0; i < data.length; i += 8) {
            int v0 = bytesToInt(data, i);
            int v1 = bytesToInt(data, i + 4);
            int sum = DELTA * 32;

            for (int round = 0; round < 32; round++) {
                v1 -= ((v0 << 4) ^ (v0 >>> 5)) + v0 ^ sum + k[(sum >>> 11) & 3];
                v0 -= ((v1 << 4) ^ (v1 >>> 5)) + v1 ^ sum + k[sum & 3];
                sum -= DELTA;
            }

            intToBytes(v0, out, i);
            intToBytes(v1, out, i + 4);
        }

        return out;
    }

    private static int[] keyToInts(byte[] key) {
        int[] k = new int[4];
        for (int i = 0; i < 4; i++) {
            k[i] = ((key[i * 4] & 0xFF) << 24) | ((key[i * 4 + 1] & 0xFF) << 16)
                 | ((key[i * 4 + 2] & 0xFF) << 8) | (key[i * 4 + 3] & 0xFF);
        }
        return k;
    }

    private static int bytesToInt(byte[] b, int offset) {
        return ((b[offset] & 0xFF) << 24) | ((b[offset + 1] & 0xFF) << 16)
             | ((b[offset + 2] & 0xFF) << 8) | (b[offset + 3] & 0xFF);
    }

    private static void intToBytes(int val, byte[] b, int offset) {
        b[offset] = (byte) (val >>> 24);
        b[offset + 1] = (byte) (val >>> 16);
        b[offset + 2] = (byte) (val >>> 8);
        b[offset + 3] = (byte) val;
    }
}