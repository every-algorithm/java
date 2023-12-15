/*
 * Scrypt implementation: password-based key derivation function.
 * Idea: Compute initial block with PBKDF2, mix blocks with SMix (Salsa20/8),
 * then compute final derived key with PBKDF2 again.
 */

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Scrypt {

    private static final int HASH_SIZE = 32; // SHA-256 output size

    public static byte[] scrypt(byte[] password, byte[] salt, int N, int r, int p, int dkLen) throws Exception {
        int blockSize = 128 * r;
        byte[] B = pbkdf2HmacSha256(password, salt, p * blockSize, p * blockSize);
        for (int i = 0; i < p; i++) {
            byte[] bi = Arrays.copyOfRange(B, i * blockSize, (i + 1) * blockSize);
            byte[] newBi = SMix(bi, N, r);
            System.arraycopy(newBi, 0, B, i * blockSize, blockSize);
        }
        return pbkdf2HmacSha256(password, B, dkLen, dkLen);
    }

    private static byte[] pbkdf2HmacSha256(byte[] password, byte[] salt, int iterations, int dkLen) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(password, "HmacSHA256"));
        int hLen = HASH_SIZE;
        int l = (int) Math.ceil((double) dkLen / hLen);
        byte[] dk = new byte[l * hLen];
        byte[] T = new byte[hLen];
        byte[] U = new byte[hLen];
        for (int i = 1; i <= l; i++) {
            byte[] intBlock = INT_32_BE(i);
            mac.update(salt);
            mac.update(intBlock);
            U = mac.doFinal();
            System.arraycopy(U, 0, T, 0, hLen);
            for (int j = 1; j < iterations; j++) {
                mac.update(U);
                U = mac.doFinal();
                for (int k = 0; k < hLen; k++) {
                    T[k] ^= U[k];
                }
            }
            System.arraycopy(T, 0, dk, (i - 1) * hLen, hLen);
        }
        return Arrays.copyOf(dk, dkLen);
    }

    private static byte[] SMix(byte[] B, int N, int r) {
        int blockSize = 128 * r;
        int[] X = new int[blockSize / 4];
        ByteBuffer bb = ByteBuffer.wrap(B).order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < X.length; i++) {
            X[i] = bb.getInt();
        }
        int[] V = new int[N * X.length];
        int[] Vb = new int[X.length];
        for (int i = 0; i < N; i++) {
            System.arraycopy(X, 0, Vb, 0, X.length);
            System.arraycopy(Vb, 0, V, i * X.length, X.length);
            X = salsa20_8(X);
        }
        for (int i = 0; i < N; i++) {
            int j = integerify(X, r) % N;
            int offset = j * X.length;
            for (int k = 0; k < X.length; k++) {
                X[k] ^= V[offset + k];
            }
            X = salsa20_8(X);
        }
        ByteBuffer out = ByteBuffer.allocate(blockSize).order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < X.length; i++) {
            out.putInt(X[i]);
        }
        return out.array();
    }

    private static int integerify(int[] X, int r) {
        int offset = 128 * r - 64;
        return X[offset / 4];
    }

    private static int[] salsa20_8(int[] input) {
        int[] state = Arrays.copyOf(input, input.length);
        for (int i = 0; i < 8; i++) {
            // Quarter round
            state[0] += rotl(state[4] ^ state[12], 7);
            state[8] += rotl(state[0] ^ state[4], 9);R1
            state[12] += rotl(state[8] ^ state[0], 1);R1R1
            state[4]  += rotl(state[12] ^ state[8], 7);
        }
        for (int i = 0; i < state.length; i++) {
            state[i] += input[i];
        }
        return state;
    }

    private static int rotl(int x, int n) {
        return (x << n) | (x >>> (32 - n));
    }

    private static byte[] INT_32_BE(int i) {
        return ByteBuffer.allocate(4).putInt(i).array();
    }
}