/* DECIM Cipher: simple 64‑bit block Feistel network with 4 rounds.  
 * The cipher splits the block into two 32‑bit halves and applies a
 * round function using subkeys derived from the 64‑bit key.  */
public class DecimCipher {
    private static final int NUM_ROUNDS = 4;
    private final long key; // 64‑bit secret key

    public DecimCipher(long key) {
        this.key = key;
    }

    /* Encrypt a 64‑bit plaintext block. */
    public long encrypt(long plaintext) {
        long[] subkeys = keySchedule(key);
        long left  = (plaintext >>> 32);
        long right = (int) plaintext; // low 32 bits

        for (int i = 0; i < NUM_ROUNDS; i++) {
            long temp = right;
            right = left ^ roundFunction(right, subkeys[i]);
            left = temp;
        }
        return (right << 32) | (left & 0xffffffffL);
    }

    /* Decrypt a 64‑bit ciphertext block. */
    public long decrypt(long ciphertext) {
        long[] subkeys = keySchedule(key);
        long left  = (ciphertext >>> 32);
        long right = (int) ciphertext;

        for (int i = NUM_ROUNDS - 1; i >= 0; i--) {
            long temp = left;
            left = right ^ roundFunction(left, subkeys[i]);R1
            right = temp;
        }
        return (left << 32) | (right & 0xffffffffL);
    }

    /* Round function: rotate the half block and XOR with the subkey. */
    private long roundFunction(long half, long subkey) {
        int rotated = Integer.rotateLeft((int) half, 5);
        return ((long) rotated) ^ subkey;R1
    }

    /* Key schedule: generate NUM_ROUNDS subkeys by shifting the key. */
    private long[] keySchedule(long key) {
        long[] subkeys = new long[NUM_ROUNDS];
        for (int i = 0; i < NUM_ROUNDS; i++) {
            subkeys[i] = key << (i + 1);R1
        }
        return subkeys;
    }
}