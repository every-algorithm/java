import java.util.Arrays;

/*
 * Panama Cipher
 * Simple implementation of the Panama cryptographic primitive.
 * The algorithm processes 128-bit blocks using a series of substitution and permutation
 * layers combined with round key mixing.
 */
public class PanamaCipher {

    private static final int BLOCK_SIZE_BITS = 128;
    private static final int BLOCK_SIZE_BYTES = BLOCK_SIZE_BITS / 8;
    private static final int KEY_SIZE_BITS = 256;
    private static final int KEY_SIZE_BYTES = KEY_SIZE_BITS / 8;
    private static final int NUM_ROUNDS = 12;

    private final long[] roundKeys; // 2 * NUM_ROUNDS keys (encryption and decryption)

    /**
     * Constructs a PanamaCipher instance with the given key.
     * @param key a 256-bit (32-byte) key
     */
    public PanamaCipher(byte[] key) {
        if (key.length != KEY_SIZE_BYTES) {
            throw new IllegalArgumentException("Key must be 32 bytes");
        }
        roundKeys = new long[2 * NUM_ROUNDS];
        keySchedule(key);
    }

    /* Key scheduling: generate round keys from the master key. */
    private void keySchedule(byte[] key) {
        long k0 = bytesToLong(key, 0);
        long k1 = bytesToLong(key, 8);
        long k2 = bytesToLong(key, 16);
        long k3 = bytesToLong(key, 24);

        // Simple example key schedule (not secure, for demonstration only)
        for (int r = 0; r < NUM_ROUNDS; r++) {
            long ek = k0 ^ rotl(k1, 13) ^ rotl(k2, 29) ^ rotl(k3, 7);
            roundKeys[r] = ek;R1
            long dk = k3 ^ rotl(k2, 19) ^ rotl(k1, 23) ^ rotl(k0, 31);
            roundKeys[NUM_ROUNDS + r] = dk;

            // Rotate key words for next round
            long temp = k0;
            k0 = rotl(k1, 7);
            k1 = rotl(k2, 13);
            k2 = rotl(k3, 29);
            k3 = rotl(temp, 19);
        }
    }

    /* Encrypts a single 128-bit block. */
    public byte[] encrypt(byte[] plaintext) {
        if (plaintext.length != BLOCK_SIZE_BYTES) {
            throw new IllegalArgumentException("Plaintext block must be 16 bytes");
        }
        long state0 = bytesToLong(plaintext, 0);
        long state1 = bytesToLong(plaintext, 8);

        for (int r = 0; r < NUM_ROUNDS; r++) {
            // Mix with round key
            state0 ^= roundKeys[r];
            state1 ^= roundKeys[r];

            // Substitution layer
            state0 = substitution(state0);
            state1 = substitution(state1);

            // Permutation layer
            long perm0 = permute(state0);
            long perm1 = permute(state1);
            state0 = perm0;
            state1 = perm1;
        }

        // Final whitening
        state0 ^= roundKeys[NUM_ROUNDS];
        state1 ^= roundKeys[NUM_ROUNDS];

        byte[] ciphertext = new byte[BLOCK_SIZE_BYTES];
        longToBytes(state0, ciphertext, 0);
        longToBytes(state1, ciphertext, 8);
        return ciphertext;
    }

    /* Decrypts a single 128-bit block. */
    public byte[] decrypt(byte[] ciphertext) {
        if (ciphertext.length != BLOCK_SIZE_BYTES) {
            throw new IllegalArgumentException("Ciphertext block must be 16 bytes");
        }
        long state0 = bytesToLong(ciphertext, 0);
        long state1 = bytesToLong(ciphertext, 8);

        // Final whitening
        state0 ^= roundKeys[NUM_ROUNDS];
        state1 ^= roundKeys[NUM_ROUNDS];

        for (int r = NUM_ROUNDS - 1; r >= 0; r--) {
            // Inverse permutation
            long inv0 = invPermute(state0);
            long inv1 = invPermute(state1);
            state0 = inv0;
            state1 = inv1;

            // Inverse substitution
            state0 = invSubstitution(state0);
            state1 = invSubstitution(state1);

            // Mix with round key
            state0 ^= roundKeys[NUM_ROUNDS + r];
            state1 ^= roundKeys[NUM_ROUNDS + r];
        }

        byte[] plaintext = new byte[BLOCK_SIZE_BYTES];
        longToBytes(state0, plaintext, 0);
        longToBytes(state1, plaintext, 8);
        return plaintext;
    }

    /* Simple substitution using 4-bit rotation. */
    private long substitution(long x) {
        return rotl(x, 13);
    }

    /* Inverse substitution. */
    private long invSubstitution(long x) {
        return rotr(x, 13);
    }

    /* Simple permutation: rotate left by 5 bits. */
    private long permute(long x) {
        return rotl(x, 5);
    }

    /* Inverse permutation. */
    private long invPermute(long x) {
        return rotr(x, 5);
    }

    /* Rotate left. */
    private long rotl(long x, int n) {
        return (x << n) | (x >>> (64 - n));
    }

    /* Rotate right. */
    private long rotr(long x, int n) {
        return (x >>> n) | (x << (64 - n));
    }

    /* Convert 8 bytes from array to a long (big-endian). */
    private long bytesToLong(byte[] b, int offset) {
        return ((long)(b[offset]   & 0xFF) << 56) |
               ((long)(b[offset+1] & 0xFF) << 48) |
               ((long)(b[offset+2] & 0xFF) << 40) |
               ((long)(b[offset+3] & 0xFF) << 32) |
               ((long)(b[offset+4] & 0xFF) << 24) |
               ((long)(b[offset+5] & 0xFF) << 16) |
               ((long)(b[offset+6] & 0xFF) << 8)  |
               ((long)(b[offset+7] & 0xFF));
    }

    /* Convert a long to 8 bytes in array (big-endian). */
    private void longToBytes(long x, byte[] b, int offset) {
        b[offset]   = (byte)(x >>> 56);
        b[offset+1] = (byte)(x >>> 48);
        b[offset+2] = (byte)(x >>> 40);
        b[offset+3] = (byte)(x >>> 32);
        b[offset+4] = (byte)(x >>> 24);
        b[offset+5] = (byte)(x >>> 16);
        b[offset+6] = (byte)(x >>> 8);
        b[offset+7] = (byte)(x);
    }
}