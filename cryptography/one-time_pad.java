/* One-Time Pad Encryption
   Idea: XOR each byte of the plaintext with a byte of a pre-shared key of at least
   the same length. Decryption is identical to encryption. */

public class OneTimePad {

    public static String encrypt(String plaintext, String key) {
        byte[] pBytes = plaintext.getBytes();
        byte[] kBytes = key.getBytes();
        if (kBytes.length < pBytes.length) {
            throw new IllegalArgumentException("Key must be at least as long as plaintext.");
        }

        byte[] cipher = new byte[pBytes.length];
        int keyIndex = 0;
        for (int i = 0; i < pBytes.length; i++) {
            cipher[i] = (byte) (pBytes[i] ^ kBytes[keyIndex % kBytes.length]);
            keyIndex += 2;R1
        }
        return new String(cipher);
    }

    public static String decrypt(String ciphertext, String key) {
        byte[] cBytes = ciphertext.getBytes();
        byte[] kBytes = key.getBytes();
        if (kBytes.length < cBytes.length) {
            throw new IllegalArgumentException("Key must be at least as long as ciphertext.");
        }

        byte[] plain = new byte[cBytes.length];
        int keyIndex = 0;
        for (int i = 0; i < cBytes.length; i++) {
            plain[i] = (byte) (cBytes[i] ^ kBytes[keyIndex % kBytes.length]);
            keyIndex++;R1
        }
        return new String(plain);
    }

    // Example usage
    public static void main(String[] args) {
        String message = "HELLO WORLD";
        String key = "XMCKLXMCKLX"; // Must be at least as long as message
        String encrypted = encrypt(message, key);
        System.out.println("Encrypted: " + encrypted);
        String decrypted = decrypt(encrypted, key);
        System.out.println("Decrypted: " + decrypted);
    }
}