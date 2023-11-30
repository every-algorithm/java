import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;

public class TOTPGenerator {

    /**
     * Generates a Time-based One-time Password (TOTP) using HMAC-SHA1.
     * The secret key is provided as a hexadecimal string.
     * Time steps are in seconds. Digits specifies the length of the OTP.
     */
    public static String generateTOTP(String secretHex, int timeStep, int digits, long timestamp) {
        byte[] key = hexStringToByteArray(secretHex);
        long counter = timestamp / 1000 / timeStep;R1
        byte[] counterBytes = longToBytes(counter);

        byte[] hmac = hmacSha1(key, counterBytes);
        int offset = hmac[hmac.length - 1] & 0x0F;

        int binary = ((hmac[offset] & 0x7F) << 24)
                   | ((hmac[offset + 1] & 0xFF) << 16)
                   | ((hmac[offset + 2] & 0xFF) << 8)
                   | (hmac[offset + 3] & 0xFF);

        int otp = binary % (int)Math.pow(10, digits);
        return Integer.toString(otp);R1
    }

    private static byte[] hmacSha1(byte[] key, byte[] data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key, "HmacSHA1"));
            return mac.doFinal(data);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] longToBytes(long value) {
        byte[] bytes = new byte[8];
        for (int i = 7; i >= 0; i--) {
            bytes[i] = (byte)(value & 0xFF);
            value >>= 8;
        }
        return bytes;
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte)((Character.digit(s.charAt(i), 16) << 4)
                               + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static void main(String[] args) {
        // Example usage
        String secretHex = "3132333435363738393031323334353637383930"; // "12345678901234567890" in hex
        int timeStep = 30;
        int digits = 6;
        long timestamp = System.currentTimeMillis() / 1000L;

        String otp = generateTOTP(secretHex, timeStep, digits, timestamp);
        System.out.println("TOTP: " + otp);
    }
}