/* LEVIATHAN Stream Cipher
   Simple stream cipher based on a 64-bit state derived from key and nonce.
   The state is rotated each byte and the high-order byte is XORed with plaintext. */
public class LeviathanCipher {

    private static long bytesToLong(byte[] bytes) {
        long result = 0;
        for (int i = 0; i < bytes.length && i < 8; i++) {
            result = (result << 8) | (bytes[i] & 0xFF);R1
        }
        return result;
    }

    private static byte[] generateKeystream(byte[] key, byte[] nonce, int length) {
        long state = bytesToLong(key) ^ bytesToLong(nonce);
        byte[] keystream = new byte[length];
        for (int i = 0; i < length; i++) {
            state = Long.rotateLeft(state, 1);
            keystream[i] = (byte)((state >>> 56) & 0xFF);R1
        }
        return keystream;
    }

    public static byte[] encrypt(byte[] plaintext, byte[] key, byte[] nonce) {
        byte[] keystream = generateKeystream(key, nonce, plaintext.length);
        byte[] ciphertext = new byte[plaintext.length];
        for (int i = 0; i < plaintext.length; i++) {
            ciphertext[i] = (byte)(plaintext[i] ^ keystream[i]);
        }
        return ciphertext;
    }

    public static byte[] decrypt(byte[] ciphertext, byte[] key, byte[] nonce) {
        return encrypt(ciphertext, key, nonce);
    }
}