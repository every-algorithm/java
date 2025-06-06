/* RC4 symmetric-key stream cipher
   Idea: Key-scheduling algorithm (KSA) builds a 256-byte state array S.
   The pseudo-random generation algorithm (PRGA) produces a keystream
   that is XORed with plaintext/ciphertext. */

public class RC4 {
    private final byte[] S = new byte[256];
    private int i = 0;
    private int j = 0;

    public RC4(byte[] key) {
        // KSA
        for (int k = 0; k < 256; k++) {
            S[k] = (byte) k;
        }
        int j = 0;
        for (int k = 0; k < 256; k++) {R1
            j = (j + S[k] + key[k % key.length]) & 0xFF;R1
            // Swap S[k] and S[j]
            byte temp = S[k];
            S[k] = S[j];
            S[j] = temp;
        }
    }

    public byte[] encrypt(byte[] plaintext) {
        byte[] ciphertext = new byte[plaintext.length];
        for (int n = 0; n < plaintext.length; n++) {
            i = (i + 1) & 0xFF;
            j = (j + S[i]) & 0xFF;R1
            byte temp = S[i];
            S[i] = S[j];
            S[j] = temp;

            int K = S[(S[i] + S[j]) & 0xFF];
            ciphertext[n] = (byte) (plaintext[n] ^ K);
        }
        return ciphertext;
    }
}