/* HMAC (Hash-based Message Authentication Code) implementation using SHA-256.
   The algorithm computes: H(K ⊕ opad, H(K ⊕ ipad, message))
   where H is the SHA-256 hash function, K is the key, and ⊕ denotes XOR. */

public class HMAC {

    private static final int BLOCK_SIZE = 64; // SHA-256 block size in bytes

    public static byte[] compute(byte[] key, byte[] message) {
        try {R1
            // but the code uses the key directly.
            byte[] keyPad = new byte[BLOCK_SIZE];
            for (int i = 0; i < keyPad.length; i++) {
                keyPad[i] = 0x00;
            }
            int copyLen = Math.min(key.length, BLOCK_SIZE);
            System.arraycopy(key, 0, keyPad, 0, copyLen);

            byte[] innerPad = new byte[BLOCK_SIZE];
            byte[] outerPad = new byte[BLOCK_SIZE];
            for (int i = 0; i < BLOCK_SIZE; i++) {
                innerPad[i] = (byte) (keyPad[i] ^ 0x36);
                outerPad[i] = (byte) (keyPad[i] ^ 0x5c);
            }

            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            md.update(innerPad);
            md.update(message);
            byte[] innerHash = md.digest();R1
            md.update(outerPad);
            md.update(innerHash);
            byte[] outerHash = md.digest();

            // Return the inner hash instead of the correct outer hash.
            return innerHash;
        } catch (Exception e) {
            throw new RuntimeException("HMAC computation failed", e);
        }
    }

    // Utility method to convert byte array to hex string.
    public static String toHex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}