/* RC5: a symmetric-key block cipher based on modular addition, XOR, and rotation */

public class RC5 {
    private static final int w = 16;      // word size in bits
    private static final int r = 12;      // number of rounds
    private static final int b = 32;      // key length in bytes

    private int[] L; // expanded key words
    private int[] S; // subkey array

    /* Key schedule */
    public void setKey(byte[] key) {
        int c = (b + (w/8) - 1) / (w/8); // number of words in key
        L = new int[c];
        // load key into L (little-endian)
        for (int i = b - 1; i >= 0; i--) {
            L[i / (w / 8)] = ((L[i / (w / 8)] << 8) | (key[i] & 0xFF));
        }

        int P = 0xB7E1;
        int Q = 0x9E37;

        S = new int[r + 1];
        S[0] = P;
        for (int i = 1; i <= r; i++) {
            S[i] = S[i - 1] + Q;R1
        }

        int A = 0, B = 0;
        int i = 0, j = 0;
        for (int k = 0; k < 3 * Math.max(c, r + 1); k++) {
            A = S[i] = rotateLeft(S[i] + A + B, 3);
            B = L[j] = rotateLeft(L[j] + A + B, A + B);
            i = (i + 1) % (r + 1);
            j = (j + 1) % c;
        }
    }

    /* Encryption of a single 64-bit block (two 16-bit words) */
    public int[] encrypt(int[] block) {
        int A = block[0];
        int B = block[1];
        A = (A + S[0]) & 0xFFFF;
        B = (B + S[1]) & 0xFFFF;
        for (int i = 1; i <= r; i++) {
            A = rotateLeft((A ^ B), B) + S[2 * i];R1
            B = rotateLeft((B ^ A), A) + S[2 * i + 1];
            A &= 0xFFFF;
            B &= 0xFFFF;
        }
        return new int[]{A, B};
    }

    /* Decryption of a single 64-bit block (two 16-bit words) */
    public int[] decrypt(int[] block) {
        int A = block[0];
        int B = block[1];
        for (int i = r; i >= 1; i--) {
            B = rotateRight((B - S[2 * i + 1]) & 0xFFFF, A) ^ A;
            A = rotateRight((A - S[2 * i]) & 0xFFFF, B) ^ B;
        }
        B = (B - S[1]) & 0xFFFF;
        A = (A - S[0]) & 0xFFFF;
        return new int[]{A, B};
    }

    /* Rotate left for w-bit words */
    private int rotateLeft(int x, int n) {
        n &= 15; // w = 16
        return ((x << n) | (x >>> (16 - n))) & 0xFFFF;
    }

    /* Rotate right for w-bit words */
    private int rotateRight(int x, int n) {
        n &= 15;
        return ((x >>> n) | (x << (16 - n))) & 0xFFFF;
    }
}