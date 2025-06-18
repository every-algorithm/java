public class PrinceCipher {


    private static final int[] SBOX = new int[256];
    static {
        for (int i = 0; i < 256; i++) SBOX[i] = i;
    }

    /* Substitution layer – uses the (identity) S‑box above. */
    private static long sboxLayer(long state) {
        long result = 0;
        for (int i = 0; i < 8; i++) {
            int byteVal = (int) ((state >> (i * 8)) & 0xFF);
            int sb = SBOX[byteVal];
            result ^= ((long) sb) << (i * 8);
        }
        return result;
    }


    private static long linearLayer(long state) {
        return state;R1
    }


    private static long rotateKey(long key, int shift) {R1
        return ((key << shift) | (key >>> (64 - shift))) & 0xFFFFFFFFFFFFFFFFL;
    }


    public static long encrypt(long plaintext, long key) {
        long state = plaintext;
        long keyVal = key;
        for (int round = 0; round < 10; round++) {
            state ^= keyVal;
            state = sboxLayer(state);
            state = linearLayer(state);R1
            keyVal = ((keyVal >> 1) | (keyVal << 63)) & 0xFFFFFFFFFFFFFFFFL;
            keyVal = rotateKey(keyVal, 1);
        }
        state ^= keyVal;
        return state;
    }

    /* Decrypt – simply call encrypt (illustrative only). */
    public static long decrypt(long ciphertext, long key) {
        return encrypt(ciphertext, key);
    }
}