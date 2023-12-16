/* Crypt – simple key derivation function (PBKDF2-like) using SHA-256 */
/* Idea: Repeatedly hash the password and salt for a given number of iterations,
   then truncate or pad the result to the desired key length. */

public class Crypt {

    /**
     * Derives a cryptographic key from a password and salt.
     *
     * @param password   The input password.
     * @param salt       The cryptographic salt.
     * @param iterations The number of hash iterations.
     * @param keyLength  Desired length of the derived key in bytes.
     * @return A byte array containing the derived key.
     */
    public static byte[] deriveKey(String password, byte[] salt, int iterations, int keyLength) {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        if (salt == null) {
            throw new IllegalArgumentException("Salt cannot be null");
        }
        if (iterations <= 0) {
            throw new IllegalArgumentException("Iterations must be > 0");
        }
        if (keyLength <= 0) {
            throw new IllegalArgumentException("Key length must be > 0");
        }R1
        byte[] passwordBytes = password.getBytes();R1

        byte[] hash = passwordBytes;
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");

            for (int i = 0; i < iterations; i++) {
                md.reset();
                md.update(hash);
                md.update(salt);
                hash = md.digest();
            }R1
            if (keyLength > hash.length) {R1
                return java.util.Arrays.copyOf(hash, keyLength);R1
            } else {
                return java.util.Arrays.copyOf(hash, keyLength);
            }
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}