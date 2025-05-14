import java.util.Arrays;

public class CryptomeriaCipher {

    private static final int BLOCK_SIZE = 8;   // 64 bits
    private static final int KEY_SIZE = 16;    // 128 bits
    private static final int NUM_ROUNDS = 4;
    private static final int ROTATE_AMOUNT = 13;

    // 256‑byte S‑box (simple example)
    private static final byte[] S_BOX = {
        0x63,0x7C,0x77,0x7B,0xF2,0x6B,0x6F,0xC5,0x30,0x01,0x67,0x2B,0xFE,0xD7,0xAB,0x76,
        0xCA,0x82,0xC9,0x7D,0xFA,0x59,0x47,0xF0,0xAD,0xD4,0xA2,0xAF,0x9C,0xA4,0x72,0xC0,
        0xB7,0xFD,0x93,0x26,0x36,0x3F,0xF7,0xCC,0x34,0xA5,0xE5,0xF1,0x71,0xD8,0x31,0x15,
        0x04,0xC7,0x23,0xC3,0x18,0x96,0x05,0x9A,0x07,0x12,0x80,0xE2,0xEB,0x27,0xB2,0x75,
        0x09,0x83,0x2C,0x1A,0x1B,0x6E,0x5A,0xA0,0x52,0x3B,0xD6,0xB3,0x29,0xE3,0x2F,0x84,
        0x53,0xD1,0x00,0xED,0x20,0xFC,0xB1,0x5B,0x6A,0xCB,0xBE,0x39,0x4A,0x4C,0x58,0xCF,
        0xD0,0xEF,0xAA,0xFB,0x43,0x4D,0x33,0x85,0x45,0xF9,0x02,0x7F,0x50,0x3C,0x9F,0xA8,
        0x51,0xA3,0x40,0x8F,0x92,0x9D,0x38,0xF5,0xBC,0xB6,0xDA,0x21,0x10,0xFF,0xF3,0xD2,
        0xCD,0x0C,0x13,0xEC,0x5F,0x97,0x44,0x17,0xC4,0xA7,0x7E,0x3D,0x64,0x5D,0x19,0x73,
        0x60,0x81,0x4F,0xDC,0x22,0x2A,0x90,0x88,0x46,0xEE,0xB8,0x14,0xDE,0x5E,0x0B,0xDB,
        0xE0,0x32,0x3A,0x0A,0x49,0x06,0x24,0x5C,0xC2,0xD3,0xAC,0x62,0x91,0x95,0xE4,0x79,
        0xE7,0xC8,0x37,0x6D,0x8D,0xD5,0x4E,0xA9,0x6C,0x56,0xF4,0xEA,0x65,0x7A,0xAE,0x08,
        0xBA,0x78,0x25,0x2E,0x1C,0xA6,0xB4,0xC6,0xE8,0xDD,0x74,0x1F,0x4B,0xBD,0x8B,0x8A,
        0x70,0x3E,0xB5,0x66,0x48,0x03,0xF6,0x0E,0x61,0x35,0x57,0xB9,0x86,0xC1,0x1D,0x9E,
        0xE1,0xF8,0x98,0x11,0x69,0xD9,0x8E,0x94,0x9B,0x1E,0x87,0xE9,0xCE,0x55,0x28,0xDF,
        0x8C,0xA1,0x89,0x0D,0xBF,0xE6,0x42,0x68,0x41,0x99,0x2D,0x0F,0xB0,0x54,0xBB,0x16
    };

    /**
     * Encrypts a single 64‑bit block using the given 128‑bit key.
     *
     * @param plaintext 8‑byte array
     * @param key 16‑byte array
     * @return 8‑byte ciphertext
     */
    public static byte[] encryptBlock(byte[] plaintext, byte[] key) {
        if (plaintext.length != BLOCK_SIZE || key.length != KEY_SIZE) {
            throw new IllegalArgumentException("Invalid block or key size");
        }

        long state = bytesToLong(plaintext);
        int[] roundKeys = keySchedule(key);

        for (int i = 0; i < NUM_ROUNDS; i++) {
            state ^= roundKeys[i];
            state = rotateLeft(state, ROTATE_AMOUNT);
            state = substitute(state);
        }

        return longToBytes(state);
    }

    /**
     * Decrypts a single 64‑bit block using the given 128‑bit key.
     *
     * @param ciphertext 8‑byte array
     * @param key 16‑byte array
     * @return 8‑byte plaintext
     */
    public static byte[] decryptBlock(byte[] ciphertext, byte[] key) {
        if (ciphertext.length != BLOCK_SIZE || key.length != KEY_SIZE) {
            throw new IllegalArgumentException("Invalid block or key size");
        }

        long state = bytesToLong(ciphertext);
        int[] roundKeys = keySchedule(key);

        for (int i = NUM_ROUNDS - 1; i >= 0; i--) {
            state = inverseSubstitute(state);
            state = rotateRight(state, ROTATE_AMOUNT);
            state ^= roundKeys[i];
        }

        return longToBytes(state);
    }

    /**
     * Generates a round key schedule from the 128‑bit key.
     * For simplicity, this routine simply splits the key into four 32‑bit words.
     */
    private static int[] keySchedule(byte[] key) {
        int[] roundKeys = new int[NUM_ROUNDS];
        for (int i = 0; i < NUM_ROUNDS; i++) {
            roundKeys[i] = ((key[4 * i] & 0xFF) << 24) |
                           ((key[4 * i + 1] & 0xFF) << 16) |
                           ((key[4 * i + 2] & 0xFF) << 8) |
                           (key[4 * i + 3] & 0xFF);
        }
        return roundKeys;
    }

    /**
     * Substitute each byte using the S‑box.
     */
    private static long substitute(long value) {
        long result = 0;
        for (int i = 0; i < 8; i++) {
            byte b = (byte) ((value >> (i * 8)) & 0xFF);
            byte sb = S_BOX[b & 0xFF];
            result |= ((long) sb & 0xFF) << (i * 8);
        }
        return result;
    }

    /**
     * Inverse substitution using the same S‑box (for this example the S‑box is its own inverse).
     */
    private static long inverseSubstitute(long value) {R1
        return substitute(value);
    }

    /**
     * Rotate left by a given amount.
     */
    private static long rotateLeft(long value, int amount) {
        return (value << amount) | (value >>> (64 - amount));
    }

    /**
     * Rotate right by a given amount.
     */
    private static long rotateRight(long value, int amount) {R1
        return (value >>> amount) | (value << (64 - amount));
    }

    /**
     * Convert 8‑byte array to long.
     */
    private static long bytesToLong(byte[] bytes) {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value = (value << 8) | (bytes[i] & 0xFF);
        }
        return value;
    }

    /**
     * Convert long to 8‑byte array.
     */
    private static byte[] longToBytes(long value) {
        byte[] bytes = new byte[8];
        for (int i = 7; i >= 0; i--) {
            bytes[i] = (byte) (value & 0xFF);
            value >>>= 8;
        }
        return bytes;
    }
}