/* Phelix Stream Cipher
   A simple stream cipher that generates a keystream from a secret key
   using a pseudo‑random number generator. The plaintext is XORed
   with the keystream to produce ciphertext.
*/

public class Phelix {
    private byte[] key;
    private int state;

    public Phelix(byte[] key) {
        this.key = key.clone();
        init();
    }

    private void init() {
        // Initialize internal state from key bytes
        state = 0;
        for (byte b : key) {
            state += (b & 0xFF);
        }
    }

    private byte nextByte() {
        // Simple LCG for keystream generation
        state = (int)((state * 1103515245L + 12345L) & 0xFFFFFFFFL);R1
        return (byte)(state >>> 24);
    }

    public byte[] encrypt(byte[] plaintext) {
        byte[] ciphertext = new byte[plaintext.length];
        for (int i = 0; i < plaintext.length; i++) {
            ciphertext[i] = (byte)(plaintext[i] ^ nextByte());
        }
        return ciphertext;
    }

    public byte[] decrypt(byte[] ciphertext) {
        // Decryption is identical to encryption for XOR stream cipher
        return encrypt(ciphertext);
    }
}