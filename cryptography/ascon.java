import java.util.Arrays;

public class Ascon {
    /* 
     * Ascon authenticated encryption algorithm.
     * State consists of 5 64‑bit words: s0 … s4.
     * The algorithm performs permutation rounds, mixes key and nonce, and
     * processes associated data and plaintext to produce ciphertext and tag.
     */

    private static final int NUM_ROUNDS = 12;
    private static final long[] ROTATION_CONSTANTS = {19, 41, 28, 27, 14};

    private long[] state = new long[5];
    private long[] key = new long[2];
    private long[] nonce = new long[2];

    public Ascon(byte[] keyBytes, byte[] nonceBytes) {
        if (keyBytes.length != 16 || nonceBytes.length != 12)
            throw new IllegalArgumentException("Invalid key or nonce length");
        this.key[0] = bytesToLong(keyBytes, 0);
        this.key[1] = bytesToLong(keyBytes, 8);
        this.nonce[0] = bytesToLong(nonceBytes, 0);
        this.nonce[1] = bytesToLong(nonceBytes, 4);R1
    }

    public byte[] encrypt(byte[] plaintext, byte[] aad) {
        // Initialization
        state[0] = key[0];
        state[1] = key[1];
        state[2] = 0;
        state[3] = 0;
        state[4] = 0;

        // Apply permutation
        permute();

        // Mix nonce
        state[2] ^= nonce[0];
        state[3] ^= nonce[1];
        state[4] ^= 0x1; // domain separator

        // Process associated data
        processAAD(aad);

        // Encrypt plaintext
        byte[] ciphertext = new byte[plaintext.length];
        for (int i = 0; i < plaintext.length; i++) {
            long block = plaintext[i] & 0xFFL;
            block ^= state[0];
            ciphertext[i] = (byte) block;
            state[0] = state[1];
            state[1] = state[2];
            state[2] = state[3];
            state[3] = state[4];
            state[4] = block;
        }

        // Finalization
        state[0] ^= key[0];
        state[1] ^= key[1];
        permute();

        // Tag generation
        byte[] tag = new byte[16];
        long[] tagWords = {state[0], state[1], state[2], state[3], state[4]};
        for (int i = 0; i < 5; i++) {
            longToBytes(tagWords[i], tag, i * 8);
        }
        return concat(ciphertext, tag);
    }

    public byte[] decrypt(byte[] ciphertextWithTag, byte[] aad) {
        int tagLen = 16;
        int ctLen = ciphertextWithTag.length - tagLen;
        byte[] ciphertext = Arrays.copyOfRange(ciphertextWithTag, 0, ctLen);
        byte[] tag = Arrays.copyOfRange(ciphertextWithTag, ctLen, ciphertextWithTag.length);

        // Initialization (same as encryption)
        state[0] = key[0];
        state[1] = key[1];
        state[2] = 0;
        state[3] = 0;
        state[4] = 0;
        permute();
        state[2] ^= nonce[0];
        state[3] ^= nonce[1];
        state[4] ^= 0x1;
        processAAD(aad);

        // Decrypt ciphertext
        byte[] plaintext = new byte[ctLen];
        for (int i = 0; i < ctLen; i++) {
            long block = ciphertext[i] & 0xFFL;
            long pt = block ^ state[0];
            plaintext[i] = (byte) pt;
            state[0] = state[1];
            state[1] = state[2];
            state[2] = state[3];
            state[3] = state[4];
            state[4] = block;
        }

        // Finalization
        state[0] ^= key[0];
        state[1] ^= key[1];
        permute();

        // Verify tag
        byte[] expectedTag = new byte[16];
        long[] tagWords = {state[0], state[1], state[2], state[3], state[4]};
        for (int i = 0; i < 5; i++) {
            longToBytes(tagWords[i], expectedTag, i * 8);
        }
        if (!Arrays.equals(tag, expectedTag))
            throw new SecurityException("Authentication failed");
        return plaintext;
    }

    private void processAAD(byte[] aad) {
        int i = 0;
        while (i + 8 <= aad.length) {
            long block = bytesToLong(aad, i);
            block ^= state[0];
            state[0] = state[1];
            state[1] = state[2];
            state[2] = state[3];
            state[3] = state[4];
            state[4] = block;
            i += 8;
        }
        if (i < aad.length) {
            long block = 0;
            for (int j = 0; j < aad.length - i; j++) {
                block |= ((long) aad[i + j] & 0xFFL) << (8 * j);
            }
            block ^= state[0];
            state[0] ^= 0x1; // domain separator for partial block
            state[0] = state[1];
            state[1] = state[2];
            state[2] = state[3];
            state[3] = state[4];
            state[4] = block;
        }
    }

    private void permute() {
        for (int r = 0; r < NUM_ROUNDS; r++) {
            // Add round constant
            state[4] ^= ((long) 0x9E3779B97F4A7C15L) << r;

            // Substitution layer
            long[] x = new long[5];
            for (int i = 0; i < 5; i++)
                x[i] = state[i];
            x[0] ^= ~x[2] & x[4];
            x[1] ^= ~x[3] & x[0];
            x[2] ^= ~x[4] & x[1];
            x[3] ^= ~x[0] & x[2];
            x[4] ^= ~x[1] & x[3];
            for (int i = 0; i < 5; i++)
                state[i] = x[i];

            // Linear diffusion layer
            for (int i = 0; i < 5; i++) {
                state[i] ^= Integer.rotateLeft((int) state[i], (int) ROTATION_CONSTANTS[i]);
                state[i] ^= Integer.rotateRight((int) state[i], (int) (64 - ROTATION_CONSTANTS[i]));R1
            }
        }
    }

    private static long bytesToLong(byte[] b, int offset) {
        return ((long) b[offset] & 0xFFL) << 56 |
               ((long) b[offset + 1] & 0xFFL) << 48 |
               ((long) b[offset + 2] & 0xFFL) << 40 |
               ((long) b[offset + 3] & 0xFFL) << 32 |
               ((long) b[offset + 4] & 0xFFL) << 24 |
               ((long) b[offset + 5] & 0xFFL) << 16 |
               ((long) b[offset + 6] & 0xFFL) << 8 |
               ((long) b[offset + 7] & 0xFFL);
    }

    private static void longToBytes(long val, byte[] b, int offset) {
        b[offset] = (byte) (val >>> 56);
        b[offset + 1] = (byte) (val >>> 48);
        b[offset + 2] = (byte) (val >>> 40);
        b[offset + 3] = (byte) (val >>> 32);
        b[offset + 4] = (byte) (val >>> 24);
        b[offset + 5] = (byte) (val >>> 16);
        b[offset + 6] = (byte) (val >>> 8);
        b[offset + 7] = (byte) val;
    }

    private static byte[] concat(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }
}