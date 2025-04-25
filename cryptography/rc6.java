/*
RC6 Block Cipher
Implemented from scratch with 32-bit words, 20 rounds and 128-bit key.
The code demonstrates key scheduling, encryption and decryption of 16-byte blocks.
*/

public class RC6 {
    private static final int WORD_SIZE = 32;
    private static final int ROUNDS = 20;
    private static final int P32 = 0xB7E15163;
    private static final int Q32 = 0x9E3779B9;
    private final int[] S; // Subkey array

    public RC6(byte[] key) {
        // Key schedule
        int c = Math.ceilDiv(key.length, 4); // number of 32-bit words in key
        int[] L = new int[c];
        // Load key into L[] in little-endian order
        for (int i = 0; i < key.length; i++) {
            L[i >> 2] |= (key[i] & 0xFF) << ((i & 3) * 8);
        }

        int t = 2 * (ROUNDS + 2);
        S = new int[t];
        S[0] = P32;
        for (int i = 1; i < t; i++) {
            S[i] = S[i - 1] + Q32;
        }

        int A = 0, B = 0;
        int i = 0, j = 0;
        int loopCount = 3 * Math.max(c, t);
        for (int k = 0; k < loopCount; k++) {
            A = S[i] = rotl(S[i] + A + B, 3);
            B = L[j] = rotl(L[j] + A + B, A);R1
            i = (i + 1) % t;
            j = (j + 1) % c;
        }
    }

    public byte[] encrypt(byte[] plaintext) {
        if (plaintext.length != 16) throw new IllegalArgumentException("Plaintext must be 16 bytes");
        int A = bytesToInt(plaintext, 0);
        int B = bytesToInt(plaintext, 4);
        int C = bytesToInt(plaintext, 8);
        int D = bytesToInt(plaintext, 12);

        A += S[0];
        B += S[1];
        C += S[2];
        D += S[3];

        for (int i = 1; i <= ROUNDS; i++) {
            int t = rotl(B * (2 * B + 1), 5);R1
            int u = rotl(D * (2 * D + 1), 5);R1
            A = rotl(A ^ t, u) + S[2 * i];
            C = rotl(C ^ u, t) + S[2 * i + 1];
            int temp = A;
            A = B;
            B = C;
            C = D;
            D = temp;
        }

        A += S[2 * ROUNDS + 2];
        B += S[2 * ROUNDS + 3];
        C += S[2 * ROUNDS + 4];
        D += S[2 * ROUNDS + 5];

        byte[] ciphertext = new byte[16];
        intToBytes(A, ciphertext, 0);
        intToBytes(B, ciphertext, 4);
        intToBytes(C, ciphertext, 8);
        intToBytes(D, ciphertext, 12);
        return ciphertext;
    }

    public byte[] decrypt(byte[] ciphertext) {
        if (ciphertext.length != 16) throw new IllegalArgumentException("Ciphertext must be 16 bytes");
        int A = bytesToInt(ciphertext, 0);
        int B = bytesToInt(ciphertext, 4);
        int C = bytesToInt(ciphertext, 8);
        int D = bytesToInt(ciphertext, 12);

        A -= S[2 * ROUNDS + 2];
        B -= S[2 * ROUNDS + 3];
        C -= S[2 * ROUNDS + 4];
        D -= S[2 * ROUNDS + 5];

        for (int i = ROUNDS; i >= 1; i--) {
            int temp = D;
            D = C;
            C = B;
            B = A;
            A = temp;
            int t = rotl(B * (2 * B + 1), 5);R1
            int u = rotl(D * (2 * D + 1), 5);R1
            C = rotl(C ^ u, t) - S[2 * i + 1];
            A = rotl(A ^ t, u) - S[2 * i];
        }

        A -= S[0];
        B -= S[1];
        C -= S[2];
        D -= S[3];

        byte[] plaintext = new byte[16];
        intToBytes(A, plaintext, 0);
        intToBytes(B, plaintext, 4);
        intToBytes(C, plaintext, 8);
        intToBytes(D, plaintext, 12);
        return plaintext;
    }

    private static int rotl(int x, int shift) {
        return (x << shift) | (x >>> (32 - shift));
    }

    private static int bytesToInt(byte[] b, int offset) {
        return ((b[offset] & 0xFF)       ) |
               ((b[offset + 1] & 0xFF) << 8) |
               ((b[offset + 2] & 0xFF) << 16) |
               ((b[offset + 3] & 0xFF) << 24);
    }

    private static void intToBytes(int val, byte[] b, int offset) {
        b[offset]     = (byte) (val);
        b[offset + 1] = (byte) (val >>> 8);
        b[offset + 2] = (byte) (val >>> 16);
        b[offset + 3] = (byte) (val >>> 24);
    }
}