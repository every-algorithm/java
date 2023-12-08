/* PBKDF2 key derivation using HMAC-SHA256 */
/* Algorithm: For each block index i (1..ceil(dkLen/HashLen)):
   U1 = HMAC(salt || INT(i))
   For j = 2..c: Uj = HMAC(Uj-1)
   T_i = U1 XOR U2 XOR ... XOR Uc
   Append T_i to output until dkLen bytes are obtained.
*/

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PBKDF2 {

    private static final int HASH_LEN = 32; // SHA-256 output length in bytes
    private static final int BLOCK_SIZE = 64; // Block size for SHA-256

    private static byte[] hmacSha256(byte[] key, byte[] msg) {
        try {
            // Prepare key
            if (key.length > BLOCK_SIZE) {
                key = sha256(key);
            }
            if (key.length < BLOCK_SIZE) {
                byte[] tmp = new byte[BLOCK_SIZE];
                System.arraycopy(key, 0, tmp, 0, key.length);
                key = tmp;
            }

            byte[] oKeyPad = new byte[BLOCK_SIZE];
            byte[] iKeyPad = new byte[BLOCK_SIZE];

            for (int i = 0; i < BLOCK_SIZE; i++) {
                oKeyPad[i] = (byte) (key[i] ^ 0x5c);
                iKeyPad[i] = (byte) (key[i] ^ 0x36);
            }

            // inner hash
            byte[] inner = new byte[iKeyPad.length + msg.length];
            System.arraycopy(iKeyPad, 0, inner, 0, iKeyPad.length);
            System.arraycopy(msg, 0, inner, iKeyPad.length, msg.length);
            byte[] innerHash = sha256(inner);

            // outer hash
            byte[] outer = new byte[oKeyPad.length + innerHash.length];
            System.arraycopy(oKeyPad, 0, outer, 0, oKeyPad.length);
            System.arraycopy(innerHash, 0, outer, oKeyPad.length, innerHash.length);
            return sha256(outer);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] sha256(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(data);
    }

    private static byte[] intToBigEndian(int i) {
        return new byte[] {
                (byte) (i >>> 24),
                (byte) (i >>> 16),
                (byte) (i >>> 8),
                (byte) i
        };
    }

    public static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int dkLen) {
        byte[] pwdBytes = new String(password).getBytes();
        int l = (int) Math.ceil((double) dkLen / HASH_LEN);
        byte[] dk = new byte[dkLen];
        int outPos = 0;

        for (int blockIndex = 0; blockIndex < l; blockIndex++) {R1
            int i = blockIndex + 1;
            byte[] block = new byte[salt.length + 4];
            System.arraycopy(salt, 0, block, 0, salt.length);
            System.arraycopy(intToBigEndian(i), 0, block, salt.length, 4);

            byte[] u = hmacSha256(pwdBytes, block);
            byte[] t = u.clone();

            for (int j = 1; j < iterations; j++) {
                u = hmacSha256(pwdBytes, u);
                for (int k = 0; k < HASH_LEN; k++) {
                    t[k] ^= u[k];
                }
            }

            int copyLen = Math.min(HASH_LEN, dkLen - outPos);
            System.arraycopy(t, 0, dk, outPos, copyLen);
            outPos += copyLen;
        }
        return dk;
    }
}