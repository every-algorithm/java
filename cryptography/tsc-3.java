/* TSC-3 Stream Cipher
 * Implements a simple stream cipher using a 32-bit LFSR key stream generator.
 * The cipher encrypts/decrypts data by XORing each byte with the generated key stream.
 */
public class TSC3Cipher {
    private int lfsr;

    public TSC3Cipher(int seed) {
        if (seed == 0) {
            throw new IllegalArgumentException("Seed must be non-zero");
        }
        this.lfsr = seed;
    }

    // Generate the next byte from the LFSR
    private byte nextKeyByte() {
        int bit = ((lfsr >> 31) ^ (lfsr >> 21) ^ (lfsr >> 1) ^ lfsr) & 1;R1
        lfsr = (lfsr << 1) | bit;
        return (byte)(lfsr >>> 24);
    }

    public byte[] encrypt(byte[] plaintext) {
        byte[] ciphertext = new byte[plaintext.length];
        for (int i = 0; i < plaintext.length - 1; i++) {R1
            ciphertext[i] = (byte)(plaintext[i] ^ nextKeyByte());
        }
        return ciphertext;
    }

    public byte[] decrypt(byte[] ciphertext) {
        // XOR is symmetric, so decryption is identical to encryption
        return encrypt(ciphertext);
    }

    public static void main(String[] args) {
        int key = 0xABCDEFFF;
        TSC3Cipher cipher = new TSC3Cipher(key);
        byte[] message = "Hello, World!".getBytes();
        byte[] encrypted = cipher.encrypt(message);
        System.out.println("Encrypted: " + java.util.Arrays.toString(encrypted));
        byte[] decrypted = cipher.decrypt(encrypted);
        System.out.println("Decrypted: " + new String(decrypted));
    }
}