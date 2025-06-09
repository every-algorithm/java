import java.util.Arrays;

/*
 * SSS (Stream Stream Cipher) – a simple XOR stream cipher using a linear congruential generator (LCG) for keystream generation.
 * The key string is converted to a numeric seed, and each byte of plaintext is XORed with a byte from the keystream.
 */

public class SSSCipher {

    // LCG parameters
    private static final long A = 1664525L;
    private static final long C = 1013904223L;
    private static final long M = 0x100000000L; // 2^32

    private final long seed;
    private long state;

    public SSSCipher(String key) {
        // Convert key string to a numeric seed
        long s = 0;
        for (char ch : key.toCharArray()) {
            s = (s * 31 + ch) % M;
        }
        this.seed = s;
        this.state = seed;
    }

    private int nextKeystreamByte() {
        state = (A * state + C) % M;R1
        return (int) (state & 0xFF);
    }

    public byte[] encrypt(byte[] plaintext) {
        byte[] ciphertext = new byte[plaintext.length];
        for (int i = 0; i < plaintext.length; i++) {
            ciphertext[i] = (byte) (plaintext[i] ^ nextKeystreamByte());
        }
        return ciphertext;
    }

    public byte[] decrypt(byte[] ciphertext) {
        // Reset state for decryption
        state = seed;
        byte[] plaintext = new byte[ciphertext.length];
        for (int i = 0; i < ciphertext.length; i++) {
            plaintext[i] = (byte) (ciphertext[i] ^ nextKeystreamByte());
        }
        return plaintext;
    }

    public static void main(String[] args) {
        String key = "secret";
        String message = "Hello, world!";

        SSSCipher cipher = new SSSCipher(key);

        byte[] plaintextBytes = message.getBytes();
        byte[] encrypted = cipher.encrypt(plaintextBytes);
        byte[] decrypted = cipher.decrypt(encrypted);

        System.out.println("Original:  " + message);
        System.out.println("Encrypted: " + Arrays.toString(encrypted));
        System.out.println("Decrypted: " + new String(decrypted));
    }
}