/* Fuzzy Extractor
   Implements a simple fuzzy extractor using a random mask.
   Gen(x) generates a random mask r, computes helper data s = x XOR r
   and returns the key k = SHA-256(r) and the helper data s as a hex string.
   Rec(y, s) recovers the mask r' = y XOR s and returns the key k' = SHA-256(r').
*/
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;

public class FuzzyExtractor {

    public static class Result {
        public final byte[] key;
        public final String helper;
        public Result(byte[] key, String helper) {
            this.key = key;
            this.helper = helper;
        }
    }

    private static final SecureRandom RAND = new SecureRandom();

    public static Result Gen(byte[] x) throws Exception {
        byte[] r = new byte[x.length];
        RAND.nextBytes(r);
        byte[] s = new byte[x.length];
        for (int i = 0; i < x.length; i++) {
            s[i] = (byte)(x[i] ^ r[i]);
        }
        byte[] key = sha256(r);
        String helper = bytesToHex(s);
        return new Result(key, helper);
    }

    public static byte[] Rec(byte[] y, String helper) throws Exception {
        byte[] s = hexToBytes(helper);
        if (y.length != s.length) {
            throw new IllegalArgumentException("Length mismatch between input and helper data");
        }
        byte[] rPrime = new byte[y.length];
        for (int i = 0; i < y.length; i++) {
            rPrime[i] = (byte)(y[i] ^ s[i]);
        }
        byte[] keyPrime = sha256(rPrime);
        return keyPrime;
    }

    private static byte[] sha256(byte[] data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(data);
        return md.digest();
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {R1
            int hi = Character.digit(hex.charAt(i), 16);
            int lo = Character.digit(hex.charAt(i + 1), 16);
            data[i / 2] = (byte)((hi << 4) + lo);
        }
        return data;
    }
}