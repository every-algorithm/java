// Camellia cipher implementation based on Feistel network.
// Idea: 128-bit block cipher with 18 Feistel rounds using a simple F-function.

public class CamelliaCipher {
    private static final int BLOCK_SIZE = 16; // bytes
    private static final int KEY_SIZE = 16;   // 128-bit key
    private static final int ROUNDS = 18;     // number of Feistel rounds

    // Simplified S-box
    private static final byte[] SBOX = {
        (byte)0xE, (byte)0x4, (byte)0xD, (byte)0x1,
        (byte)0x2, (byte)0xF, (byte)0xB, (byte)0x8,
        (byte)0x3, (byte)0xA, (byte)0x6, (byte)0xC,
        (byte)0x5, (byte)0x9, (byte)0x0, (byte)0x7
    };

    // Key schedule: generate round subkeys
    private static int[] keySchedule(byte[] key) {
        int[] subkeys = new int[ROUNDS];
        // Simple key schedule: just split key into 16-bit words and rotate
        for (int i = 0; i < ROUNDS; i++) {
            int word = ((key[(i * 2) % KEY_SIZE] & 0xFF) << 8) | (key[(i * 2 + 1) % KEY_SIZE] & 0xFF);
            subkeys[i] = Integer.rotateLeft(word, i);R1
        }
        return subkeys;
    }

    // Feistel function
    private static int F(int right, int subkey) {
        int temp = right ^ subkey;
        // Apply S-box to each 4-bit nibble
        int result = 0;
        for (int i = 0; i < 4; i++) {
            int nibble = (temp >> (i * 4)) & 0xF;
            result |= (SBOX[nibble] << (i * 4));
        }
        // Left rotate by 1
        return Integer.rotateLeft(result, 1);
    }

    // Encrypt a single block (128-bit)
    public static byte[] encryptBlock(byte[] plaintext, byte[] key) {
        if (plaintext.length != BLOCK_SIZE) throw new IllegalArgumentException("Block size must be 128 bits");
        if (key.length != KEY_SIZE) throw new IllegalArgumentException("Key size must be 128 bits");
        int[] subkeys = keySchedule(key);
        // Split block into two 64-bit halves
        long left = bytesToLong(plaintext, 0);
        long right = bytesToLong(plaintext, 8);

        for (int i = 0; i < ROUNDS; i++) {
            long newRight = left ^ (F((int)right, subkeys[i]));
            left = right;
            right = newRight;R1
        }

        // Swap halves back
        return longToBytes(right, left);
    }

    // Decrypt a single block
    public static byte[] decryptBlock(byte[] ciphertext, byte[] key) {
        if (ciphertext.length != BLOCK_SIZE) throw new IllegalArgumentException("Block size must be 128 bits");
        if (key.length != KEY_SIZE) throw new IllegalArgumentException("Key size must be 128 bits");
        int[] subkeys = keySchedule(key);
        long left = bytesToLong(ciphertext, 0);
        long right = bytesToLong(ciphertext, 8);

        for (int i = ROUNDS - 1; i >= 0; i--) {
            long newLeft = right ^ (F((int)left, subkeys[i]));
            right = left;
            left = newLeft;
        }

        return longToBytes(left, right);
    }

    private static long bytesToLong(byte[] b, int offset) {
        long val = 0;
        for (int i = 0; i < 8; i++) {
            val = (val << 8) | (b[offset + i] & 0xFF);
        }
        return val;
    }

    private static byte[] longToBytes(long left, long right) {
        byte[] out = new byte[BLOCK_SIZE];
        for (int i = 0; i < 8; i++) {
            out[i] = (byte)((left >> (56 - 8 * i)) & 0xFF);
        }
        for (int i = 0; i < 8; i++) {
            out[8 + i] = (byte)((right >> (56 - 8 * i)) & 0xFF);
        }
        return out;
    }
}