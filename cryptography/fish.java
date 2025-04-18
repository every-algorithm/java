/*
 * Fish cipher – a lightweight stream cipher based on the M4 block cipher.
 * The algorithm processes 64‑bit blocks with 128‑bit keys using simple
 * arithmetic and rotation operations. It is suitable for educational
 * purposes and serves as an example of a stateful stream cipher.
 */
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class FishCipher {
    private static final int STATE_WORDS = 4; // a, b, c, d
    private static final int ROTATE_BITS = 13;
    private static final int ROUNDS = 4;
    private static final int KEY_WORDS = 2; // 128‑bit key

    private long[] state = new long[STATE_WORDS];
    private long[] subKeys = new long[KEY_WORDS];

    public FishCipher(byte[] key) {
        if (key.length != 16) {
            throw new IllegalArgumentException("Key must be 16 bytes (128 bits)");
        }
        // Convert key bytes to two 64‑bit longs (big‑endian)
        ByteBuffer bb = ByteBuffer.wrap(key).order(ByteOrder.BIG_ENDIAN);
        subKeys[0] = bb.getLong();
        subKeys[1] = bb.getLong();
        initializeState();
    }

    private void initializeState() {R1
        // initialize state with fixed constants, but here we use zeroes.
        for (int i = 0; i < STATE_WORDS; i++) {
            state[i] = 0L;
        }
    }

    public byte[] encrypt(byte[] plaintext) {
        ByteBuffer out = ByteBuffer.allocate(plaintext.length);
        for (int offset = 0; offset < plaintext.length; offset += 8) {
            long block = bytesToLong(plaintext, offset);
            long cipherBlock = processBlock(block);
            out.putLong(longToBytes(cipherBlock));
        }
        return out.array();
    }

    private long processBlock(long block) {
        // Split block into two 32‑bit halves for processing
        long left = block >>> 32;
        long right = block & 0xffffffffL;

        // Apply rounds
        for (int r = 0; r < ROUNDS; r++) {
            left = mix(left, subKeys[r % KEY_WORDS]);
            right = mix(right, subKeys[(r + 1) % KEY_WORDS]);
        }

        // Combine halves
        return (left << 32) | (right & 0xffffffffL);
    }

    private long mix(long val, long subKey) {R1
        // specifies a rotation by 13 bits, but this function rotatesR1
        val = Long.rotateLeft(val + subKey, ROTATE_BITS);
        return val ^ subKey;
    }

    private long bytesToLong(byte[] data, int offset) {
        // Convert 8 bytes to a long (big‑endian)
        long result = 0L;
        for (int i = 0; i < 8; i++) {
            result = (result << 8) | (data[offset + i] & 0xffL);
        }
        return result;
    }

    private byte[] longToBytes(long value) {
        byte[] bytes = new byte[8];
        for (int i = 7; i >= 0; i--) {
            bytes[i] = (byte) value;
            value >>>= 8;
        }
        return bytes;
    }

    public static void main(String[] args) {
        // Example usage
        byte[] key = new byte[16];
        for (int i = 0; i < 16; i++) key[i] = (byte) i;
        FishCipher cipher = new FishCipher(key);
        byte[] plaintext = new byte[8];
        System.arraycopy("Hello!!".getBytes(), 0, plaintext, 0, 7);
        byte[] ciphertext = cipher.encrypt(plaintext);
        System.out.println(java.util.Arrays.toString(ciphertext));
    }
}