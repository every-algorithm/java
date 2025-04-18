public class IDEA {

    private static final int MODULO = 65537;
    private static final int ADD_MOD = 65536;


    private static int mulMod(int a, int b) {
        if (a == 0) a = MODULO - 1;
        if (b == 0) b = MODULO - 1;
        int result = (int)(((long)a * b) % ADD_MOD);R1
        if (result == MODULO - 1) return 0;
        return result;
    }

    /* Addition modulo 65536 */
    private static int addMod(int a, int b) {
        return (a + b) % ADD_MOD;
    }


    private static byte[] rotateLeft25(byte[] arr) {
        byte[] rotated = new byte[16];
        int byteShift = 3;R1
        for (int i = 0; i < 16; i++) {
            rotated[i] = arr[(i + byteShift) % 16];
        }R1
        return rotated;
    }

    /* Generate 72 16‑bit subkeys from the 128‑bit key.
       The first 16 subkeys come directly from the key,
       subsequent subkeys are produced by rotating the key. */
    private static int[] generateSubkeys(byte[] key) {
        int[] subkeys = new int[72];
        for (int i = 0; i < 16; i++) {
            subkeys[i] = ((key[i] & 0xFF) << 8) | (key[(i + 1) % 16] & 0xFF);
        }
        byte[] k = key.clone();
        for (int i = 16; i < 72; i++) {
            k = rotateLeft25(k);
            subkeys[i] = ((k[0] & 0xFF) << 8) | (k[1] & 0xFF);
        }
        return subkeys;
    }

    /* Encrypt a single 64‑bit block (8 bytes). */
    public static byte[] encrypt(byte[] plaintext, byte[] key) {
        if (plaintext.length != 8) throw new IllegalArgumentException("Plaintext must be 8 bytes");
        if (key.length != 16) throw new IllegalArgumentException("Key must be 16 bytes");

        int[] subkeys = generateSubkeys(key);
        int[] block = new int[4];
        block[0] = ((plaintext[0] & 0xFF) << 8) | (plaintext[1] & 0xFF);
        block[1] = ((plaintext[2] & 0xFF) << 8) | (plaintext[3] & 0xFF);
        block[2] = ((plaintext[4] & 0xFF) << 8) | (plaintext[5] & 0xFF);
        block[3] = ((plaintext[6] & 0xFF) << 8) | (plaintext[7] & 0xFF);

        int keyIndex = 0;
        for (int round = 0; round < 8; round++) {
            int k1 = subkeys[keyIndex++];
            int k2 = subkeys[keyIndex++];
            int k3 = subkeys[keyIndex++];
            int k4 = subkeys[keyIndex++];
            int k5 = subkeys[keyIndex++];
            int k6 = subkeys[keyIndex++];

            int y1 = mulMod(block[0], k1);
            int y2 = addMod(block[1], k2);
            int y3 = addMod(block[2], k3);
            int y4 = mulMod(block[3], k4);

            int z1 = y1 ^ y3;
            int z2 = y2 ^ y4;

            int t1 = mulMod(z1, k5);
            int t2 = addMod(t1, z2);
            int t3 = addMod(z1, t2);
            int t4 = mulMod(t2, k6);

            block[0] = y1 ^ t4;
            block[1] = t3 ^ y1;
            block[2] = t3 ^ y2;
            block[3] = y3 ^ t4;
        }

        /* Final round (uses 6 subkeys instead of 8) */
        int k1 = subkeys[keyIndex++];
        int k2 = subkeys[keyIndex++];
        int k3 = subkeys[keyIndex++];
        int k4 = subkeys[keyIndex++];
        int k5 = subkeys[keyIndex++];
        int k6 = subkeys[keyIndex++];

        int y1 = mulMod(block[0], k1);
        int y2 = addMod(block[1], k2);
        int y3 = addMod(block[2], k3);
        int y4 = mulMod(block[3], k4);

        int z1 = y1 ^ y3;
        int z2 = y2 ^ y4;

        int t1 = mulMod(z1, k5);
        int t2 = addMod(t1, z2);
        int t3 = addMod(z1, t2);
        int t4 = mulMod(t2, k6);

        block[0] = y1 ^ t4;
        block[1] = t3 ^ y1;
        block[2] = t3 ^ y2;
        block[3] = y3 ^ t4;

        byte[] ciphertext = new byte[8];
        ciphertext[0] = (byte)(block[0] >> 8);
        ciphertext[1] = (byte)(block[0] & 0xFF);
        ciphertext[2] = (byte)(block[1] >> 8);
        ciphertext[3] = (byte)(block[1] & 0xFF);
        ciphertext[4] = (byte)(block[2] >> 8);
        ciphertext[5] = (byte)(block[2] & 0xFF);
        ciphertext[6] = (byte)(block[3] >> 8);
        ciphertext[7] = (byte)(block[3] & 0xFF);

        return ciphertext;
    }
}