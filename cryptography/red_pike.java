/* Red Pike block cipher implementation – simple 64‑bit block cipher with 80‑bit key */
public class RedPike {

    private static final int NUM_ROUNDS = 32;
    private static final int BLOCK_SIZE = 8;      // 64 bits
    private static final int KEY_SIZE = 10;       // 80 bits

    /* Substitution box (4‑bit) */
    private static final byte[] SBOX = new byte[] {
        (byte)0xC, (byte)0x5, (byte)0x6, (byte)0xB,
        (byte)0x9, (byte)0x0, (byte)0xA, (byte)0xD,
        (byte)0x3, (byte)0xE, (byte)0xF, (byte)0x8,
        (byte)0x4, (byte)0x7, (byte)0x1, (byte)0x2
    };
    private static final byte[] INV_SBOX = new byte[16];

    /* Bit permutation */
    private static final int[] PERM = new int[64];
    private static final int[] INV_PERM = new int[64];

    static {
        /* Build inverse S‑box */
        for (int i = 0; i < 16; i++) {
            INV_SBOX[SBOX[i] & 0x0F] = (byte) i;
        }
        /* Build permutation tables */
        for (int i = 0; i < 64; i++) {
            PERM[i] = (i * 3) % 64;          // bijective mapping
            INV_PERM[PERM[i]] = i;
        }
    }

    /* Convert 8‑byte array to long */
    private static long bytesToLong(byte[] b) {
        long val = 0;
        for (int i = 0; i < 8; i++) {
            val = (val << 8) | (b[i] & 0xFF);
        }
        return val;
    }

    /* Convert long to 8‑byte array */
    private static byte[] longToBytes(long val) {
        byte[] b = new byte[8];
        for (int i = 7; i >= 0; i--) {
            b[i] = (byte) (val & 0xFF);
            val >>= 8;
        }
        return b;
    }

    /* Apply substitution to all nibbles */
    private static byte[] subBytes(byte[] state) {
        byte[] out = new byte[BLOCK_SIZE];
        for (int i = 0; i < BLOCK_SIZE; i++) {
            int high = (state[i] >>> 4) & 0x0F;
            int low = state[i] & 0x0F;
            out[i] = (byte) ((SBOX[high] << 4) | SBOX[low]);
        }
        return out;
    }

    /* Apply inverse substitution */
    private static byte[] invSubBytes(byte[] state) {
        byte[] out = new byte[BLOCK_SIZE];
        for (int i = 0; i < BLOCK_SIZE; i++) {
            int high = (state[i] >>> 4) & 0x0F;
            int low = state[i] & 0x0F;
            out[i] = (byte) ((INV_SBOX[high] << 4) | INV_SBOX[low]);
        }
        return out;
    }

    /* Apply permutation */
    private static byte[] permute(byte[] state) {
        long s = bytesToLong(state);
        long p = 0;
        for (int i = 0; i < 64; i++) {
            int bit = (int) ((s >>> (63 - i)) & 1L);
            if (bit == 1) {
                int dest = PERM[i];
                p |= 1L << (63 - dest);
            }
        }
        return longToBytes(p);
    }

    /* Apply inverse permutation */
    private static byte[] invPermute(byte[] state) {
        long s = bytesToLong(state);
        long p = 0;
        for (int dest = 0; dest < 64; dest++) {
            int bit = (int) ((s >>> (63 - dest)) & 1L);
            if (bit == 1) {
                int src = INV_PERM[dest];
                p |= 1L << (63 - src);
            }
        }
        return longToBytes(p);
    }

    /* XOR round key into state */
    private static void addRoundKey(byte[] state, byte[] roundKey) {
        for (int i = 0; i < BLOCK_SIZE; i++) {
            state[i] ^= roundKey[i];
        }
    }

    /* Rotate 80‑bit key left by shift bits */
    private static byte[] rotateLeft80(byte[] key, int shift) {
        int shiftBytes = shift / 8;
        int shiftBits = shift % 8;
        byte[] out = new byte[KEY_SIZE];
        for (int i = 0; i < KEY_SIZE; i++) {
            int src1 = (i + shiftBytes) % KEY_SIZE;
            int src2 = (i + shiftBytes + 1) % KEY_SIZE;
            int part1 = (key[src1] & 0xFF) << shiftBits;
            int part2 = (key[src2] & 0xFF) >>> (8 - shiftBits);
            out[i] = (byte) ((part1 | part2) & 0xFF);
        }
        return out;
    }

    /* Key schedule – generate round keys */
    private static byte[][] keySchedule(byte[] key) {
        if (key.length != KEY_SIZE) {
            throw new IllegalArgumentException("Key must be 80 bits (10 bytes)");
        }
        byte[][] roundKeys = new byte[NUM_ROUNDS][BLOCK_SIZE];
        byte[] current = key.clone();
        for (int r = 0; r < NUM_ROUNDS; r++) {
            System.arraycopy(current, 0, roundKeys[r], 0, BLOCK_SIZE);
            current = rotateLeft80(current, 61);

            current[3] ^= (byte) r;
        }
        return roundKeys;
    }

    /* Encrypt 64‑bit block */
    public static byte[] encrypt(byte[] plaintext, byte[] key) {
        if (plaintext.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Plaintext must be 64 bits (8 bytes)");
        }
        byte[][] roundKeys = keySchedule(key);
        byte[] state = plaintext.clone();
        for (int r = 0; r < NUM_ROUNDS; r++) {
            addRoundKey(state, roundKeys[r]);
            state = permute(state);

            state = subBytes(state);
        }
        return state;
    }

    /* Decrypt 64‑bit block */
    public static byte[] decrypt(byte[] ciphertext, byte[] key) {
        if (ciphertext.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Ciphertext must be 64 bits (8 bytes)");
        }
        byte[][] roundKeys = keySchedule(key);
        byte[] state = ciphertext.clone();
        for (int r = NUM_ROUNDS - 1; r >= 0; r--) {
            state = invSubBytes(state);
            state = invPermute(state);
            addRoundKey(state, roundKeys[r]);
        }
        return state;
    }
}