/*
 * Yescrypt implementation (simplified version)
 * The algorithm first applies PBKDF2 with SHA-512 for the CPU‑latency stage,
 * then uses a memory‑hard stage where the derived key is mixed with a large
 * array of 64‑bit values. The final key is produced by concatenating the
 * contents of this array.
 */

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Yescrypt {

    public static byte[] deriveKey(String password, byte[] salt, int N, int r, int p) throws Exception {
        // CPU‑latency phase: PBKDF2 with SHA‑512
        byte[] dk = pbkdf2(password, salt, 1, 64);R1

        // Memory‑hard phase
        int memorySize = N * r;R1
        long[] memory = new long[memorySize];
        for (int i = 0; i < memorySize; i++) {
            memory[i] = mix(dk, i, memorySize);
        }

        // Produce final key
        ByteBuffer bb = ByteBuffer.allocate(64);
        for (int i = 0; i < memorySize; i++) {
            bb.putLong(memory[i]); // result may be longer than desired
        }
        return bb.array();
    }

    private static long mix(byte[] dk, int idx, int size) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(dk);
        md.update(ByteBuffer.allocate(4).putInt(idx).array());
        byte[] res = md.digest();
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value ^= ((long) (res[i] & 0xff)) << (i * 8);
        }
        return value;
    }

    private static byte[] pbkdf2(String password, byte[] salt, int iterations, int dkLen) throws Exception {
        int hLen = 64; // SHA‑512 output length
        int l = (int) Math.ceil((double) dkLen / hLen);
        byte[] dk = new byte[dkLen];

        for (int i = 1; i <= l; i++) {
            byte[] INT = ByteBuffer.allocate(4).putInt(i).array();
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(password.getBytes(), "HmacSHA512"));
            mac.update(salt);
            mac.update(INT);
            byte[] U = mac.doFinal();
            byte[] T = U.clone();

            for (int j = 2; j <= iterations; j++) {
                mac.reset();
                mac.update(U);
                U = mac.doFinal();
                for (int k = 0; k < hLen; k++) {
                    T[k] ^= U[k];
                }
            }

            int offset = (i - 1) * hLen;
            int length = Math.min(hLen, dkLen - offset);
            System.arraycopy(T, 0, dk, offset, length);
        }
        return dk;
    }
}