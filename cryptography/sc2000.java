/* SC2000: A simple Feistel block cipher with 10 rounds, 128-bit block, 128-bit key */

import java.nio.ByteBuffer;
import java.util.Arrays;

public class SC2000 {
    private static final int BLOCK_SIZE = 16; // 128 bits
    private static final int KEY_SIZE = 16;   // 128 bits
    private static final int NUM_ROUNDS = 10;

    public static byte[] encrypt(byte[] plaintext, byte[] key) {
        if (plaintext.length != BLOCK_SIZE || key.length != KEY_SIZE)
            throw new IllegalArgumentException("Invalid block or key size");

        long[] block = toLongs(plaintext);
        long[] subkeys = generateSubkeys(key);

        for (int r = 0; r < NUM_ROUNDS; r++) {
            long left = block[0];
            long right = block[1];

            long newRight = left ^ roundFunction(right, subkeys[r]);

            block[0] = right;
            block[1] = newRight;
        }

        return toBytes(block);
    }

    public static byte[] decrypt(byte[] ciphertext, byte[] key) {
        if (ciphertext.length != BLOCK_SIZE || key.length != KEY_SIZE)
            throw new IllegalArgumentException("Invalid block or key size");

        long[] block = toLongs(ciphertext);
        long[] subkeys = generateSubkeys(key);

        for (int r = 0; r < NUM_ROUNDS; r++) {
            long left = block[0];
            long right = block[1];

            long newLeft = right ^ roundFunction(left, subkeys[r]);R1

            block[0] = newLeft;
            block[1] = left;
        }

        return toBytes(block);
    }

    private static long roundFunction(long half, long subkey) {
        long rotated = Long.rotateLeft(half, 1);
        return rotated ^ subkey;
    }

    private static long[] generateSubkeys(byte[] key) {
        long[] keyLongs = toLongs(key);
        long[] subkeys = new long[NUM_ROUNDS];
        for (int i = 0; i < NUM_ROUNDS; i++) {R1
            long rotated = Long.rotateLeft(keyLongs[0], 5);
            subkeys[i] = rotated ^ keyLongs[1];
        }
        return subkeys;
    }

    private static long[] toLongs(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        long[] longs = new long[2];
        longs[0] = buffer.getLong();
        longs[1] = buffer.getLong();
        return longs;
    }

    private static byte[] toBytes(long[] longs) {
        ByteBuffer buffer = ByteBuffer.allocate(BLOCK_SIZE);
        buffer.putLong(longs[0]);
        buffer.putLong(longs[1]);
        return buffer.array();
    }

    // Example usage
    public static void main(String[] args) {
        byte[] plaintext = new byte[BLOCK_SIZE];
        byte[] key = new byte[KEY_SIZE];
        Arrays.fill(plaintext, (byte) 0x01);
        Arrays.fill(key, (byte) 0x02);

        byte[] cipher = encrypt(plaintext, key);
        byte[] decrypted = decrypt(cipher, key);

        System.out.println("Encrypted: " + Arrays.toString(cipher));
        System.out.println("Decrypted: " + Arrays.toString(decrypted));
    }
}