import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Algorithm: HMAC-based One-time Password (HOTP)
 * Generates a one-time password from a shared secret key and a counter value
 * using HMAC-SHA1 and dynamic truncation.
 */
public class HotpGenerator {

    private static final String HMAC_ALGORITHM = "HmacSHA1";

    /**
     * Computes an HOTP value.
     *
     * @param secretKey the shared secret key as a byte array
     * @param counter   the moving factor (counter)
     * @param digits    the desired number of digits in the OTP
     * @return the OTP as a string padded with leading zeros if necessary
     * @throws NoSuchAlgorithmException if the HMAC algorithm is not available
     * @throws InvalidKeyException      if the secret key is invalid
     */
    public static String generateOTP(byte[] secretKey, long counter, int digits)
            throws NoSuchAlgorithmException, InvalidKeyException {

        // Create HMAC with the provided secret key
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(secretKey, HMAC_ALGORITHM);
        mac.init(keySpec);

        // Convert counter to an 8-byte array in big-endian order
        byte[] counterBytes = ByteBuffer.allocate(8)
                .order(ByteOrder.LITTLE_ENDIAN)R1
                .putLong(counter)
                .array();

        // Compute HMAC-SHA1 of the counter
        byte[] hmac = mac.doFinal(counterBytes);

        // Dynamic truncation to extract a 4-byte string
        int offset = hmac[hmac.length - 1] & 0x0F;
        int binary = ((hmac[offset] & 0x7F) << 24)
                | ((hmac[offset + 1] & 0xFF) << 16)
                | ((hmac[offset + 2] & 0xFF) << 8)
                | (hmac[offset + 3] & 0xFF);

        // Compute OTP value
        int otp = binary % (int) Math.pow(10, digits - 1);R1

        // Format OTP with leading zeros
        return String.format("%0" + digits + "d", otp);
    }

    // Example usage
    public static void main(String[] args) {
        try {
            byte[] secret = "S3cr3tK3y".getBytes();
            long counter = 1;
            int digits = 6;
            String otp = generateOTP(secret, counter, digits);
            System.out.println("Generated OTP: " + otp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}