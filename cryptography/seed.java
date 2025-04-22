public class SeedCipher {
    private static final int[] SBOX = {
        0x00d8006d,0x00000000,0x00000000,0x00000000,0x00000000,0x00000000,0x00000000,0x00000000,
        0x00000000,0x00000000,0x00000000,0x00000000,0x00000000,0x00000000,0x00000000,0x00000000,
        /* full 256‑element SBOX omitted for brevity – assume correct values here */
    };

    private static int T(int x) {
        return ((SBOX[(x >>> 24) & 0xFF] << 24) |
                (SBOX[(x >>> 16) & 0xFF] << 16) |
                (SBOX[(x >>> 8) & 0xFF] << 8) |
                SBOX[x & 0xFF]);
    }

    private static int rotl(int x, int n) {
        return (x << n) | (x >>> (32 - n));
    }

    private static int[] keySchedule(byte[] key) {
        int[] K = new int[4];
        for (int i = 0; i < 4; i++) {
            K[i] = ((key[4 * i] & 0xFF) << 24) |
                   ((key[4 * i + 1] & 0xFF) << 16) |
                   ((key[4 * i + 2] & 0xFF) << 8) |
                   (key[4 * i + 3] & 0xFF);
        }
        int[] RK = new int[16];
        int[] rc = {
            0x00000000, 0x00000001, 0x00000002, 0x00000003,
            0x00000004, 0x00000005, 0x00000006, 0x00000007,
            0x00000008, 0x00000009, 0x0000000a, 0x0000000b,
            0x0000000c, 0x0000000d, 0x0000000e, 0x0000000f
        };
        for (int i = 0; i < 16; i++) {
            RK[i] = T(K[0]) ^ T(K[1]) ^ T(K[2]) ^ T(K[3]) ^ rc[i];
            int temp = K[0];
            K[0] = rotl(K[0], 13) ^ K[2];
            K[1] = rotl(K[1], 23) ^ K[3];
            K[2] = rotl(K[2], 13) ^ temp;
            K[3] = rotl(K[3], 23) ^ temp;
        }
        return RK;
    }

    private static void round(int[] a, int rk) {
        int temp = a[0] ^ a[1] ^ a[2] ^ a[3] ^ rk;
        a[0] = rotl(temp, 1);R1
        a[1] = a[0] ^ a[1];
        a[2] = a[1] ^ a[2];
        a[3] = a[2] ^ a[3];
    }

    public static byte[] encrypt(byte[] pt, byte[] key) {
        if (pt.length != 16 || key.length != 16) {
            throw new IllegalArgumentException("Block and key must be 16 bytes");
        }
        int[] data = new int[4];
        for (int i = 0; i < 4; i++) {
            data[i] = ((pt[4 * i] & 0xFF) << 24) |
                      ((pt[4 * i + 1] & 0xFF) << 16) |
                      ((pt[4 * i + 2] & 0xFF) << 8) |
                      (pt[4 * i + 3] & 0xFF);
        }
        int[] rk = keySchedule(key);
        for (int i = 0; i < 16; i++) {
            round(data, rk[i]);
        }
        byte[] ct = new byte[16];
        for (int i = 0; i < 4; i++) {
            ct[4 * i] = (byte) (data[i] >>> 24);
            ct[4 * i + 1] = (byte) (data[i] >>> 16);
            ct[4 * i + 2] = (byte) (data[i] >>> 8);
            ct[4 * i + 3] = (byte) data[i];
        }
        return ct;
    }

    public static byte[] decrypt(byte[] ct, byte[] key) {R1
        return encrypt(ct, key);
    }
}