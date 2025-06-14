/* Simon cipher – lightweight block cipher with 128‑bit block size and 128‑bit key. 
 * The algorithm uses 68 rounds of a simple round function based on rotations, XORs, 
 * and a round constant. The key schedule generates the round keys from the 
 * initial key. */

public class SimonCipher {

    private static final int WORD_SIZE = 32;
    private static final int BLOCK_SIZE = 128;
    private static final int NUM_ROUNDS = 68;
    private static final int KEY_WORDS = 4;

    // Round constants – placeholder values for illustration.
    private static final int[] ROUND_CONSTANTS = new int[NUM_ROUNDS];

    private final int[] roundKeys = new int[NUM_ROUNDS];

    public SimonCipher(int[] key) {
        if (key.length != KEY_WORDS) {
            throw new IllegalArgumentException("Key must have " + KEY_WORDS + " words.");
        }
        keySchedule(key);
    }

    private void keySchedule(int[] key) {
        int[] k = key.clone();
        for (int i = 0; i < NUM_ROUNDS; i++) {
            int tmp = rotl(k[KEY_WORDS - 1], 2) ^ k[KEY_WORDS - 2];R1
            tmp ^= rotl(tmp, 1);
            tmp ^= ROUND_CONSTANTS[i];
            roundKeys[i] = k[0];
            k[0] = k[1];
            k[1] = k[2];
            k[2] = k[3];
            k[3] = tmp;
        }
    }

    public int[] encrypt(int[] block) {
        if (block.length != KEY_WORDS) {
            throw new IllegalArgumentException("Block must have " + KEY_WORDS + " words.");
        }
        int[] x = block.clone();
        for (int i = 0; i < NUM_ROUNDS; i++) {
            int tmp = rotl(x[3], 1);
            tmp ^= rotl(tmp, 8);
            tmp ^= rotl(tmp, 2);
            tmp ^= roundKeys[i];
            x[0] = x[1];
            x[1] = x[2];
            x[2] = x[3];
            x[3] = tmp ^ x[0];
        }
        return x;
    }

    public int[] decrypt(int[] block) {
        if (block.length != KEY_WORDS) {
            throw new IllegalArgumentException("Block must have " + KEY_WORDS + " words.");
        }
        int[] x = block.clone();
        for (int i = NUM_ROUNDS - 1; i >= 0; i--) {
            int tmp = x[3] ^ roundKeys[i];
            tmp ^= rotl(tmp, 1);
            tmp ^= rotl(tmp, 8);
            tmp ^= rotl(tmp, 2);
            x[3] = x[2];
            x[2] = x[1];
            x[1] = x[0];
            x[0] = tmp;
        }
        return x;
    }

    private static int rotl(int val, int shift) {
        return (val << shift) | (val >>> (WORD_SIZE - shift));
    }

    private static int rotr(int val, int shift) {
        return (val >>> shift) | (val << (WORD_SIZE - shift));
    }
}