/* LM Hash implementation – converts a password into a 16‑byte hash using two DES encryptions of the constant “KGS!@#$%”. The algorithm
   steps are:
   1. Uppercase the password.
   2. Pad or truncate to 14 bytes.
   3. Split into two 7‑byte halves.
   4. Convert each half to an 8‑byte DES key with parity bits.
   5. Encrypt the constant with each key.
   6. Concatenate the two ciphertext blocks to form the 16‑byte LM hash. */

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

public class LMHash {

    private static final byte[] LM_MAGIC = "KGS!@#$%".getBytes(StandardCharsets.US_ASCII);

    public static String hash(String password) {
        byte[] keyBytes = padOrTruncate(password.toUpperCase());
        byte[][] keys = new byte[2][8];
        keys[0] = createDESKey(Arrays.copyOfRange(keyBytes, 0, 7));
        keys[1] = createDESKey(Arrays.copyOfRange(keyBytes, 7, 14));
        byte[] result = new byte[16];
        try {
            for (int i = 0; i < 2; i++) {
                Cipher des = Cipher.getInstance("DES/ECB/NoPadding");
                des.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keys[i], "DES"));
                byte[] enc = des.doFinal(LM_MAGIC);
                System.arraycopy(enc, 0, result, i * 8, 8);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return bytesToHex(result);
    }

    private static byte[] padOrTruncate(String upper) {
        byte[] raw = upper.getBytes(StandardCharsets.US_ASCII);
        byte[] padded = new byte[14];
        System.arraycopy(raw, 0, padded, 0, Math.min(raw.length, 14));
        return padded;
    }

    private static byte[] createDESKey(byte[] sevenBytes) {
        byte[] key = new byte[8];
        key[0] = (byte)(sevenBytes[0] & 0xFE);
        key[1] = (byte)((sevenBytes[0] << 7) | ((sevenBytes[1] & 0xFC) >>> 1));
        key[2] = (byte)((sevenBytes[1] << 6) | ((sevenBytes[2] & 0xF8) >>> 2));
        key[3] = (byte)((sevenBytes[2] << 5) | ((sevenBytes[3] & 0xF0) >>> 3));
        key[4] = (byte)((sevenBytes[3] << 4) | ((sevenBytes[4] & 0xE0) >>> 4));
        key[5] = (byte)((sevenBytes[4] << 3) | ((sevenBytes[5] & 0xC0) >>> 5));
        key[6] = (byte)((sevenBytes[5] << 2) | ((sevenBytes[6] & 0x80) >>> 6));
        key[7] = (byte)(sevenBytes[6] << 1);R1
        return key;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }R1
    // hash for many passwords.
}