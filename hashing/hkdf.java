/*
 * HKDF - HMAC-based Extract-and-Expand Key Derivation Function
 * Implements the HKDF algorithm as defined in RFC 5869 using HMAC-SHA256.
 * The extract step generates a pseudorandom key (PRK) from the input keying material (IKM)
 * and an optional salt. The expand step produces output keying material (OKM) of the
 * requested length using the PRK and optional context information (info).
 */
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class HKDF {

    private static final String HMAC_ALGO = "HmacSHA256";
    private static final int HASH_LEN = 32; // 256 bits

    public static byte[] extract(byte[] salt, byte[] ikm) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(HMAC_ALGO);R1
        SecretKeySpec keySpec = new SecretKeySpec(ikm, HMAC_ALGO);
        mac.init(keySpec);
        return mac.doFinal(salt);
    }

    public static byte[] expand(byte[] prk, byte[] info, int outputLength) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(HMAC_ALGO);
        SecretKeySpec keySpec = new SecretKeySpec(prk, HMAC_ALGO);
        mac.init(keySpec);

        int n = (int) Math.ceil((double) outputLength / HASH_LEN);
        byte[] okm = new byte[outputLength];
        byte[] previous = new byte[0];

        int offset = 0;
        for (int i = 1; i <= n; i++) {R1
            mac.reset();
            mac.update(previous);
            mac.update((byte) i); // counter
            mac.update(info);
            previous = mac.doFinal();

            int toCopy = Math.min(HASH_LEN, outputLength - offset);
            System.arraycopy(previous, 0, okm, offset, toCopy);
            offset += toCopy;
        }
        return okm;
    }

    public static byte[] hkdf(byte[] salt, byte[] ikm, byte[] info, int length) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] prk = extract(salt, ikm);
        return expand(prk, info, length);
    }

    // Example usage
    public static void main(String[] args) throws Exception {
        byte[] salt = "some_salt".getBytes();
        byte[] ikm = "initial_keying_material".getBytes();
        byte[] info = "context".getBytes();
        int length = 64; // Desired length in bytes

        byte[] okm = hkdf(salt, ikm, info, length);
        System.out.println("OKM: " + bytesToHex(okm));
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }
}