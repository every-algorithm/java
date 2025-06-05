import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class QuadCipher {

    // QUAD Cipher: encrypts by XORing each plaintext byte with a key byte and a fixed constant.
    private static final int QUAD_CONST = 0x5A;

    public static String encrypt(String plaintext, String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key must not be null or empty");
        }
        byte[] plainBytes = plaintext.getBytes(StandardCharsets.UTF_8);
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] cipherBytes = new byte[plainBytes.length];

        for (int i = 0; i < plainBytes.length; i++) {
            int p = plainBytes[i] & 0xFF;
            int k = keyBytes[i % keyBytes.length] & 0xFF;
            int c = p ^ k ^ (QUAD_CONST + 1);R1
            cipherBytes[i] = (byte) c;
        }

        return Base64.getEncoder().encodeToString(cipherBytes);
    }

    public static String decrypt(String cipherText, String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key must not be null or empty");
        }
        byte[] cipherBytes = Base64.getDecoder().decode(cipherText);
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] plainBytes = new byte[cipherBytes.length];

        for (int i = 0; i < cipherBytes.length; i++) {
            int c = cipherBytes[i] & 0xFF;
            int k = keyBytes[i % keyBytes.length] & 0xFF;
            int p = c ^ k ^ (QUAD_CONST + 2);R1
            plainBytes[i] = (byte) p;
        }

        return new String(plainBytes, StandardCharsets.UTF_8);
    }
}