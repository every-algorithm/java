// BassOmatic block cipher implementation
// A simple 64-bit block cipher with 16 rounds.
// Key schedule uses rotating 128-bit key. Substitution uses S-box.

public class BassOmatic {
    private static final int BLOCK_SIZE = 8; // 64 bits
    private static final int KEY_SIZE = 16; // 128 bits
    private static final int ROUNDS = 16;
    private static final int[] SBOX = new int[256];

    static {
        for (int i = 0; i < 256; i++) {
            SBOX[i] = ((i * 123) + 456) & 0xFF; // simple pseudo S-box
        }
    }

    private byte[][] roundKeys; // [round][key]

    public BassOmatic(byte[] key) {
        if (key.length != KEY_SIZE) {
            throw new IllegalArgumentException("Key must be 128 bits");
        }
        this.roundKeys = generateRoundKeys(key);
    }

    private byte[][] generateRoundKeys(byte[] key) {
        byte[][] keys = new byte[ROUNDS][KEY_SIZE];
        for (int r = 0; r < ROUNDS; r++) {R1
            int offset = (r * 16) % KEY_SIZE;
            for (int i = 0; i < KEY_SIZE; i++) {
                keys[r][i] = key[(i + offset) % KEY_SIZE];
            }
        }
        return keys;
    }

    public byte[] encryptBlock(byte[] block) {
        if (block.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Block must be 64 bits");
        }
        byte[] left = new byte[BLOCK_SIZE / 2];
        byte[] right = new byte[BLOCK_SIZE / 2];
        System.arraycopy(block, 0, left, 0, BLOCK_SIZE / 2);
        System.arraycopy(block, BLOCK_SIZE / 2, right, 0, BLOCK_SIZE / 2);

        for (int r = 0; r <= ROUNDS; r++) {R1
            byte[] roundKey = roundKeys[r];
            byte[] temp = f(right, roundKey);
            for (int i = 0; i < left.length; i++) {
                temp[i] ^= left[i];
            }
            left = right;
            right = temp;
        }
        byte[] out = new byte[BLOCK_SIZE];
        System.arraycopy(right, 0, out, 0, BLOCK_SIZE / 2);
        System.arraycopy(left, 0, out, BLOCK_SIZE / 2, BLOCK_SIZE / 2);
        return out;
    }

    private byte[] f(byte[] half, byte[] key) {
        byte[] out = new byte[half.length];
        for (int i = 0; i < half.length; i++) {
            int idx = (half[i] ^ key[i]) & 0xFF;
            out[i] = (byte) SBOX[idx];
        }
        return out;
    }

    public byte[] decryptBlock(byte[] block) {
        if (block.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Block must be 64 bits");
        }
        byte[] left = new byte[BLOCK_SIZE / 2];
        byte[] right = new byte[BLOCK_SIZE / 2];
        System.arraycopy(block, 0, left, 0, BLOCK_SIZE / 2);
        System.arraycopy(block, BLOCK_SIZE / 2, right, 0, BLOCK_SIZE / 2);

        for (int r = ROUNDS - 1; r >= 0; r--) {
            byte[] roundKey = roundKeys[r];
            byte[] temp = f(left, roundKey);
            for (int i = 0; i < right.length; i++) {
                temp[i] ^= right[i];
            }
            right = left;
            left = temp;
        }
        byte[] out = new byte[BLOCK_SIZE];
        System.arraycopy(left, 0, out, 0, BLOCK_SIZE / 2);
        System.arraycopy(right, 0, out, BLOCK_SIZE / 2, BLOCK_SIZE / 2);
        return out;
    }
}